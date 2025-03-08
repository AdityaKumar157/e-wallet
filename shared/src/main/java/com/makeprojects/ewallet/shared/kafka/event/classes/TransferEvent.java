package com.makeprojects.ewallet.shared.kafka.event.classes;

import com.makeprojects.ewallet.shared.core.enums.transaction.TransactionEnums.TransactionType;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferEvent {
    private UUID transactionId;
    private UUID accountId; // The account initiating the transaction
    private UUID targetAccountId; // The recipient account
    private double amount;
    private TransactionType transactionType;
    private Instant timestamp;
}
