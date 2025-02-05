package me.zombieman.nightvisionplus.listeners;

import me.zombieman.nightvisionplus.NightVisionPlus;
import me.zombieman.nightvisionplus.data.PlayerData;
import me.zombieman.nightvisionplus.effects.PlayerEffects;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class MilkConsumeEvent implements Listener {
    private final NightVisionPlus plugin;

    public MilkConsumeEvent(NightVisionPlus plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMilkConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType().name().equals("MILK_BUCKET")) {
            Player p = event.getPlayer();
            UUID pUUID = p.getUniqueId();
            FileConfiguration playerDataConfig = PlayerData.getPlayerDataConfig(plugin, pUUID);
            boolean wantsEnable = playerDataConfig.getBoolean("nightVision.player." + pUUID + ".nvp", false);
            if (wantsEnable) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        PlayerEffects playerEffects = new PlayerEffects();
                        playerEffects.pEffect(p, true);
                    }
                }.runTaskLater(plugin, 1);
            }
        }
    }
}