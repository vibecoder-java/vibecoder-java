package com.vibecoder.vibecore.listeners;

import com.vibecoder.vibecore.config.PluginConfig;
import com.vibecoder.vibecore.economy.EconomyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerJoinListenerTest {

    @Mock
    private PluginConfig config;

    @Mock
    private EconomyManager economyManager;

    @Mock
    private Player player;

    @Mock
    private PlayerJoinEvent event;

    private PlayerJoinListener listener;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        listener = new PlayerJoinListener(config, economyManager);
        playerId = UUID.randomUUID();
        lenient().when(event.getPlayer()).thenReturn(player);
        lenient().when(player.getUniqueId()).thenReturn(playerId);
        lenient().when(player.getName()).thenReturn("TestPlayer");
    }

    @Test
    void onPlayerJoin_newPlayer_createsAccountAndSendsWelcome() {
        when(config.isWelcomeMessageEnabled()).thenReturn(true);
        when(config.getWelcomeMessage()).thenReturn("&aWelcome {player}!");
        when(economyManager.createAccount(playerId)).thenReturn(true);
        when(economyManager.getStartingBalance()).thenReturn(100.0);

        listener.onPlayerJoin(event);

        verify(economyManager).createAccount(playerId);
        verify(player, times(2)).sendMessage(anyString());
    }

    @Test
    void onPlayerJoin_existingPlayer_doesNotSendStartingBalanceMsg() {
        when(config.isWelcomeMessageEnabled()).thenReturn(true);
        when(config.getWelcomeMessage()).thenReturn("Welcome back {player}!");
        when(economyManager.createAccount(playerId)).thenReturn(false);

        listener.onPlayerJoin(event);

        verify(player, times(1)).sendMessage(anyString());
    }

    @Test
    void onPlayerJoin_welcomeDisabled_noWelcomeMessage() {
        when(config.isWelcomeMessageEnabled()).thenReturn(false);
        when(economyManager.createAccount(playerId)).thenReturn(false);

        listener.onPlayerJoin(event);

        verify(player, never()).sendMessage(anyString());
    }

    @Test
    void onPlayerJoin_welcomeDisabledButNewPlayer_sendsBalanceOnly() {
        when(config.isWelcomeMessageEnabled()).thenReturn(false);
        when(economyManager.createAccount(playerId)).thenReturn(true);
        when(economyManager.getStartingBalance()).thenReturn(100.0);

        listener.onPlayerJoin(event);

        verify(player, times(1)).sendMessage(anyString());
    }
}
