package com.vibecoder.vibecore.commands;

import com.vibecoder.vibecore.config.PluginConfig;
import com.vibecoder.vibecore.manager.CooldownManager;
import com.vibecoder.vibecore.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand implements CommandExecutor {

    private final CooldownManager cooldownManager;
    private final PluginConfig config;

    public FeedCommand(CooldownManager cooldownManager, PluginConfig config) {
        this.cooldownManager = cooldownManager;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage(TextUtil.colorize("&cUsage: /feed <player>"));
            return true;
        }

        Player target;
        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(TextUtil.colorize("&cPlayer not found: " + args[0]));
                return true;
            }
        } else {
            target = (Player) sender;
        }

        if (sender instanceof Player player) {
            if (cooldownManager.isOnCooldown(player.getUniqueId(), "feed")) {
                long remaining = cooldownManager.getRemainingCooldown(player.getUniqueId(), "feed");
                sender.sendMessage(TextUtil.colorize(
                        "&cYou must wait " + TextUtil.formatDuration(remaining) + " before feeding again."));
                return true;
            }
            cooldownManager.setCooldown(player.getUniqueId(), "feed", config.getFeedCooldownSeconds());
        }

        target.setFoodLevel(20);
        target.setSaturation(20f);
        target.setExhaustion(0f);

        if (sender.equals(target)) {
            sender.sendMessage(TextUtil.colorize("&aYou have been fed!"));
        } else {
            sender.sendMessage(TextUtil.colorize("&aYou fed &e" + target.getName() + "&a!"));
            target.sendMessage(TextUtil.colorize("&aYou have been fed by &e" + sender.getName() + "&a!"));
        }

        return true;
    }
}
