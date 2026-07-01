package com.vibecoder.vibecore.utils;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private TextUtil() {
    }

    /**
     * Translates color codes (& prefix) and hex colors (&#RRGGBB) into Bukkit-compatible strings.
     */
    public static String colorize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            String hexColor = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hexColor.toCharArray()) {
                replacement.append('§').append(c);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }
        matcher.appendTail(buffer);
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    /**
     * Strips all color codes from a string.
     */
    public static String stripColors(String text) {
        if (text == null) {
            return null;
        }
        return ChatColor.stripColor(colorize(text));
    }

    /**
     * Replaces placeholders in a message string.
     * Placeholders are in the format {key}.
     */
    public static String replacePlaceholders(String text, String... keyValuePairs) {
        if (text == null || keyValuePairs.length == 0) {
            return text;
        }
        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("Key-value pairs must be provided in pairs");
        }
        String result = text;
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            result = result.replace("{" + keyValuePairs[i] + "}", keyValuePairs[i + 1]);
        }
        return result;
    }

    /**
     * Formats a duration in seconds into a human-readable string.
     */
    public static String formatDuration(long totalSeconds) {
        if (totalSeconds < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
        if (totalSeconds == 0) {
            return "0 seconds";
        }

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append(hours == 1 ? " hour" : " hours");
        }
        if (minutes > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(minutes).append(minutes == 1 ? " minute" : " minutes");
        }
        if (seconds > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(seconds).append(seconds == 1 ? " second" : " seconds");
        }
        return sb.toString();
    }

    /**
     * Formats a currency amount with proper decimal places.
     */
    public static String formatCurrency(double amount) {
        if (amount == Math.floor(amount)) {
            return String.format("$%.0f", amount);
        }
        return String.format("$%.2f", amount);
    }
}
