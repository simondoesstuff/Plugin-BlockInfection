package tech.simonwalker.blockinfection.BlockBFS;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import tech.simonwalker.blockinfection.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class WorldSwapTerraformer extends AdjacentBlockBFS {
    @Getter
    private final World otherWorld = findOtherWorld();
    @Getter
    private final double spreadResistance;
    @Getter
    private final int spreadLimit;
    @Getter
    private final int baseSpeed;

    public WorldSwapTerraformer(Plugin plugin, Location origin, double spreadResistance, int spreadLimit, int baseSpeed) {
        super(plugin, origin);

        if (spreadResistance < 0) {
            throw new IllegalArgumentException("The spreadResistance can not be negative.");
        }

        this.spreadResistance = spreadResistance + 1;
        this.spreadLimit = spreadLimit;
        this.baseSpeed = baseSpeed;
    }

    public WorldSwapTerraformer(Plugin plugin, Location origin) {
        this(plugin, origin, 0.5, 10000, 100);
    }

    // should only be called once by constructor
    private World findOtherWorld() {
        String targetName = getWorld().getName();
        World.Environment targetEnv;

        if (getWorld().getEnvironment() == World.Environment.NETHER) {
            targetEnv = World.Environment.NORMAL;

            if (getWorld().getName().endsWith("_nether")) {
                targetName = targetName.substring(0, targetName.length() - "_nether".length());
            }
        } else {
            targetEnv = World.Environment.NETHER;
            targetName = targetName + "_nether";
        }

        // required for lambdas
        final String finalTargetName = targetName;

        // first try to find a world matching by name
        var found = Bukkit.getWorlds().stream()
                .filter(world -> world.getName().equals(finalTargetName))
                .findFirst();

        // if it failed, try finding a world that's the target environment
        if (found.isEmpty()) {
            found = Bukkit.getWorlds().stream()
                    .filter(world -> world.getEnvironment() == targetEnv)
                    .findAny();

            // if it still failed, find ANY other world
            if (found.isEmpty()) {
                found = Bukkit.getWorlds().stream()
                        .filter(world -> !world.getName().equals(getWorld().getName()))
                        .findAny();

                // if it still failed, we can not continue
                if (found.isEmpty()) {
                    throw new IllegalArgumentException("Corresponding world not found.");
                }
            }
        }

        return found.get();
    }

    private Block correspondingBlock(Block node) {
        return getOtherWorld().getBlockAt(node.getLocation());
    }

    @Override
    protected boolean expansionAllowed(Block node) {
        // once we discover up to the spreadLimit, halt spread.
        if (amountDiscovered() >= spreadLimit) return false;

        // the infection can only spread to blocks that "exist" in both worlds
        return expansionAllowedPerWorld(node, true) && expansionAllowedPerWorld(node, false);
    }

    private boolean expansionAllowedPerWorld(Block node, boolean otherWorld) {
        // the node in the specified world. (The node param is really just a vector param)
        Block block = otherWorld ? correspondingBlock(node) : node;
        return true;
//        return block.isSolid() && !block.getType().isAir();
    }

    @Override
    protected void transformBlock(Block node) {
        // swap the block between dimensions

        var data1 = node.getBlockData().clone();
        var data2 = correspondingBlock(node).getBlockData().clone();

        // set block 1
        node.setType(data2.getMaterial());
        node.setBlockData(data2);

        // set block 2
        correspondingBlock(node).setType(data1.getMaterial());
        correspondingBlock(node).setBlockData(data1);
    }

    @Override
    protected int expansionDelay(Block node) {
        double factor = 1 - amountDiscovered() / (double) spreadLimit / spreadResistance;
        int upperBound = (int) Math.round(baseSpeed * factor);
        int lowerBound = 1;

        if (upperBound == 0) {
            return lowerBound;
        }

        return ThreadLocalRandom.current().nextInt(lowerBound, upperBound);
    }
}
