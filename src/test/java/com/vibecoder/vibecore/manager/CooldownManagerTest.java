package com.vibecoder.vibecore.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CooldownManagerTest {

    private CooldownManager cooldownManager;
    private UUID player1;
    private UUID player2;

    @BeforeEach
    void setUp() {
        cooldownManager = new CooldownManager();
        player1 = UUID.randomUUID();
        player2 = UUID.randomUUID();
    }

    @Test
    void setCooldown_validParams_doesNotThrow() {
        assertDoesNotThrow(() -> cooldownManager.setCooldown(player1, "heal", 30));
    }

    @Test
    void setCooldown_nullPlayerId_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> cooldownManager.setCooldown(null, "heal", 30));
    }

    @Test
    void setCooldown_nullAction_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> cooldownManager.setCooldown(player1, null, 30));
    }

    @Test
    void setCooldown_negativeDuration_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> cooldownManager.setCooldown(player1, "heal", -1));
    }

    @Test
    void setCooldown_zeroDuration_allowed() {
        assertDoesNotThrow(() -> cooldownManager.setCooldown(player1, "heal", 0));
    }

    @Test
    void isOnCooldown_activeCooldown_returnsTrue() {
        cooldownManager.setCooldown(player1, "heal", 60);
        assertTrue(cooldownManager.isOnCooldown(player1, "heal"));
    }

    @Test
    void isOnCooldown_noCooldownSet_returnsFalse() {
        assertFalse(cooldownManager.isOnCooldown(player1, "heal"));
    }

    @Test
    void isOnCooldown_differentAction_returnsFalse() {
        cooldownManager.setCooldown(player1, "heal", 60);
        assertFalse(cooldownManager.isOnCooldown(player1, "feed"));
    }

    @Test
    void isOnCooldown_differentPlayer_returnsFalse() {
        cooldownManager.setCooldown(player1, "heal", 60);
        assertFalse(cooldownManager.isOnCooldown(player2, "heal"));
    }

    @Test
    void isOnCooldown_nullPlayerId_returnsFalse() {
        assertFalse(cooldownManager.isOnCooldown(null, "heal"));
    }

    @Test
    void isOnCooldown_nullAction_returnsFalse() {
        assertFalse(cooldownManager.isOnCooldown(player1, null));
    }

    @Test
    void isOnCooldown_expiredCooldown_returnsFalse() {
        cooldownManager.setCooldown(player1, "heal", 0);
        assertFalse(cooldownManager.isOnCooldown(player1, "heal"));
    }

    @Test
    void getRemainingCooldown_activeCooldown_returnsPositive() {
        cooldownManager.setCooldown(player1, "heal", 60);
        long remaining = cooldownManager.getRemainingCooldown(player1, "heal");
        assertTrue(remaining > 0);
        assertTrue(remaining <= 60);
    }

    @Test
    void getRemainingCooldown_noCooldown_returnsZero() {
        assertEquals(0, cooldownManager.getRemainingCooldown(player1, "heal"));
    }

    @Test
    void getRemainingCooldown_nullPlayerId_returnsZero() {
        assertEquals(0, cooldownManager.getRemainingCooldown(null, "heal"));
    }

    @Test
    void getRemainingCooldown_nullAction_returnsZero() {
        assertEquals(0, cooldownManager.getRemainingCooldown(player1, null));
    }

    @Test
    void clearCooldowns_removesAllForPlayer() {
        cooldownManager.setCooldown(player1, "heal", 60);
        cooldownManager.setCooldown(player1, "feed", 60);
        cooldownManager.clearCooldowns(player1);
        assertFalse(cooldownManager.isOnCooldown(player1, "heal"));
        assertFalse(cooldownManager.isOnCooldown(player1, "feed"));
    }

    @Test
    void clearCooldowns_doesNotAffectOtherPlayers() {
        cooldownManager.setCooldown(player1, "heal", 60);
        cooldownManager.setCooldown(player2, "heal", 60);
        cooldownManager.clearCooldowns(player1);
        assertTrue(cooldownManager.isOnCooldown(player2, "heal"));
    }

    @Test
    void clearCooldowns_nullPlayer_doesNotThrow() {
        assertDoesNotThrow(() -> cooldownManager.clearCooldowns(null));
    }

    @Test
    void clearAllCooldowns_removesEverything() {
        cooldownManager.setCooldown(player1, "heal", 60);
        cooldownManager.setCooldown(player2, "feed", 60);
        cooldownManager.clearAllCooldowns();
        assertFalse(cooldownManager.isOnCooldown(player1, "heal"));
        assertFalse(cooldownManager.isOnCooldown(player2, "feed"));
    }

    @Test
    void multipleActions_independentCooldowns() {
        cooldownManager.setCooldown(player1, "heal", 60);
        cooldownManager.setCooldown(player1, "feed", 0);
        assertTrue(cooldownManager.isOnCooldown(player1, "heal"));
        assertFalse(cooldownManager.isOnCooldown(player1, "feed"));
    }
}
