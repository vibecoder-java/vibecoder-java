package com.vibecoder.vibecore.listeners;

import com.vibecoder.vibecore.config.PluginConfig;
import com.vibecoder.vibecore.economy.EconomyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerDeathListenerTest {

    @Mock
    private PluginConfig config;

    @Mock
    private EconomyManager economyManager;

    @Mock
    private Player player;

    @Mock
    private PlayerDeathEvent event;

    private PlayerDeathListener listener;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        listener = new PlayerDeathListener(config, economyManager);
        playerId = UUID.randomUUID();
        lenient().when(event.getEntity()).thenReturn(player);
        lenient().when(player.getUniqueId()).thenReturn(playerId);
    }

    @Test
    void onPlayerDeath_penaltyEnabled_appliesPenalty() {
        when(config.isDeathPenaltyEnabled()).thenReturn(true);
        when(config.getDeathPenaltyPercent()).thenReturn(5.0);
        when(economyManager.hasAccount(playerId)).thenReturn(true);
        when(economyManager.applyPenalty(playerId, 5.0)).thenReturn(10.0);

        listener.onPlayerDeath(event);

        verify(economyManager).applyPenalty(playerId, 5.0);
        verify(player).sendMessage(anyString());
    }

    @Test
    void onPlayerDeath_penaltyDisabled_doesNothing() {
        when(config.isDeathPenaltyEnabled()).thenReturn(false);

        listener.onPlayerDeath(event);

        verify(economyManager, never()).applyPenalty(any(), anyDouble());
        verify(player, never()).sendMessage(anyString());
    }

    @Test
    void onPlayerDeath_noAccount_doesNotApplyPenalty() {
        when(config.isDeathPenaltyEnabled()).thenReturn(true);
        when(economyManager.hasAccount(playerId)).thenReturn(false);

        listener.onPlayerDeath(event);

        verify(economyManager, never()).applyPenalty(any(), anyDouble());
    }

    @Test
    void onPlayerDeath_zeroPenalty_doesNotSendMessage() {
        when(config.isDeathPenaltyEnabled()).thenReturn(true);
        when(config.getDeathPenaltyPercent()).thenReturn(5.0);
        when(economyManager.hasAccount(playerId)).thenReturn(true);
        when(economyManager.applyPenalty(playerId, 5.0)).thenReturn(0.0);

        listener.onPlayerDeath(event);

        verify(player, never()).sendMessage(anyString());
    }
}
