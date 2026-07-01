package com.vibecoder.vibecore.utils;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public final class LocationUtil {

    private LocationUtil() {
    }

    /**
     * Serializes a Location to a Map for config storage.
     */
    public static Map<String, Object> serializeLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        Map<String, Object> map = new HashMap<>();
        if (location.getWorld() != null) {
            map.put("world", location.getWorld().getName());
        }
        map.put("x", location.getX());
        map.put("y", location.getY());
        map.put("z", location.getZ());
        map.put("yaw", (double) location.getYaw());
        map.put("pitch", (double) location.getPitch());
        return map;
    }

    /**
     * Calculates the distance between two locations (must be in same world).
     * Returns -1 if locations are in different worlds.
     */
    public static double distance(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            throw new IllegalArgumentException("Locations cannot be null");
        }
        World w1 = loc1.getWorld();
        World w2 = loc2.getWorld();
        if (w1 == null || w2 == null || !w1.equals(w2)) {
            return -1;
        }
        return loc1.distance(loc2);
    }

    /**
     * Checks if a location is within a cubic radius of another location.
     */
    public static boolean isWithinRadius(Location center, Location target, double radius) {
        if (center == null || target == null) {
            throw new IllegalArgumentException("Locations cannot be null");
        }
        if (radius < 0) {
            throw new IllegalArgumentException("Radius cannot be negative");
        }
        double dist = distance(center, target);
        if (dist < 0) {
            return false;
        }
        return dist <= radius;
    }

    /**
     * Returns a formatted string representation of a location.
     */
    public static String formatLocation(Location location) {
        if (location == null) {
            return "unknown";
        }
        String worldName = location.getWorld() != null ? location.getWorld().getName() : "unknown";
        return String.format("%s (%.1f, %.1f, %.1f)",
                worldName, location.getX(), location.getY(), location.getZ());
    }
}
