package com.vibecoder.vibecore.commands;

import com.vibecoder.vibecore.config.PluginConfig;
import com.vibecoder.vibecore.manager.CooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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
class HealCommandTest {

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

    @Mock
    private AttributeInstance maxHealthAttr;

    private HealCommand healCommand;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        healCommand = new HealCommand(cooldownManager, config);
        playerId = UUID.randomUUID();
        lenient().when(player.getUniqueId()).thenReturn(playerId);
        lenient().when(player.getName()).thenReturn("TestPlayer");
    }

    @Test
    void onCommand_consoleSenderNoArgs_showsUsage() {
        boolean result = healCommand.onCommand(consoleSender, command, "heal", new String[]{});
        assertTrue(result);
        verify(consoleSender).sendMessage(anyString());
    }

    @Test
    void onCommand_playerSelfHeal_healsPlayer() {
        when(cooldownManager.isOnCooldown(playerId, "heal")).thenReturn(false);
        when(config.getHealCooldownSeconds()).thenReturn(30);
        when(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).thenReturn(maxHealthAttr);
        when(maxHealthAttr.getValue()).thenReturn(20.0);

        boolean result = healCommand.onCommand(player, command, "heal", new String[]{});

        assertTrue(result);
        verify(player).setHealth(20.0);
        verify(player).setFoodLevel(20);
        verify(player).setSaturation(20f);
        verify(player).setFireTicks(0);
        verify(cooldownManager).setCooldown(playerId, "heal", 30);
    }

    @Test
    void onCommand_playerOnCooldown_showsError() {
        when(cooldownManager.isOnCooldown(playerId, "heal")).thenReturn(true);
        when(cooldownManager.getRemainingCooldown(playerId, "heal")).thenReturn(15L);

        boolean result = healCommand.onCommand(player, command, "heal", new String[]{});

        assertTrue(result);
        verify(player).sendMessage(anyString());
        verify(player, never()).setHealth(anyDouble());
    }

    @Test
    void onCommand_targetNotFound_showsError() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getPlayer("Unknown")).thenReturn(null);

            boolean result = healCommand.onCommand(player, command, "heal", new String[]{"Unknown"});

            assertTrue(result);
            verify(player).sendMessage(anyString());
        }
    }

    @Test
    void onCommand_healOtherPlayer_healsTarget() {
        lenient().when(targetPlayer.getName()).thenReturn("TargetPlayer");
        when(cooldownManager.isOnCooldown(playerId, "heal")).thenReturn(false);
        when(config.getHealCooldownSeconds()).thenReturn(30);
        when(targetPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH)).thenReturn(maxHealthAttr);
        when(maxHealthAttr.getValue()).thenReturn(20.0);

        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getPlayer("TargetPlayer")).thenReturn(targetPlayer);

            boolean result = healCommand.onCommand(player, command, "heal", new String[]{"TargetPlayer"});

            assertTrue(result);
            verify(targetPlayer).setHealth(20.0);
            verify(targetPlayer).setFoodLevel(20);
        }
    }
}
