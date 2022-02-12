package tech.simonwalker.blockinfection.Utils;

import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import tech.simonwalker.blockinfection.BlockInfectionPlugin;

public class Utils {
    /**
     * Sends a chat message. Supports color chat with the '§' symbol (Alt 21).
     * @param sender
     * @param text
     */
    public static void msg(CommandSender sender, String text, Object... args) {
        msgNoPrefix(sender, BlockInfectionPlugin.getPrefix() + " " + text, args);
    }

    public static void msgNoPrefix(CommandSender sender, String text, Object... args) {
        text = text.replaceAll("&", "\\&");
        text = text.replaceAll("§", "&");
        text = String.format(text, args);
        var colored = ChatColor.translateAlternateColorCodes('&', text);

        sender.sendMessage(colored);
    }

    public static boolean isBlockFaceConnected(BlockFace face) {
        return switch (face) {
            case UP, DOWN, NORTH, EAST, SOUTH, WEST -> true;
            default -> false;
        };
    }
}
