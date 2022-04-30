package com.thepwo.blockcounter;

import com.thepwo.blockcounter.commands.BlocksTopCommand;
import com.thepwo.blockcounter.database.Database;
import com.thepwo.blockcounter.database.DatabaseType;
import com.thepwo.blockcounter.database.impl.yaml.YamlDatabase;
import com.thepwo.blockcounter.listeners.BlockListener;
import com.thepwo.blockcounter.listeners.ConnectionListener;
import com.thepwo.blockcounter.utils.string.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlockCounter extends JavaPlugin {
    private static BlockCounter plugin;
    private Database database;

    public static BlockCounter getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        loadDatabase();
        hookGangsPlus();
        registerListeners();
        registerCommands();
    }

    private void loadDatabase() {
        DatabaseType databaseType = DatabaseType.valueOf(getConfig().getString("database.type").toUpperCase());

        switch (databaseType) {
            case YAML -> this.database = new YamlDatabase(this);
        }

        this.database.connect().whenComplete((success, throwable) -> {
            if (success) {
                log(String.format("&2[BlockCounter] &aSuccessfully established connection with %s database!", databaseType));
            } else {
                log("&4[BlockCounter] &cCould not connect to database! Disabling...");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        });
    }

    private void hookGangsPlus() {
        if (Bukkit.getPluginManager().getPlugin("GangsPlus") == null) {
            log("&4[BlockCounter] &cGangsPlus not installed! Disabling...");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void registerListeners() {
        (new ConnectionListener()).register();
        (new BlockListener()).register();
    }

    private void registerCommands() {
        (new BlocksTopCommand()).register();
    }

    @Override
    public void onDisable() {
        log("&4[BlockCounter] &cTerminating...");

        this.database.close();
        log("&4[BlockCounter] &cClosed database connection");

        log("&4[BlockCounter] &cTerminated");
    }

    public Database getDatabase() {
        return database;
    }

    public String getMessage(String identifier) {
        return StringUtils.colorize(getConfig().getStringList("messages." + identifier));
    }

    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage(StringUtils.colorize(message));
    }
}
