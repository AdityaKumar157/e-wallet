package com.makeprojects.ewallet.useraccounts.dto;

import com.makeprojects.ewallet.shared.core.enums.AccountEnums;

import java.util.UUID;

/**
 * Projection for {@link com.makeprojects.ewallet.shared.database.model.BankAccount}
 */
public interface DelegatedTransactionAccountDTO {
    UUID getBankAccountId();

    AccountEnums.Banks getBankName();
}