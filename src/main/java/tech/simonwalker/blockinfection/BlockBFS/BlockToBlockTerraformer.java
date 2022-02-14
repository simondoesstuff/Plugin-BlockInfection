package tech.simonwalker.blockinfection.BlockBFS;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import tech.simonwalker.blockinfection.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class BlockToBlockTerraformer extends AdjacentBlockBFS {
    private final Material materialFrom, materialTo;

    public BlockToBlockTerraformer(Plugin plugin, Location origin, Material from, Material to) {
        super(plugin, origin);

        this.materialFrom = from;
        this.materialTo = to;
    }

    @Override
    protected void transformBlock(Block node) {
        node.setType(materialTo);
    }

    @Override
    protected boolean expansionAllowed(Block node) {
        return node.getType() == materialFrom;
    }

    @Override
    protected int expansionDelay(Block node) {
        return ThreadLocalRandom.current().nextInt(1, 200);
    }
}
