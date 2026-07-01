package com.vibecoder.vibecore.commands;

import com.vibecoder.vibecore.config.PluginConfig;
import com.vibecoder.vibecore.manager.CooldownManager;
import com.vibecoder.vibecore.utils.LocationUtil;
import com.vibecoder.vibecore.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final CooldownManager cooldownManager;
    private final PluginConfig config;
    private Location spawnLocation;

    public SpawnCommand(JavaPlugin plugin, CooldownManager cooldownManager, PluginConfig config) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TextUtil.colorize("&cThis command can only be used by players."));
            return true;
        }

        if (label.equalsIgnoreCase("setspawn")) {
            return handleSetSpawn(player);
        }
        return handleSpawn(player);
    }

    private boolean handleSetSpawn(Player player) {
        spawnLocation = player.getLocation();
        player.sendMessage(TextUtil.colorize(
                "&aSpawn set to &e" + LocationUtil.formatLocation(spawnLocation) + "&a!"));
        return true;
    }

    private boolean handleSpawn(Player player) {
        if (spawnLocation == null) {
            player.sendMessage(TextUtil.colorize("&cSpawn has not been set yet! Use /setspawn first."));
            return true;
        }

        if (cooldownManager.isOnCooldown(player.getUniqueId(), "spawn")) {
            long remaining = cooldownManager.getRemainingCooldown(player.getUniqueId(), "spawn");
            player.sendMessage(TextUtil.colorize(
                    "&cYou must wait " + TextUtil.formatDuration(remaining) + " before teleporting again."));
            return true;
        }

        cooldownManager.setCooldown(player.getUniqueId(), "spawn", config.getSpawnCooldownSeconds());
        player.teleport(spawnLocation);
        player.sendMessage(TextUtil.colorize("&aTeleported to spawn!"));
        return true;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location location) {
        this.spawnLocation = location;
    }
}
