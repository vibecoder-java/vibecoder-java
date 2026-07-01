package com.vibecoder.vibecore.manager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {

    private final Map<String, Map<UUID, Long>> cooldowns = new ConcurrentHashMap<>();

    /**
     * Sets a cooldown for a player on a specific action.
     */
    public void setCooldown(UUID playerId, String action, int durationSeconds) {
        if (playerId == null || action == null) {
            throw new IllegalArgumentException("Player ID and action cannot be null");
        }
        if (durationSeconds < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
        cooldowns.computeIfAbsent(action, k -> new ConcurrentHashMap<>())
                .put(playerId, System.currentTimeMillis() + (durationSeconds * 1000L));
    }

    /**
     * Checks if a player is on cooldown for a specific action.
     */
    public boolean isOnCooldown(UUID playerId, String action) {
        if (playerId == null || action == null) {
            return false;
        }
        Map<UUID, Long> actionCooldowns = cooldowns.get(action);
        if (actionCooldowns == null) {
            return false;
        }
        Long expiryTime = actionCooldowns.get(playerId);
        if (expiryTime == null) {
            return false;
        }
        if (System.currentTimeMillis() >= expiryTime) {
            actionCooldowns.remove(playerId);
            return false;
        }
        return true;
    }

    /**
     * Gets the remaining cooldown time in seconds for a player on an action.
     * Returns 0 if not on cooldown.
     */
    public long getRemainingCooldown(UUID playerId, String action) {
        if (playerId == null || action == null) {
            return 0;
        }
        Map<UUID, Long> actionCooldowns = cooldowns.get(action);
        if (actionCooldowns == null) {
            return 0;
        }
        Long expiryTime = actionCooldowns.get(playerId);
        if (expiryTime == null) {
            return 0;
        }
        long remaining = expiryTime - System.currentTimeMillis();
        if (remaining <= 0) {
            actionCooldowns.remove(playerId);
            return 0;
        }
        return (remaining + 999) / 1000; // round up to nearest second
    }

    /**
     * Clears all cooldowns for a player.
     */
    public void clearCooldowns(UUID playerId) {
        if (playerId == null) {
            return;
        }
        for (Map<UUID, Long> actionCooldowns : cooldowns.values()) {
            actionCooldowns.remove(playerId);
        }
    }

    /**
     * Clears all cooldowns for all players.
     */
    public void clearAllCooldowns() {
        cooldowns.clear();
    }
}
