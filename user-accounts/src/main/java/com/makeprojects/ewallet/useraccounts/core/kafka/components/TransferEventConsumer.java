package com.makeprojects.ewallet.useraccounts.core.kafka.components;

import com.makeprojects.ewallet.shared.event.classes.TransferEvent;
import com.makeprojects.ewallet.shared.event.classes.TransferResult;
import com.makeprojects.ewallet.useraccounts.core.service.definition.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Slf4j
public class TransferEventConsumer {

    private final WalletService walletService;
    private final KafkaTemplate<String, TransferResult> kafkaTemplate;

    @Autowired
    public TransferEventConsumer(WalletService walletService, KafkaTemplate<String, TransferResult> kafkaTemplate) {
        this.walletService = walletService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = "transfer-events",
            groupId = "user-accounts-group",
            concurrency = "3"  // Enables parallel processing for different partitions
    )
    public void processMoneyTransfer(@Payload TransferEvent transferEvent, @Header(KafkaHeaders.RECEIVED_KEY) String partitionKey) {
        log.info("Processing transfer event from partition key {}: {}", partitionKey, transferEvent);

        boolean success = false;
        TransferResult transferResult = TransferResult.builder()
                .transactionId(transferEvent.getTransactionId())
                .timestamp(Instant.now())
                .build();

        try {
            success = this.walletService.transferMoney(transferEvent.getAccountId(), transferEvent.getTargetAccountId(), transferEvent.getAmount(),
                    transferEvent.getTransactionType());

            String message = success ? "Success" : "Failed";
            transferResult.setSuccess(success);
            transferResult.setMessage(message);

            // Produce "transfer-results" event for TransferResultConsumer to consume
            kafkaTemplate.send("transfer-results", partitionKey, transferResult);    // Maintain same partition key
        } catch (Exception e) {
            log.error("Error processing transfer event: {}", transferEvent, e);
            transferResult.setSuccess(false);
            transferResult.setMessage("Failed");
            kafkaTemplate.send("transfer-results", partitionKey, transferResult);
        }
    }
}
