package com.makeprojects.ewallet.shared.core.definition;

/**
 * Represents an account that manages its own transactions without external services.
 * Implemented by Wallet.
 */
public interface AutonomousTransactionAccount extends MonetaryAccount{

    /**
     * Retrieves current balance
     * @return current balance of account
     */
    double getBalance();

    /**
     * Sets the balance of the autonomous transaction account.
     * @param newBalance The updated balance.
     */
    void setBalance(double newBalance);

    /**
     * Adds funds to the account
     * @param amount value to be added
     * @param fromAccount funding source account
     * @return true if amount is added, otherwise false
     */
    boolean deposit(double amount, AutonomousTransactionAccount fromAccount);

    /**
     * Withdraws funds (returns false if insufficient balance)
     * @param amount value to be withdrawn
     * @param toAccount receiving account
     * @return true if amount is withdrawn, otherwise false
     */
    boolean withdraw(double amount, AutonomousTransactionAccount toAccount);

    /**
     * Transfers money to another account
     * @param targetAccount target account for transferring account
     * @param amount value to be transferred
     * @param receive true to receive from targetAccount, false to send to targetAccount
     * @return true if transfer succeeded, otherwise false
     */
    boolean transfer(AutonomousTransactionAccount targetAccount, double amount, boolean receive);
}
