package com.vibecoder.vibecore.commands;

import com.vibecoder.vibecore.config.PluginConfig;
import com.vibecoder.vibecore.manager.CooldownManager;
import com.vibecoder.vibecore.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand implements CommandExecutor {

    private final CooldownManager cooldownManager;
    private final PluginConfig config;

    public HealCommand(CooldownManager cooldownManager, PluginConfig config) {
        this.cooldownManager = cooldownManager;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage(TextUtil.colorize("&cUsage: /heal <player>"));
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
            if (cooldownManager.isOnCooldown(player.getUniqueId(), "heal")) {
                long remaining = cooldownManager.getRemainingCooldown(player.getUniqueId(), "heal");
                sender.sendMessage(TextUtil.colorize(
                        "&cYou must wait " + TextUtil.formatDuration(remaining) + " before healing again."));
                return true;
            }
            cooldownManager.setCooldown(player.getUniqueId(), "heal", config.getHealCooldownSeconds());
        }

        double maxHealth = target.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue();
        target.setHealth(maxHealth);
        target.setFoodLevel(20);
        target.setSaturation(20f);
        target.setFireTicks(0);

        if (sender.equals(target)) {
            sender.sendMessage(TextUtil.colorize("&aYou have been healed!"));
        } else {
            sender.sendMessage(TextUtil.colorize("&aYou healed &e" + target.getName() + "&a!"));
            target.sendMessage(TextUtil.colorize("&aYou have been healed by &e" + sender.getName() + "&a!"));
        }

        return true;
    }
}
