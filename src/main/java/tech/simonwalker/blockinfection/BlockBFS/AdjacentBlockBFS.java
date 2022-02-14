package tech.simonwalker.blockinfection.BlockBFS;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import tech.simonwalker.blockinfection.Utils.Utils;

import java.util.Arrays;

public abstract class AdjacentBlockBFS extends BlockBFS {
    public AdjacentBlockBFS(Plugin plugin, Location origin) {
        super(plugin, origin);
    }

    @Override
    protected void onNodeDiscovered(Vector node) {
        super.onNodeDiscovered(node);
        transformBlock(node.toLocation(getWorld()).getBlock());
    }

    @Override
    protected Route[] expandNode(Vector node) {
        return Arrays.stream(BlockFace.values())
                // get all connected block faces
                .filter(Utils::isBlockFaceConnected)
                // filter routes by those allowed in routeAllowed()
                .filter(face -> expansionAllowed(
                        node.clone().add(face.getDirection()).toLocation(getWorld()).getBlock()
                ))
                // map the new routes associated with each face
                .map(face -> {
                    var routeVector = node.clone().add(face.getDirection());
                    var routeBlock = routeVector.toLocation(getWorld()).getBlock();

                    return new Route(
                            routeVector,
                            // let expansionDelay() decide the delay
                            expansionDelay(routeBlock)
                    );
                })
                .toList().toArray(new Route[0]);
    }

    protected abstract boolean expansionAllowed(Block node);

    protected abstract int expansionDelay(Block node);

    protected abstract void transformBlock(Block node);
}
