package com.vibecoder.vibecore.economy;

import com.vibecoder.vibecore.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {

    private final EconomyManager economyManager;

    public BalanceCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(TextUtil.colorize("&cPlayer not found: " + args[0]));
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(TextUtil.colorize("&cUsage: /balance <player>"));
                return true;
            }
            target = (Player) sender;
        }

        if (!economyManager.hasAccount(target.getUniqueId())) {
            sender.sendMessage(TextUtil.colorize("&cThat player does not have an account."));
            return true;
        }

        double balance = economyManager.getBalance(target.getUniqueId());
        if (sender.equals(target)) {
            sender.sendMessage(TextUtil.colorize(
                    "&aYour balance: &e" + TextUtil.formatCurrency(balance)));
        } else {
            sender.sendMessage(TextUtil.colorize(
                    "&e" + target.getName() + "&a's balance: &e" + TextUtil.formatCurrency(balance)));
        }
        return true;
    }
}
