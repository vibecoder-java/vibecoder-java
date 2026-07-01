package com.vibecoder.vibecore.commands;

import com.vibecoder.vibecore.config.PluginConfig;
import com.vibecoder.vibecore.manager.CooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedCommandTest {

    @Mock
    private CooldownManager cooldownManager;

    @Mock
    private PluginConfig config;

    @Mock
    private Player player;

    @Mock
    private Player targetPlayer;

    @Mock
    private Command command;

    @Mock
    private CommandSender consoleSender;

    private FeedCommand feedCommand;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        feedCommand = new FeedCommand(cooldownManager, config);
        playerId = UUID.randomUUID();
        lenient().when(player.getUniqueId()).thenReturn(playerId);
        lenient().when(player.getName()).thenReturn("TestPlayer");
    }

    @Test
    void onCommand_consoleSenderNoArgs_showsUsage() {
        boolean result = feedCommand.onCommand(consoleSender, command, "feed", new String[]{});
        assertTrue(result);
        verify(consoleSender).sendMessage(anyString());
    }

    @Test
    void onCommand_playerSelfFeed_feedsPlayer() {
        when(cooldownManager.isOnCooldown(playerId, "feed")).thenReturn(false);
        when(config.getFeedCooldownSeconds()).thenReturn(30);

        boolean result = feedCommand.onCommand(player, command, "feed", new String[]{});

        assertTrue(result);
        verify(player).setFoodLevel(20);
        verify(player).setSaturation(20f);
        verify(player).setExhaustion(0f);
        verify(cooldownManager).setCooldown(playerId, "feed", 30);
    }

    @Test
    void onCommand_playerOnCooldown_showsError() {
        when(cooldownManager.isOnCooldown(playerId, "feed")).thenReturn(true);
        when(cooldownManager.getRemainingCooldown(playerId, "feed")).thenReturn(10L);

        boolean result = feedCommand.onCommand(player, command, "feed", new String[]{});

        assertTrue(result);
        verify(player, never()).setFoodLevel(anyInt());
    }

    @Test
    void onCommand_targetNotFound_showsError() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getPlayer("Unknown")).thenReturn(null);

            boolean result = feedCommand.onCommand(player, command, "feed", new String[]{"Unknown"});

            assertTrue(result);
            verify(player).sendMessage(anyString());
        }
    }

    @Test
    void onCommand_feedOtherPlayer_feedsTarget() {
        lenient().when(targetPlayer.getName()).thenReturn("TargetPlayer");
        when(cooldownManager.isOnCooldown(playerId, "feed")).thenReturn(false);
        when(config.getFeedCooldownSeconds()).thenReturn(30);

        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getPlayer("TargetPlayer")).thenReturn(targetPlayer);

            boolean result = feedCommand.onCommand(player, command, "feed", new String[]{"TargetPlayer"});

            assertTrue(result);
            verify(targetPlayer).setFoodLevel(20);
            verify(targetPlayer).setSaturation(20f);
            verify(targetPlayer).setExhaustion(0f);
        }
    }
}
