package com.vibecoder.vibecore.config;

import org.bukkit.configuration.file.FileConfiguration;

public class PluginConfig {

    private final boolean welcomeMessageEnabled;
    private final String welcomeMessage;
    private final boolean deathPenaltyEnabled;
    private final double deathPenaltyPercent;
    private final double startingBalance;
    private final int healCooldownSeconds;
    private final int feedCooldownSeconds;
    private final int spawnCooldownSeconds;
    private final double maxPayAmount;
    private final double minPayAmount;

    public PluginConfig(FileConfiguration config) {
        this.welcomeMessageEnabled = config.getBoolean("welcome.enabled", true);
        this.welcomeMessage = config.getString("welcome.message",
                "&aWelcome to the server, &e{player}&a!");
        this.deathPenaltyEnabled = config.getBoolean("death-penalty.enabled", false);
        this.deathPenaltyPercent = config.getDouble("death-penalty.percent", 5.0);
        this.startingBalance = config.getDouble("economy.starting-balance", 100.0);
        this.healCooldownSeconds = config.getInt("cooldowns.heal", 30);
        this.feedCooldownSeconds = config.getInt("cooldowns.feed", 30);
        this.spawnCooldownSeconds = config.getInt("cooldowns.spawn", 10);
        this.maxPayAmount = config.getDouble("economy.max-pay", 10000.0);
        this.minPayAmount = config.getDouble("economy.min-pay", 1.0);
    }

    public boolean isWelcomeMessageEnabled() {
        return welcomeMessageEnabled;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public boolean isDeathPenaltyEnabled() {
        return deathPenaltyEnabled;
    }

    public double getDeathPenaltyPercent() {
        return deathPenaltyPercent;
    }

    public double getStartingBalance() {
        return startingBalance;
    }

    public int getHealCooldownSeconds() {
        return healCooldownSeconds;
    }

    public int getFeedCooldownSeconds() {
        return feedCooldownSeconds;
    }

    public int getSpawnCooldownSeconds() {
        return spawnCooldownSeconds;
    }

    public double getMaxPayAmount() {
        return maxPayAmount;
    }

    public double getMinPayAmount() {
        return minPayAmount;
    }
}
