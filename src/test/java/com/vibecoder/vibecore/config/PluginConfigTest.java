package com.vibecoder.vibecore.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PluginConfigTest {

    @Mock
    private FileConfiguration fileConfig;

    @Test
    void defaultValues_whenConfigEmpty_returnsDefaults() {
        when(fileConfig.getBoolean("welcome.enabled", true)).thenReturn(true);
        when(fileConfig.getString("welcome.message", "&aWelcome to the server, &e{player}&a!"))
                .thenReturn("&aWelcome to the server, &e{player}&a!");
        when(fileConfig.getBoolean("death-penalty.enabled", false)).thenReturn(false);
        when(fileConfig.getDouble("death-penalty.percent", 5.0)).thenReturn(5.0);
        when(fileConfig.getDouble("economy.starting-balance", 100.0)).thenReturn(100.0);
        when(fileConfig.getInt("cooldowns.heal", 30)).thenReturn(30);
        when(fileConfig.getInt("cooldowns.feed", 30)).thenReturn(30);
        when(fileConfig.getInt("cooldowns.spawn", 10)).thenReturn(10);
        when(fileConfig.getDouble("economy.max-pay", 10000.0)).thenReturn(10000.0);
        when(fileConfig.getDouble("economy.min-pay", 1.0)).thenReturn(1.0);

        PluginConfig config = new PluginConfig(fileConfig);

        assertTrue(config.isWelcomeMessageEnabled());
        assertEquals("&aWelcome to the server, &e{player}&a!", config.getWelcomeMessage());
        assertFalse(config.isDeathPenaltyEnabled());
        assertEquals(5.0, config.getDeathPenaltyPercent());
        assertEquals(100.0, config.getStartingBalance());
        assertEquals(30, config.getHealCooldownSeconds());
        assertEquals(30, config.getFeedCooldownSeconds());
        assertEquals(10, config.getSpawnCooldownSeconds());
        assertEquals(10000.0, config.getMaxPayAmount());
        assertEquals(1.0, config.getMinPayAmount());
    }

    @Test
    void customValues_whenConfigSet_returnsCustom() {
        when(fileConfig.getBoolean("welcome.enabled", true)).thenReturn(false);
        when(fileConfig.getString("welcome.message", "&aWelcome to the server, &e{player}&a!"))
                .thenReturn("Hi {player}!");
        when(fileConfig.getBoolean("death-penalty.enabled", false)).thenReturn(true);
        when(fileConfig.getDouble("death-penalty.percent", 5.0)).thenReturn(10.0);
        when(fileConfig.getDouble("economy.starting-balance", 100.0)).thenReturn(500.0);
        when(fileConfig.getInt("cooldowns.heal", 30)).thenReturn(60);
        when(fileConfig.getInt("cooldowns.feed", 30)).thenReturn(45);
        when(fileConfig.getInt("cooldowns.spawn", 10)).thenReturn(5);
        when(fileConfig.getDouble("economy.max-pay", 10000.0)).thenReturn(50000.0);
        when(fileConfig.getDouble("economy.min-pay", 1.0)).thenReturn(10.0);

        PluginConfig config = new PluginConfig(fileConfig);

        assertFalse(config.isWelcomeMessageEnabled());
        assertEquals("Hi {player}!", config.getWelcomeMessage());
        assertTrue(config.isDeathPenaltyEnabled());
        assertEquals(10.0, config.getDeathPenaltyPercent());
        assertEquals(500.0, config.getStartingBalance());
        assertEquals(60, config.getHealCooldownSeconds());
        assertEquals(45, config.getFeedCooldownSeconds());
        assertEquals(5, config.getSpawnCooldownSeconds());
        assertEquals(50000.0, config.getMaxPayAmount());
        assertEquals(10.0, config.getMinPayAmount());
    }
}
