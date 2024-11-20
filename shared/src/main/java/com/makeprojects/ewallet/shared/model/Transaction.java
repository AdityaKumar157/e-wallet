package com.makeprojects.ewallet.shared.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
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
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID transactionId;

    @ManyToOne
    @JsonIncludeProperties({"accountId", "user"})
    private Account senderAccount;

    @ManyToOne
    @JsonIncludeProperties({"accountId", "user"})
    private Account receiverAccount;

    private double amount;
    private boolean wasSuccessful;
    private Instant createdAt;
}
