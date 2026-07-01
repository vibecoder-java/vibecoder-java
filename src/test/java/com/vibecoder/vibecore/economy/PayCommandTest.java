package com.vibecoder.vibecore.economy;

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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayCommandTest {

    @Mock
    private EconomyManager economyManager;

    @Mock
    private Player player;

    @Mock
    private Player targetPlayer;

    @Mock
    private Command command;

    @Mock
    private CommandSender consoleSender;

    private PayCommand payCommand;
    private UUID playerId;
    private UUID targetId;

    @BeforeEach
    void setUp() {
        payCommand = new PayCommand(economyManager);
        playerId = UUID.randomUUID();
        targetId = UUID.randomUUID();
        lenient().when(player.getUniqueId()).thenReturn(playerId);
        lenient().when(player.getName()).thenReturn("TestPlayer");
    }

    @Test
    void onCommand_consoleSender_showsError() {
        boolean result = payCommand.onCommand(consoleSender, command, "pay", new String[]{});
        assertTrue(result);
        verify(consoleSender).sendMessage(anyString());
    }

    @Test
    void onCommand_insufficientArgs_showsUsage() {
        boolean result = payCommand.onCommand(player, command, "pay", new String[]{"target"});
        assertTrue(result);
        verify(player).sendMessage(anyString());
    }

    @Test
    void onCommand_noArgs_showsUsage() {
        boolean result = payCommand.onCommand(player, command, "pay", new String[]{});
        assertTrue(result);
        verify(player).sendMessage(anyString());
    }

    @Test
    void onCommand_targetNotFound_showsError() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getPlayer("Unknown")).thenReturn(null);

            boolean result = payCommand.onCommand(player, command, "pay", new String[]{"Unknown", "50"});

            assertTrue(result);
            verify(player).sendMessage(anyString());
        }
    }

    @Test
    void onCommand_payYourself_showsError() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getPlayer("TestPlayer")).thenReturn(player);

            boolean result = payCommand.onCommand(player, command, "pay", new String[]{"TestPlayer", "50"});

            assertTrue(result);
            verify(player).sendMessage(anyString());
        }
    }

    @Test
    void onCommand_invalidAmount_showsError() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getPlayer("TargetPlayer")).thenReturn(targetPlayer);

            boolean result = payCommand.onCommand(player, command, "pay", new String[]{"TargetPlayer", "abc"});

            assertTrue(result);
            verify(player).sendMessage(anyString());
        }
    }

    @Test
    void onCommand_negativeAmount_showsError() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getPlayer("TargetPlayer")).thenReturn(targetPlayer);

            boolean result = payCommand.onCommand(player, command, "pay", new String[]{"TargetPlayer", "-50"});

            assertTrue(result);
            verify(player).sendMessage(anyString());
        }
    }

    @Test
    void onCommand_successfulPay_sendsMessages() {
        when(targetPlayer.getUniqueId()).thenReturn(targetId);
        when(targetPlayer.getName()).thenReturn("TargetPlayer");
        when(economyManager.transfer(playerId, targetId, 50.0)).thenReturn(true);

        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getPlayer("TargetPlayer")).thenReturn(targetPlayer);

            boolean result = payCommand.onCommand(player, command, "pay", new String[]{"TargetPlayer", "50"});

            assertTrue(result);
            verify(player).sendMessage(anyString());
            verify(targetPlayer).sendMessage(anyString());
            verify(economyManager).transfer(playerId, targetId, 50.0);
        }
    }

    @Test
    void onCommand_transferFailed_showsError() {
        when(targetPlayer.getUniqueId()).thenReturn(targetId);

        when(economyManager.transfer(playerId, targetId, 50.0)).thenReturn(false);

        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getPlayer("TargetPlayer")).thenReturn(targetPlayer);

            boolean result = payCommand.onCommand(player, command, "pay", new String[]{"TargetPlayer", "50"});

            assertTrue(result);
            verify(player).sendMessage(anyString());
        }
    }
}
