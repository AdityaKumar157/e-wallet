package com.makeprojects.ewallet.shared.event.classes;

import com.makeprojects.ewallet.shared.core.enums.transaction.TransactionEnums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class TransferEvent {
    private UUID accountId; // The account initiating the transaction
    private UUID targetAccountId; // The recipient account
    private double amount;
    private TransactionType transactionType;
    private Instant timestamp;
}
