package me.zombieman.nightvisionplus.commands;

import me.zombieman.nightvisionplus.NightVisionPlus;
import me.zombieman.nightvisionplus.data.PlayerData;
import me.zombieman.nightvisionplus.effects.PlayerEffects;
import me.zombieman.nightvisionplus.utils.ColorUtils;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MainCommands implements CommandExecutor, TabCompleter {
    private final NightVisionPlus plugin;

    public MainCommands(NightVisionPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtils.deserialize(plugin.getConfig().getString("canNotRunCommandFromPlayer")));
            return true;
        }

        UUID pUUID = player.getUniqueId();
        FileConfiguration playerDataConfig = PlayerData.getPlayerDataConfig(plugin, pUUID);
        PlayerEffects pEffects = new PlayerEffects();

        if (player.hasPermission("nightvisionplus.command.admin")) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    long startTime = System.currentTimeMillis();
                    try {
                        plugin.reloadConfig();
                        PlayerData.reloadAllPlayerData(plugin);
                        long endTime = System.currentTimeMillis();
                        long elapsedTime = endTime - startTime;

                        player.sendMessage(ColorUtils.deserialize(plugin.getConfig().getString("pluginReloaded")
                                .replace("%elapsed-time%", String.valueOf(elapsedTime))));

                    } catch (Exception e) {
                        plugin.getComponentLogger().error(ColorUtils.deserialize(plugin.getConfig().getString("pluginReloadError")
                                .replace("%error%", e.getMessage())));
                    }

                } else if (args[0].equalsIgnoreCase("reset")) {
                    if (args[1].equalsIgnoreCase("config.yml")) {
                        long startTime = System.currentTimeMillis();
                        plugin.saveResource("config.yml", true);
                        plugin.reloadConfig();
                        long endTime = System.currentTimeMillis();
                        long time = endTime - startTime + 1;
                        player.sendMessage(ColorUtils.deserialize(plugin.getConfig().getString("configResetSuccessfully")
                                .replace("%config%", args[1])
                                .replace("%elapsed-time%", String.valueOf(time))));
                    } else if (args[1].equalsIgnoreCase("playerData")) {
                        long startTime = System.currentTimeMillis();
                        PlayerData.removeAllPlayerFiles(plugin);
                        long endTime = System.currentTimeMillis();
                        long time = endTime - startTime + 1;
                        player.sendMessage(ColorUtils.deserialize(plugin.getConfig().getString("configResetSuccessfully")
                                .replace("%config%", args[1])
                                .replace("%elapsed-time%", String.valueOf(time))));
                    } else if (args[1].equalsIgnoreCase("all")) {
                        long startTime = System.currentTimeMillis();
                        plugin.saveResource("config.yml", true);
                        plugin.reloadConfig();
                        PlayerData.removeAllPlayerFiles(plugin);
                        long endTime = System.currentTimeMillis();
                        long time = endTime - startTime + 1;
                        player.sendMessage(ColorUtils.deserialize(plugin.getConfig().getString("allConfigsResetSuccessfully")
                                .replace("%elapsed-time%", String.valueOf(time))));
                    } else {
                        player.sendMessage(ColorUtils.deserialize(plugin.getConfig().getString("nvpCommandResetUsage")));
                    }
                } else {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("""
                            <#7289da><strikethrough>                                            </strikethrough>
                            <#7289da><bold>Click Here To Get Support!</bold>
                            <#7289da><strikethrough>                                            </strikethrough>""")
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/SuypvRBa4c"))
                            .hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize("<#7289da><bold>Click Here To Get Support!"))));
                }
            } else {
                boolean wantsEnable = playerDataConfig.getBoolean("nightVision.player." + pUUID + ".nvp", false);
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

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        Player player = (Player) sender;

        if (args.length == 1) {
            if (player.hasPermission("nightvisionplus.command.admin")) {
                completions.add("reload");
                completions.add("reset");
                completions.add("support");
            }
        } else if (args.length == 2) {
            if (player.hasPermission("nightvisionplus.command.admin")) {
                if (args[0].equalsIgnoreCase("reset")) {
                    completions.add("PlayerData");
                    completions.add("config.yml");
                    completions.add("all");
                }
            }
        }

        String lastArg = args[args.length - 1].toUpperCase();
        return completions.stream().filter(s -> s.toUpperCase().startsWith(lastArg.toUpperCase())).collect(Collectors.toList());
    }
}
