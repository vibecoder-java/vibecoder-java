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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceCommandTest {

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

    private BalanceCommand balanceCommand;
    private UUID playerId;
    private UUID targetId;

    @BeforeEach
    void setUp() {
        balanceCommand = new BalanceCommand(economyManager);
        playerId = UUID.randomUUID();
        targetId = UUID.randomUUID();
        lenient().when(player.getUniqueId()).thenReturn(playerId);
        lenient().when(player.getName()).thenReturn("TestPlayer");
    }

    @Test
    void onCommand_playerCheckOwnBalance_showsBalance() {
        when(economyManager.hasAccount(playerId)).thenReturn(true);
        when(economyManager.getBalance(playerId)).thenReturn(150.0);

        boolean result = balanceCommand.onCommand(player, command, "balance", new String[]{});

        assertTrue(result);
        verify(player).sendMessage(anyString());
    }

    @Test
    void onCommand_playerNoAccount_showsError() {
        when(economyManager.hasAccount(playerId)).thenReturn(false);

        boolean result = balanceCommand.onCommand(player, command, "balance", new String[]{});

        assertTrue(result);
        verify(player).sendMessage(anyString());
    }

    @Test
    void onCommand_checkOtherPlayerBalance_showsBalance() {
        when(targetPlayer.getUniqueId()).thenReturn(targetId);
        when(targetPlayer.getName()).thenReturn("TargetPlayer");
        when(economyManager.hasAccount(targetId)).thenReturn(true);
        when(economyManager.getBalance(targetId)).thenReturn(500.0);

        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getPlayer("TargetPlayer")).thenReturn(targetPlayer);

            boolean result = balanceCommand.onCommand(player, command, "balance", new String[]{"TargetPlayer"});

            assertTrue(result);
            verify(player).sendMessage(anyString());
        }
    }

    @Test
    void onCommand_targetNotFound_showsError() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getPlayer("Unknown")).thenReturn(null);

            boolean result = balanceCommand.onCommand(player, command, "balance", new String[]{"Unknown"});

            assertTrue(result);
            verify(player).sendMessage(anyString());
        }
    }

    @Test
    void onCommand_consoleSenderNoArgs_showsUsage() {
        boolean result = balanceCommand.onCommand(consoleSender, command, "balance", new String[]{});
        assertTrue(result);
        verify(consoleSender).sendMessage(anyString());
    }
}
