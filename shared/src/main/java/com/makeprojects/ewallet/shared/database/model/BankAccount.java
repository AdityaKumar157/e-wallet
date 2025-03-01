package com.makeprojects.ewallet.shared.database.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.makeprojects.ewallet.shared.core.definition.DelegatedTransactionAccount;
import com.makeprojects.ewallet.shared.core.enums.AccountEnums.Banks;
import com.makeprojects.ewallet.shared.core.enums.AccountEnums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Builder
@With
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BankAccount implements DelegatedTransactionAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID bankAccountId;

    @ManyToOne
    @JoinColumn(name = "walletId", nullable = false)
    @JsonIncludeProperties({"walletId"})
    private Wallet wallet;

    private String accountHolderName; // Name of the account owner

    @Enumerated(EnumType.STRING)
    private Banks bankName; // e.g., "State Bank of India"

    private String accountNumber; // Actual bank account number

    private String ifscCode; // IFSC code for bank transactions

    private String upiId; // e.g., "user@icici"

    @Builder.Default
    private Instant createdAt = Instant.now();  // reflects when this account got linked to wallet, necessary to capture logs

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    /**
     * Unique identifier for the account
     * @return UUID of account
     */
    @Override
    public UUID getAccountId() {
        return bankAccountId;
    }

    /**
     * Name of the account owner
     * @return name of the account holder as a simple String
     */
    @Override
    public String getAccountHolderName() {
        return accountHolderName;
    }

    /**
     * Checks if the account is active
     * @return true if active, otherwise false
     */
    @Override
    public boolean isActive() {
        return accountStatus == AccountStatus.ACTIVE;
    }
}
