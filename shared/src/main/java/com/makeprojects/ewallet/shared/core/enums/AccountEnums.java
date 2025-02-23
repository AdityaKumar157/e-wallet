package com.makeprojects.ewallet.shared.core.enums;

public class AccountEnums {

    /**
     * Enum representing different statuses of an account.
     */
    public enum AccountStatus {
        PENDING_KYC,
        ACTIVE,
        SUSPENDED,
        CLOSED,
        FROZEN,
        RESTRICTED
    }

    /**
     * Enum representing different real banks
     */
    public enum Banks {
        UNSPECIFIED,
        SBI,
        ICICI,
        HDFC,
        AXIS,
        PNB,
        BOI,
        YES_BANK
    }
}
