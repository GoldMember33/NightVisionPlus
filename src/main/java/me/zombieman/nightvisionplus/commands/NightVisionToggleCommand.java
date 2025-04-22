package me.zombieman.nightvisionplus.commands;

import me.zombieman.nightvisionplus.NightVisionPlus;
import me.zombieman.nightvisionplus.effects.PlayerEffects;
import me.zombieman.nightvisionplus.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NightVisionToggleCommand implements CommandExecutor, TabCompleter {

    private final NightVisionPlus plugin;

    public NightVisionToggleCommand(NightVisionPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtils.deserialize(plugin.getConfig().getString("canNotRunCommandFromConsole")));
            return true;
        }

        PlayerEffects pEffects = new PlayerEffects();

        if (player.hasPermission("nightvisionplus.command.apply")) {
            boolean wantsEnable;

            if (args.length == 1) {
                boolean toggleChoice;
                try {
                    toggleChoice = Boolean.parseBoolean(args[0]);
                } catch (Exception e) {
                    player.sendMessage(ColorUtils.deserialize(plugin.getConfig().getString("toggleErrorMessage")));
                    return false;
                }

                plugin.getPlayerManager().savePlayerData(player, toggleChoice);
                pEffects.pEffect(player, toggleChoice);

                wantsEnable = toggleChoice;
                player.sendMessage(ColorUtils.deserialize(plugin.getConfig().getString("toggledMessage")
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
                completions.add("true");
                completions.add("false");
            }
        }

        String lastArg = args[args.length - 1].toUpperCase();
        return completions.stream().filter(s -> s.toUpperCase().startsWith(lastArg.toUpperCase())).collect(Collectors.toList());
    }
}
