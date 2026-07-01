package com.vibecoder.vibecore.economy;

import com.vibecoder.vibecore.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final EconomyManager economyManager;

    public PayCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TextUtil.colorize("&cThis command can only be used by players."));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(TextUtil.colorize("&cUsage: /pay <player> <amount>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(TextUtil.colorize("&cPlayer not found: " + args[0]));
            return true;
        }

        if (target.equals(player)) {
            sender.sendMessage(TextUtil.colorize("&cYou cannot pay yourself!"));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(TextUtil.colorize("&cInvalid amount: " + args[1]));
            return true;
        }

        if (amount <= 0) {
            sender.sendMessage(TextUtil.colorize("&cAmount must be positive!"));
            return true;
        }

        boolean success = economyManager.transfer(player.getUniqueId(), target.getUniqueId(), amount);
        if (!success) {
            sender.sendMessage(TextUtil.colorize("&cTransfer failed. Check your balance."));
            return true;
        }

        String formattedAmount = TextUtil.formatCurrency(amount);
        sender.sendMessage(TextUtil.colorize(
                "&aYou paid &e" + target.getName() + " " + formattedAmount + "&a!"));
        target.sendMessage(TextUtil.colorize(
                "&aYou received &e" + formattedAmount + "&a from &e" + player.getName() + "&a!"));
        return true;
    }
}
