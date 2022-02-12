package tech.simonwalker.blockinfection;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.simonwalker.blockinfection.BlockBFS.BlockDestroyerTerraformer;
import tech.simonwalker.blockinfection.BlockBFS.BlockToBlockTerraformer;

import java.util.List;

import static tech.simonwalker.blockinfection.Utils.Utils.msg;

public class RootCommand implements SimplifiedCommandExecutor, SimplifiedTabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        return switch (args.length) {
            case 1 -> List.of("start");
            case 2 -> List.of("destroy", "transform");
            case 3 -> args[1].equalsIgnoreCase("transform") ? List.of("fromBlock") : null;
            case 4 -> args[1].equalsIgnoreCase("transform") ? List.of("toBlock") : null;
            default -> null;
        };
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        switch (args[0]) {
            case "start":
                if (!(sender instanceof Player)) {
                    msg(sender, "§4You must be player.");
                    return;
                }

                if (args.length == 1) {
                    msg(sender, "§4You include \"destroy\" or \"transform\".");
                    return;
                }

                if (args[1].equalsIgnoreCase("destroy")) {
                    startInfection(((Player) sender).getPlayer());
                    return;
                }

                if (args[1].equalsIgnoreCase("transform")) {
                    if (args.length < 4) {
                        msg(sender, "§4You must include a \"from\" block and a \"to\" block.");
                        return;
                    }

                    var fromBlockArg = args[2];
                    var toBlockArg = args[3];

                    Material fromBlock, toBlock;

                    try {
                        fromBlock = Material.valueOf(fromBlockArg.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        msg(sender, "§4\"from\" block not recognized.");
                        return;
                    }

                    try {
                        toBlock = Material.valueOf(toBlockArg.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        msg(sender, "§4\"to\" block not recognized.");
                        return;
                    }

                    transformationInfection((Player) sender, fromBlock, toBlock);
                    return;
                }
            default:
                msg(sender, "§4/bi start");
        }
    }

    private void transformationInfection(Player player, Material from, Material to) {
        msg(player, "§aAs you wish.");

        var blockTarget = player.getTargetBlock(20);

        if (blockTarget == null) {
            msg(player, "§4You must be looking at a block");
            return;
        }

        var BFS = new BlockToBlockTerraformer(BlockInfectionPlugin.getInst(), blockTarget.getLocation(), from, to);
        BFS.start();
    }

    private void startInfection(Player player) {
        msg(player, "§aI am so sorry.");

        var blockTarget = player.getTargetBlock(20);

        if (blockTarget == null) {
            msg(player, "§4You must be looking at a block");
            return;
        }

        var BFS = new BlockDestroyerTerraformer(BlockInfectionPlugin.getInst(), blockTarget.getLocation());
        BFS.start();
    }
}
