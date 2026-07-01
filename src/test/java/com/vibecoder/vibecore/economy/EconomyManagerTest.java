package com.vibecoder.vibecore.economy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EconomyManagerTest {

    private EconomyManager economy;
    private UUID player1;
    private UUID player2;

    @BeforeEach
    void setUp() {
        economy = new EconomyManager(100.0);
        player1 = UUID.randomUUID();
        player2 = UUID.randomUUID();
    }

    @Test
    void constructor_negativeStartingBalance_throws() {
        assertThrows(IllegalArgumentException.class, () -> new EconomyManager(-1));
    }

    @Test
    void constructor_zeroStartingBalance_allowed() {
        EconomyManager em = new EconomyManager(0);
        assertEquals(0, em.getStartingBalance());
    }

    @Test
    void createAccount_newPlayer_returnsTrue() {
        assertTrue(economy.createAccount(player1));
        assertEquals(100.0, economy.getBalance(player1));
    }

    @Test
    void createAccount_existingPlayer_returnsFalse() {
        economy.createAccount(player1);
        assertFalse(economy.createAccount(player1));
    }

    @Test
    void createAccount_nullPlayer_throws() {
        assertThrows(IllegalArgumentException.class, () -> economy.createAccount(null));
    }

    @Test
    void hasAccount_existingPlayer_returnsTrue() {
        economy.createAccount(player1);
        assertTrue(economy.hasAccount(player1));
    }

    @Test
    void hasAccount_nonExistingPlayer_returnsFalse() {
        assertFalse(economy.hasAccount(player1));
    }

    @Test
    void hasAccount_nullPlayer_returnsFalse() {
        assertFalse(economy.hasAccount(null));
    }

    @Test
    void getBalance_existingAccount_returnsBalance() {
        economy.createAccount(player1);
        assertEquals(100.0, economy.getBalance(player1));
    }

    @Test
    void getBalance_noAccount_returnsZero() {
        assertEquals(0.0, economy.getBalance(player1));
    }

    @Test
    void getBalance_nullPlayer_returnsZero() {
        assertEquals(0.0, economy.getBalance(null));
    }

    @Test
    void setBalance_validAmount_updatesBalance() {
        economy.createAccount(player1);
        economy.setBalance(player1, 500.0);
        assertEquals(500.0, economy.getBalance(player1));
    }

    @Test
    void setBalance_negativeAmount_throws() {
        economy.createAccount(player1);
        assertThrows(IllegalArgumentException.class, () -> economy.setBalance(player1, -10));
    }

    @Test
    void setBalance_nullPlayer_throws() {
        assertThrows(IllegalArgumentException.class, () -> economy.setBalance(null, 100));
    }

    @Test
    void deposit_validAmount_increasesBalance() {
        economy.createAccount(player1);
        double newBalance = economy.deposit(player1, 50.0);
        assertEquals(150.0, newBalance);
        assertEquals(150.0, economy.getBalance(player1));
    }

    @Test
    void deposit_noAccount_returnsNegativeOne() {
        assertEquals(-1, economy.deposit(player1, 50));
    }

    @Test
    void deposit_zeroAmount_throws() {
        economy.createAccount(player1);
        assertThrows(IllegalArgumentException.class, () -> economy.deposit(player1, 0));
    }

    @Test
    void deposit_negativeAmount_throws() {
        economy.createAccount(player1);
        assertThrows(IllegalArgumentException.class, () -> economy.deposit(player1, -10));
    }

    @Test
    void deposit_nullPlayer_throws() {
        assertThrows(IllegalArgumentException.class, () -> economy.deposit(null, 50));
    }

    @Test
    void withdraw_validAmount_decreasesBalance() {
        economy.createAccount(player1);
        double newBalance = economy.withdraw(player1, 30.0);
        assertEquals(70.0, newBalance);
    }

    @Test
    void withdraw_insufficientFunds_returnsNegativeOne() {
        economy.createAccount(player1);
        assertEquals(-1, economy.withdraw(player1, 200));
    }

    @Test
    void withdraw_exactBalance_returnsZero() {
        economy.createAccount(player1);
        double newBalance = economy.withdraw(player1, 100.0);
        assertEquals(0.0, newBalance);
    }

    @Test
    void withdraw_noAccount_returnsNegativeOne() {
        assertEquals(-1, economy.withdraw(player1, 10));
    }

    @Test
    void withdraw_negativeAmount_throws() {
        economy.createAccount(player1);
        assertThrows(IllegalArgumentException.class, () -> economy.withdraw(player1, -10));
    }

    @Test
    void withdraw_nullPlayer_throws() {
        assertThrows(IllegalArgumentException.class, () -> economy.withdraw(null, 10));
    }

    @Test
    void transfer_validTransfer_movesMoneyBetweenAccounts() {
        economy.createAccount(player1);
        economy.createAccount(player2);
        assertTrue(economy.transfer(player1, player2, 40.0));
        assertEquals(60.0, economy.getBalance(player1));
        assertEquals(140.0, economy.getBalance(player2));
    }

    @Test
    void transfer_insufficientFunds_returnsFalse() {
        economy.createAccount(player1);
        economy.createAccount(player2);
        assertFalse(economy.transfer(player1, player2, 200));
    }

    @Test
    void transfer_toSelf_returnsFalse() {
        economy.createAccount(player1);
        assertFalse(economy.transfer(player1, player1, 10));
    }

    @Test
    void transfer_noFromAccount_returnsFalse() {
        economy.createAccount(player2);
        assertFalse(economy.transfer(player1, player2, 10));
    }

    @Test
    void transfer_noToAccount_returnsFalse() {
        economy.createAccount(player1);
        assertFalse(economy.transfer(player1, player2, 10));
    }

    @Test
    void transfer_nullFrom_throws() {
        assertThrows(IllegalArgumentException.class, () -> economy.transfer(null, player2, 10));
    }

    @Test
    void transfer_nullTo_throws() {
        assertThrows(IllegalArgumentException.class, () -> economy.transfer(player1, null, 10));
    }

    @Test
    void transfer_negativeAmount_throws() {
        assertThrows(IllegalArgumentException.class, () -> economy.transfer(player1, player2, -10));
    }

    @Test
    void applyPenalty_validPercent_deductsCorrectly() {
        economy.createAccount(player1);
        double penalty = economy.applyPenalty(player1, 10.0);
        assertEquals(10.0, penalty);
        assertEquals(90.0, economy.getBalance(player1));
    }

    @Test
    void applyPenalty_zeroPercent_deductsNothing() {
        economy.createAccount(player1);
        double penalty = economy.applyPenalty(player1, 0);
        assertEquals(0, penalty);
        assertEquals(100.0, economy.getBalance(player1));
    }

    @Test
    void applyPenalty_hundredPercent_deductsAll() {
        economy.createAccount(player1);
        double penalty = economy.applyPenalty(player1, 100);
        assertEquals(100.0, penalty);
        assertEquals(0, economy.getBalance(player1));
    }

    @Test
    void applyPenalty_noAccount_returnsZero() {
        assertEquals(0, economy.applyPenalty(player1, 10));
    }

    @Test
    void applyPenalty_negativePercent_throws() {
        economy.createAccount(player1);
        assertThrows(IllegalArgumentException.class, () -> economy.applyPenalty(player1, -5));
    }

    @Test
    void applyPenalty_overHundredPercent_throws() {
        economy.createAccount(player1);
        assertThrows(IllegalArgumentException.class, () -> economy.applyPenalty(player1, 101));
    }

    @Test
    void applyPenalty_nullPlayer_throws() {
        assertThrows(IllegalArgumentException.class, () -> economy.applyPenalty(null, 10));
    }

    @Test
    void getStartingBalance_returnsConfiguredValue() {
        assertEquals(100.0, economy.getStartingBalance());
    }
}
