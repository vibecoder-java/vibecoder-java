package com.vibecoder.vibecore.listeners;

import com.vibecoder.vibecore.config.PluginConfig;
import com.vibecoder.vibecore.economy.EconomyManager;
import com.vibecoder.vibecore.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final PluginConfig config;
    private final EconomyManager economyManager;

    public PlayerJoinListener(PluginConfig config, EconomyManager economyManager) {
        this.config = config;
        this.economyManager = economyManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        boolean isNew = economyManager.createAccount(player.getUniqueId());

        if (config.isWelcomeMessageEnabled()) {
            String message = TextUtil.replacePlaceholders(
                    config.getWelcomeMessage(),
                    "player", player.getName());
            player.sendMessage(TextUtil.colorize(message));
        }

        if (isNew) {
            player.sendMessage(TextUtil.colorize(
                    "&aYour starting balance is &e"
                            + TextUtil.formatCurrency(economyManager.getStartingBalance()) + "&a!"));
        }
    }
}
