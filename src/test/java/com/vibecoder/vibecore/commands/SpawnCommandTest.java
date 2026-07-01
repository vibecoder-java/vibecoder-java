package com.vibecoder.vibecore.commands;

import com.vibecoder.vibecore.config.PluginConfig;
import com.vibecoder.vibecore.manager.CooldownManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpawnCommandTest {

    @Mock
    private JavaPlugin plugin;

    @Mock
    private CooldownManager cooldownManager;

    @Mock
    private PluginConfig config;

    @Mock
    private Player player;

    @Mock
    private Command command;

    @Mock
    private CommandSender consoleSender;

    @Mock
    private World world;

    private SpawnCommand spawnCommand;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        spawnCommand = new SpawnCommand(plugin, cooldownManager, config);
        playerId = UUID.randomUUID();
        lenient().when(player.getUniqueId()).thenReturn(playerId);
        lenient().when(player.getName()).thenReturn("TestPlayer");
        lenient().when(world.getName()).thenReturn("world");
    }

    @Test
    void onCommand_consoleSender_showsError() {
        boolean result = spawnCommand.onCommand(consoleSender, command, "spawn", new String[]{});
        assertTrue(result);
        verify(consoleSender).sendMessage(anyString());
    }

    @Test
    void onCommand_setSpawn_setsLocation() {
        Location loc = new Location(world, 10, 64, 20);
        when(player.getLocation()).thenReturn(loc);

        boolean result = spawnCommand.onCommand(player, command, "setspawn", new String[]{});

        assertTrue(result);
        assertEquals(loc, spawnCommand.getSpawnLocation());
        verify(player).sendMessage(anyString());
    }

    @Test
    void onCommand_spawn_noSpawnSet_showsError() {
        boolean result = spawnCommand.onCommand(player, command, "spawn", new String[]{});

        assertTrue(result);
        verify(player).sendMessage(anyString());
        verify(player, never()).teleport(any(Location.class));
    }

    @Test
    void onCommand_spawn_withSpawnSet_teleportsPlayer() {
        Location spawnLoc = new Location(world, 10, 64, 20);
        spawnCommand.setSpawnLocation(spawnLoc);
        when(cooldownManager.isOnCooldown(playerId, "spawn")).thenReturn(false);
        when(config.getSpawnCooldownSeconds()).thenReturn(10);

        boolean result = spawnCommand.onCommand(player, command, "spawn", new String[]{});

        assertTrue(result);
        verify(player).teleport(spawnLoc);
        verify(cooldownManager).setCooldown(playerId, "spawn", 10);
    }

    @Test
    void onCommand_spawn_onCooldown_showsError() {
        spawnCommand.setSpawnLocation(new Location(world, 0, 0, 0));
        when(cooldownManager.isOnCooldown(playerId, "spawn")).thenReturn(true);
        when(cooldownManager.getRemainingCooldown(playerId, "spawn")).thenReturn(5L);

        boolean result = spawnCommand.onCommand(player, command, "spawn", new String[]{});

        assertTrue(result);
        verify(player, never()).teleport(any(Location.class));
    }

    @Test
    void getSpawnLocation_initiallyNull() {
        assertNull(spawnCommand.getSpawnLocation());
    }

    @Test
    void setSpawnLocation_updatesLocation() {
        Location loc = new Location(world, 5, 10, 15);
        spawnCommand.setSpawnLocation(loc);
        assertEquals(loc, spawnCommand.getSpawnLocation());
    }
}
