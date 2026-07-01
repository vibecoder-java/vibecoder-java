package com.vibecoder.vibecore.listeners;

import com.vibecoder.vibecore.config.PluginConfig;
import com.vibecoder.vibecore.economy.EconomyManager;
import com.vibecoder.vibecore.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private final PluginConfig config;
    private final EconomyManager economyManager;

    public PlayerDeathListener(PluginConfig config, EconomyManager economyManager) {
        this.config = config;
        this.economyManager = economyManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!config.isDeathPenaltyEnabled()) {
            return;
        }

        Player player = event.getEntity();
        if (!economyManager.hasAccount(player.getUniqueId())) {
            return;
        }

        double penalty = economyManager.applyPenalty(
                player.getUniqueId(), config.getDeathPenaltyPercent());

        if (penalty > 0) {
            player.sendMessage(TextUtil.colorize(
                    "&cYou lost &e" + TextUtil.formatCurrency(penalty) + "&c due to death!"));
        }
    }
}
