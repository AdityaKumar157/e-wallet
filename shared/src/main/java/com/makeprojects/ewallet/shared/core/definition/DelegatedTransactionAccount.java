package com.makeprojects.ewallet.shared.core.definition;

/**
 * Represents an account whose transactions are managed by an external bank service.
 * Implemented by BankAccount. No methods are defined as all operations require BankService.
 */
public interface DelegatedTransactionAccount extends MonetaryAccount {
    // No methods, as all logic is handled in BankService
}
