package com.vibecoder.vibecore;

import com.vibecoder.vibecore.commands.FeedCommand;
import com.vibecoder.vibecore.commands.HealCommand;
import com.vibecoder.vibecore.commands.SpawnCommand;
import com.vibecoder.vibecore.config.PluginConfig;
import com.vibecoder.vibecore.economy.EconomyManager;
import com.vibecoder.vibecore.economy.BalanceCommand;
import com.vibecoder.vibecore.economy.PayCommand;
import com.vibecoder.vibecore.listeners.PlayerJoinListener;
import com.vibecoder.vibecore.listeners.PlayerDeathListener;
import com.vibecoder.vibecore.manager.CooldownManager;
import org.bukkit.plugin.java.JavaPlugin;

public class VibeCore extends JavaPlugin {

    private PluginConfig pluginConfig;
    private EconomyManager economyManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        pluginConfig = new PluginConfig(getConfig());
        economyManager = new EconomyManager(pluginConfig.getStartingBalance());
        cooldownManager = new CooldownManager();

        registerCommands();
        registerListeners();

        getLogger().info("VibeCore has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("VibeCore has been disabled!");
    }

    private void registerCommands() {
        getCommand("heal").setExecutor(new HealCommand(cooldownManager, pluginConfig));
        getCommand("feed").setExecutor(new FeedCommand(cooldownManager, pluginConfig));
        getCommand("spawn").setExecutor(new SpawnCommand(this, cooldownManager, pluginConfig));
        getCommand("setspawn").setExecutor(new SpawnCommand(this, cooldownManager, pluginConfig));
        getCommand("balance").setExecutor(new BalanceCommand(economyManager));
        getCommand("pay").setExecutor(new PayCommand(economyManager));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(
                new PlayerJoinListener(pluginConfig, economyManager), this);
        getServer().getPluginManager().registerEvents(
                new PlayerDeathListener(pluginConfig, economyManager), this);
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}
