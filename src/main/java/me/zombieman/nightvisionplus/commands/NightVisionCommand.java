package me.zombieman.nightvisionplus.commands;

import me.zombieman.nightvisionplus.NightVisionPlus;
import me.zombieman.nightvisionplus.data.PlayerData;
import me.zombieman.nightvisionplus.effects.PlayerEffects;
import me.zombieman.nightvisionplus.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NightVisionCommand implements CommandExecutor {
    private final NightVisionPlus plugin;

    public NightVisionCommand(NightVisionPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtils.deserialize(plugin.getConfig().getString("canNotRunCommandFromConsole")));
            return true;
        }

        PlayerEffects pEffects = new PlayerEffects();

        if (player.hasPermission("nightvisionplus.command.apply")) {
            FileConfiguration playerDataConfig = PlayerData.getPlayerDataConfig(plugin, player.getUniqueId());
            UUID pUUID = player.getUniqueId();
            boolean wantsEnable = playerDataConfig.getBoolean("nightVision.player." + pUUID + ".nvp", false);

            if (args.length >= 1) {
                String targetName = args[0];
                Player target = Bukkit.getPlayerExact(targetName);

                if (target == null) {
                    player.sendMessage(ColorUtils.deserialize(plugin.getConfig().getString("targetPlayerIsNotOnline")
                                       .replace("%target-player%", targetName)));
                } else {
                    UUID tUUID = target.getUniqueId();

                    if (player.hasPermission("nightvisionplus.command.apply.other")) {
                        boolean TargetWantsEnable = PlayerData.getPlayerDataConfig(plugin, player.getUniqueId()).getBoolean("nightVision.player." + tUUID + ".nvp", false);
                        if (!TargetWantsEnable) {
                            playerDataConfig.set("nightVision.player." + tUUID + ".nvp", true);
                            playerDataConfig.set("nightVision.player." + tUUID + ".ign", targetName);
                            PlayerData.savePlayerData(plugin, target);
                            pEffects.pEffect(target, true);
                            player.sendMessage(ColorUtils.deserialize(plugin.getConfig().getString("enableMessageOthers")
                                    .replace("%player%", player.getName())
                                    .replace("%target-player%", targetName)));
                            if (target != player) {
                                target.sendMessage(ColorUtils.deserialize(plugin.getConfig().getString("enableMessageToOther")
                                        .replace("%player%", player.getName())
                                        .replace("%target-player%", targetName)));
                            }

                        } else {
                            playerDataConfig.set("nightVision.player." + tUUID + ".nvp", false);
                            playerDataConfig.set("nightVision.player." + tUUID + ".ign", targetName);
                            PlayerData.savePlayerData(plugin, target);
                            pEffects.pEffect(target, false);
                            player.sendMessage(ColorUtils.deserialize(plugin.getConfig().getString("disableMessageOthers")
                                    .replace("%player%", player.getName())
                                    .replace("%target-player%", targetName)));
                            if (target != player) {
                                target.sendMessage(ColorUtils.deserialize(plugin.getConfig().getString("disableMessageToOther")
                                        .replace("%player%", player.getName())
                                        .replace("%target-player%", targetName)));
                            }
                        }
                    } else {
                        player.sendMessage(ColorUtils.deserialize(plugin.getConfig().getString("noPermissionToApplyEffectToOthers")));
                    }
                }

            } else {
                if (!wantsEnable) {
                    pEffects.pEffect(player, true);
//                    pData.savePlayerData(player, true);
                    playerDataConfig.set("nightVision.player." + pUUID + ".nvp", true);
                    playerDataConfig.set("nightVision.player." + pUUID + ".ign", player.getName());
                    PlayerData.savePlayerData(plugin, player);
                    player.sendMessage(ColorUtils.deserialize(plugin.getConfig().getString("enableMessage")));
                } else {
                    pEffects.pEffect(player, false);
                    playerDataConfig.set("nightVision.player." + pUUID + ".nvp", false);
                    playerDataConfig.set("nightVision.player." + pUUID + ".ign", player.getName());
                    PlayerData.savePlayerData(plugin, player);
                    player.sendMessage(ColorUtils.deserialize(plugin.getConfig().getString("disableMessage")));
                }
            }
        } else {
            player.sendMessage(ColorUtils.deserialize(plugin.getConfig().getString("noPermissionToPerformCommands")));
        }

        return true;
    }
}

