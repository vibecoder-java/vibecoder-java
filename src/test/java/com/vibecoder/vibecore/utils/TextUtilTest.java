package com.vibecoder.vibecore.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextUtilTest {

    // --- replacePlaceholders ---

    @Test
    void replacePlaceholders_singlePair_replacesCorrectly() {
        String result = TextUtil.replacePlaceholders("Hello {player}!", "player", "Steve");
        assertEquals("Hello Steve!", result);
    }

    @Test
    void replacePlaceholders_multiplePairs_replacesAll() {
        String result = TextUtil.replacePlaceholders(
                "{player} has {amount}",
                "player", "Steve", "amount", "100");
        assertEquals("Steve has 100", result);
    }

    @Test
    void replacePlaceholders_noMatchingKey_leavesUnchanged() {
        String result = TextUtil.replacePlaceholders("Hello {world}!", "player", "Steve");
        assertEquals("Hello {world}!", result);
    }

    @Test
    void replacePlaceholders_nullText_returnsNull() {
        assertNull(TextUtil.replacePlaceholders(null, "player", "Steve"));
    }

    @Test
    void replacePlaceholders_emptyPairs_returnsOriginal() {
        String result = TextUtil.replacePlaceholders("Hello!");
        assertEquals("Hello!", result);
    }

    @Test
    void replacePlaceholders_oddNumberOfPairs_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> TextUtil.replacePlaceholders("Hello", "key"));
    }

    @Test
    void replacePlaceholders_duplicatePlaceholder_replacesAll() {
        String result = TextUtil.replacePlaceholders(
                "{name} and {name}", "name", "Steve");
        assertEquals("Steve and Steve", result);
    }

    // --- formatDuration ---

    @Test
    void formatDuration_zero_returnsZeroSeconds() {
        assertEquals("0 seconds", TextUtil.formatDuration(0));
    }

    @Test
    void formatDuration_oneSecond_singularForm() {
        assertEquals("1 second", TextUtil.formatDuration(1));
    }

    @Test
    void formatDuration_multipleSeconds_pluralForm() {
        assertEquals("45 seconds", TextUtil.formatDuration(45));
    }

    @Test
    void formatDuration_oneMinute_singularForm() {
        assertEquals("1 minute", TextUtil.formatDuration(60));
    }

    @Test
    void formatDuration_minutesAndSeconds() {
        assertEquals("2 minutes, 30 seconds", TextUtil.formatDuration(150));
    }

    @Test
    void formatDuration_oneHour_singularForm() {
        assertEquals("1 hour", TextUtil.formatDuration(3600));
    }

    @Test
    void formatDuration_hoursMinutesSeconds() {
        assertEquals("1 hour, 1 minute, 1 second", TextUtil.formatDuration(3661));
    }

    @Test
    void formatDuration_multipleHours() {
        assertEquals("2 hours, 30 minutes", TextUtil.formatDuration(9000));
    }

    @Test
    void formatDuration_negative_throws() {
        assertThrows(IllegalArgumentException.class, () -> TextUtil.formatDuration(-1));
    }

    // --- formatCurrency ---

    @Test
    void formatCurrency_wholeNumber_noDecimals() {
        assertEquals("$100", TextUtil.formatCurrency(100.0));
    }

    @Test
    void formatCurrency_withDecimals_twoDecimalPlaces() {
        assertEquals("$99.99", TextUtil.formatCurrency(99.99));
    }

    @Test
    void formatCurrency_zero_formatsCorrectly() {
        assertEquals("$0", TextUtil.formatCurrency(0.0));
    }

    @Test
    void formatCurrency_largeNumber_formatsCorrectly() {
        assertEquals("$1000000", TextUtil.formatCurrency(1000000.0));
    }

    @Test
    void formatCurrency_singleDecimal_showsTwoPlaces() {
        assertEquals("$10.50", TextUtil.formatCurrency(10.5));
    }

    // --- colorize ---

    @Test
    void colorize_nullText_returnsNull() {
        assertNull(TextUtil.colorize(null));
    }

    @Test
    void colorize_emptyText_returnsEmpty() {
        assertEquals("", TextUtil.colorize(""));
    }

    @Test
    void colorize_noColorCodes_returnsOriginal() {
        assertEquals("Hello World", TextUtil.colorize("Hello World"));
    }

    @Test
    void colorize_withAmpersandCodes_translates() {
        String result = TextUtil.colorize("&aGreen");
        assertTrue(result.contains("§a"));
        assertTrue(result.contains("Green"));
    }

    @Test
    void colorize_withHexCode_translates() {
        String result = TextUtil.colorize("&#FF0000Red text");
        assertTrue(result.contains("§x"));
        assertTrue(result.contains("Red text"));
    }

    // --- stripColors ---

    @Test
    void stripColors_nullText_returnsNull() {
        assertNull(TextUtil.stripColors(null));
    }

    @Test
    void stripColors_withColorCodes_stripsAll() {
        String result = TextUtil.stripColors("&aGreen &cRed");
        assertEquals("Green Red", result);
    }

    @Test
    void stripColors_plainText_returnsOriginal() {
        assertEquals("Hello", TextUtil.stripColors("Hello"));
    }
}
