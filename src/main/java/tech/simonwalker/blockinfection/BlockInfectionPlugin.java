package tech.simonwalker.blockinfection;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Singleton
 */
public final class BlockInfectionPlugin extends JavaPlugin {
    private static BlockInfectionPlugin inst;

    /**
     * DO NOT CALL
     *
     * Use getInst() instead.
     */
    public BlockInfectionPlugin() throws Exception {
        if (inst != null) {
            throw new Exception("Use getInst() instead.");
        }

        inst = this;
    }

    /**
     * @return NULL until spigot initializes the plugin
     */
    public static BlockInfectionPlugin getInst() {
        return inst;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        getLogger().info("hey bruhchiggity!");
        registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        getLogger().info("cya brigga?");
    }

    public static void log(String msg) {
        getInst().getLogger().info(msg);
    }

    public static String getPrefix() {
        return "§d[§5B§8l§5o§8c§5k§8I§5n§8f§d]";
    }

    private void registerCommands() {
        var rootCmd = new RootCommand();
        getCommand("blockInfection").setExecutor(rootCmd);
        getCommand("blockInfection").setTabCompleter(rootCmd);
    }
}
