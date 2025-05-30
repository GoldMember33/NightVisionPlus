package me.zombieman.nightvisionplus;

import me.zombieman.nightvisionplus.commands.MainCommands;
import me.zombieman.nightvisionplus.commands.NightVisionCommand;
import me.zombieman.nightvisionplus.commands.NightVisionToggleCommand;
import me.zombieman.nightvisionplus.data.PlayerData;
import me.zombieman.nightvisionplus.data.PlayerManager;
import me.zombieman.nightvisionplus.effects.PlayerEffects;
import me.zombieman.nightvisionplus.listeners.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class NightVisionPlus extends JavaPlugin {

    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        // Plugin startup logic

        PlayerData.initDataFolder(this);

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            getLogger().info("Config file not found, creating...");
            saveResource("config.yml", false);
        }

        // Commands

        getCommand("night-vision").setExecutor(new NightVisionCommand(this));
        getCommand("nightvisionplus").setExecutor(new MainCommands(this));
        getCommand("night-vision-toggle").setExecutor(new NightVisionToggleCommand(this));

        new PlayerData();
        this.playerManager = new PlayerManager(this);
        new PlayerEffects();

        new JoinListener(this);
        new DeathListener(this);
        new MilkConsumeEvent(this);
        new IgnoredCommands(this);
        new PlayerListener(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
