package com.makeprojects.ewallet.transactions.service;

import com.makeprojects.ewallet.shared.model.Transaction;
import com.makeprojects.ewallet.transactions.repository.TransactionRepository;
import com.makeprojects.ewallet.transactions.util.TransactionValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            return null;
        }

        transaction.setWasSuccessful(true);
        Transaction savedTransaction = this.transactionRepository.save(transaction);

        log.info("Transaction successful");
        return savedTransaction;
    }
}
