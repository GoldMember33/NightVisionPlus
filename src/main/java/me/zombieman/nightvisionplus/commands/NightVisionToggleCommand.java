package me.zombieman.nightvisionplus.commands;

import me.zombieman.nightvisionplus.NightVisionPlus;
import me.zombieman.nightvisionplus.data.PlayerData;
import me.zombieman.nightvisionplus.effects.PlayerEffects;
import me.zombieman.nightvisionplus.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NightVisionToggleCommand implements CommandExecutor, TabCompleter {

    private final NightVisionPlus plugin;

    public NightVisionToggleCommand(NightVisionPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorUtils.color("&cYou can't run this command from the console"));
            return true;
        }

        Player player = (Player) sender;
        String playerName = player.getName();

        PlayerEffects pEffects = new PlayerEffects();

        if (player.hasPermission("nightvisionplus.command.apply")) {
            FileConfiguration playerDataConfig = PlayerData.getPlayerDataConfig(plugin, player.getUniqueId());
            UUID pUUID = player.getUniqueId();
            boolean wantsEnable = playerDataConfig.getBoolean("nightVision.player." + pUUID + ".nvp", false);

            if (args.length == 1) {
                wantsEnable = !wantsEnable;
                playerDataConfig.set("nightVision.player." + pUUID + ".nvp", wantsEnable);
                playerDataConfig.set("nightVision.player." + pUUID + ".ign", playerName);
                PlayerData.savePlayerData(plugin, player);
                pEffects.pEffect(player, wantsEnable);
                player.sendMessage(ColorUtils.color(plugin.getConfig().getString("toggledMessage")
                        .replace("%player%", player.getName())
                        .replace("%status%", wantsEnable ?
                                plugin.getConfig().getString("nightVisionEnabled") :
                                plugin.getConfig().getString("nightVisionDisabled"))));
            }

            if (args.length == 2) {

                boolean toggleChoice;
                try {
                    toggleChoice = Boolean.parseBoolean(args[1]);
                } catch (Exception e) {
                    player.sendMessage(ColorUtils.color(plugin.getConfig().getString("toggleErrorMessage")));
                    return false;
                }

                if (toggleChoice) {
                    playerDataConfig.set("nightVision.player." + pUUID + ".nvp", true);
                    playerDataConfig.set("nightVision.player." + pUUID + ".ign", playerName);
                    PlayerData.savePlayerData(plugin, player);
                    pEffects.pEffect(player, true);

                } else {

                    playerDataConfig.set("nightVision.player." + pUUID + ".nvp", false);
                    playerDataConfig.set("nightVision.player." + pUUID + ".ign", playerName);
                    PlayerData.savePlayerData(plugin, player);
                    pEffects.pEffect(player, false);

                }

                wantsEnable = toggleChoice;
                player.sendMessage(ColorUtils.color(plugin.getConfig().getString("toggledMessage")
                        .replace("%player%", player.getName())
                        .replace("%status%", wantsEnable ?
                                plugin.getConfig().getString("nightVisionEnabled") :
                                plugin.getConfig().getString("nightVisionDisabled"))));
                return true;

            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                                                @NotNull String[] args) {

        List<String> completions = new ArrayList<>();

        Player player = (Player) sender;

        if (args.length == 1) {
            if (player.hasPermission("nightvisionplus.command.apply")) {
                completions.add("toggle");
            }
        } else if (args.length == 2) {
            if (player.hasPermission("nightvisionplus.command.apply")) {
                completions.add("true");
                completions.add("false");
            }
        }

        String lastArg = args[args.length - 1].toUpperCase();
        return completions.stream().filter(s -> s.toUpperCase().startsWith(lastArg.toUpperCase())).collect(Collectors.toList());
    }
}
