package com.makeprojects.ewallet.shared.kafka.event.classes;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferResult {
    private UUID transactionId;
    private boolean success;
    private Instant timestamp;
    private String message;
}
