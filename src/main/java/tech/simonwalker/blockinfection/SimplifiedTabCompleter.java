package tech.simonwalker.blockinfection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface SimplifiedTabCompleter extends TabCompleter {
    @Nullable
    default List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return onTabComplete(sender, args);
    }

    @Nullable
    List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args);
}
