package com.makeprojects.ewallet.shared.core.definition;

public interface MonetaryAccount extends Account {

    /**
     * Retrieves current balance
     * @return current balance of account
     */
    double getBalance();

    /**
     * Adds funds to the account
     * @param amount value to be added
     * @param fromAccount funding source account
     * @return true if amount is added, otherwise false
     */
    boolean deposit(double amount, MonetaryAccount fromAccount);

    /**
     * Withdraws funds (returns false if insufficient balance)
     * @param amount value to be withdrawn
     * @param toAccount receiving account
     * @return true if amount is withdrawn, otherwise false
     */
    boolean withdraw(double amount, MonetaryAccount toAccount);

    /**
     * Transfers money to another account
     * @param targetAccount target account for transferring account
     * @param amount value to be transferred
     * @return true if transfer succeeded, otherwise false
     */
    boolean transfer(MonetaryAccount targetAccount, double amount);
}
