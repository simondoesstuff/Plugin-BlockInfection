package tech.simonwalker.blockinfection.BlockBFS;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

public class BlockDestroyerTerraformer extends BlockToBlockTerraformer {
    public BlockDestroyerTerraformer(Plugin plugin, Location origin) {
        super(plugin, origin, null, Material.AIR);
    }


    private void particle(Color color, Location location) {
        var particle = Particle.REDSTONE.builder();

        var x = location.getX();
        var z = location.getZ();
        location.add(x > 0 ? 0.5 : -0.5, 1.0, z > 0 ? 0.5 : -0.5);

        particle.color(color);
        particle.location(location);
        particle.receivers(30);
        particle.count(3);

        particle.spawn();
    }

    @Override
    protected void transformBlock(Block node) {
        super.transformBlock(node);

        particle(Color.GREEN, node.getLocation());
    }

    @Override
    protected boolean expansionAllowed(Block node) {
        super.expansionAllowed(node);

        particle(Color.YELLOW, node.getLocation());
        return !node.getType().isAir() && node.isSolid();
    }
}
