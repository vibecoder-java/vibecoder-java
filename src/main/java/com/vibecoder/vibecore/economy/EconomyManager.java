package com.vibecoder.vibecore.economy;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EconomyManager {

    private final Map<UUID, Double> balances = new ConcurrentHashMap<>();
    private final double startingBalance;

    public EconomyManager(double startingBalance) {
        if (startingBalance < 0) {
            throw new IllegalArgumentException("Starting balance cannot be negative");
        }
        this.startingBalance = startingBalance;
    }

    /**
     * Creates an account for a player with the starting balance.
     * Returns false if account already exists.
     */
    public boolean createAccount(UUID playerId) {
        if (playerId == null) {
            throw new IllegalArgumentException("Player ID cannot be null");
        }
        if (balances.containsKey(playerId)) {
            return false;
        }
        balances.put(playerId, startingBalance);
        return true;
    }

    /**
     * Returns true if the player has an account.
     */
    public boolean hasAccount(UUID playerId) {
        if (playerId == null) {
            return false;
        }
        return balances.containsKey(playerId);
    }

    /**
     * Gets the balance for a player. Returns 0 if no account exists.
     */
    public double getBalance(UUID playerId) {
        if (playerId == null) {
            return 0;
        }
        return balances.getOrDefault(playerId, 0.0);
    }

    /**
     * Sets the balance for a player.
     */
    public void setBalance(UUID playerId, double amount) {
        if (playerId == null) {
            throw new IllegalArgumentException("Player ID cannot be null");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        balances.put(playerId, amount);
    }

    /**
     * Deposits money into a player's account.
     * Returns the new balance, or -1 if no account exists.
     */
    public double deposit(UUID playerId, double amount) {
        if (playerId == null) {
            throw new IllegalArgumentException("Player ID cannot be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        if (!hasAccount(playerId)) {
            return -1;
        }
        double newBalance = balances.get(playerId) + amount;
        balances.put(playerId, newBalance);
        return newBalance;
    }

    /**
     * Withdraws money from a player's account.
     * Returns the new balance, or -1 if insufficient funds or no account.
     */
    public double withdraw(UUID playerId, double amount) {
        if (playerId == null) {
            throw new IllegalArgumentException("Player ID cannot be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (!hasAccount(playerId)) {
            return -1;
        }
        double currentBalance = balances.get(playerId);
        if (currentBalance < amount) {
            return -1;
        }
        double newBalance = currentBalance - amount;
        balances.put(playerId, newBalance);
        return newBalance;
    }

    /**
     * Transfers money from one player to another.
     * Returns true if successful.
     */
    public boolean transfer(UUID from, UUID to, double amount) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Player IDs cannot be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        if (from.equals(to)) {
            return false;
        }
        if (!hasAccount(from) || !hasAccount(to)) {
            return false;
        }
        double fromBalance = balances.get(from);
        if (fromBalance < amount) {
            return false;
        }
        balances.put(from, fromBalance - amount);
        balances.put(to, balances.get(to) + amount);
        return true;
    }

    /**
     * Applies a percentage penalty to a player's balance.
     * Returns the amount deducted.
     */
    public double applyPenalty(UUID playerId, double percent) {
        if (playerId == null) {
            throw new IllegalArgumentException("Player ID cannot be null");
        }
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("Percent must be between 0 and 100");
        }
        if (!hasAccount(playerId)) {
            return 0;
        }
        double balance = balances.get(playerId);
        double penalty = balance * (percent / 100.0);
        balances.put(playerId, balance - penalty);
        return penalty;
    }

    public double getStartingBalance() {
        return startingBalance;
    }
}
