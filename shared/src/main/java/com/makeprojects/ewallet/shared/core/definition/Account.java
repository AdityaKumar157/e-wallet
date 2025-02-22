package com.makeprojects.ewallet.shared.core.definition;

import java.util.UUID;

public interface Account {

    /**
     * Unique identifier for the account
     * @return UUID of account
     */
    UUID getAccountId();

    /**
     * Name of the account owner
     * @return name of the account holder as a simple String
     */
    String getAccountHolderName();

    /**
     * Checks if the account is active
     * @return true if active, otherwise false
     */
    boolean isActive();
}
