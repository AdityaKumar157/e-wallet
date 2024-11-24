package com.makeprojects.ewallet.transactions.core.service.implementation;

import com.makeprojects.ewallet.shared.exceptions.InvalidTransactionException;
import com.makeprojects.ewallet.shared.database.model.Transaction;
import com.makeprojects.ewallet.transactions.database.repository.TransactionRepository;
import com.makeprojects.ewallet.transactions.util.TransactionValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public Transaction createTransaction(Transaction transaction) {
        if(!TransactionValidation.isValidTransaction(transaction)) {
            log.error("Transaction is invalid.");
            throw new InvalidTransactionException(String.format("Transaction with id %s is invalid.", transaction.getTransactionId()));
        }

        transaction.setWasSuccessful(true);
        Transaction savedTransaction = this.transactionRepository.save(transaction);

        log.info("Transaction successful");
        return savedTransaction;
    }

    public List<Transaction> getMiniStatement(UUID accountId, Instant start, Instant end) {
        return this.transactionRepository.getTransactionsForAccountInDateRange(
                accountId,
                start,
                end,
                Sort.by(Sort.Order.desc("createdAt")));
    }
}
