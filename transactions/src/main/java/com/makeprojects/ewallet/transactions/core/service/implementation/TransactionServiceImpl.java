package com.makeprojects.ewallet.transactions.core.service.implementation;

//<editor-fold desc="Imports">
import com.makeprojects.ewallet.shared.exceptions.InvalidTransactionException;
import com.makeprojects.ewallet.shared.database.model.Transaction;
import com.makeprojects.ewallet.shared.exceptions.NotFoundException;
import com.makeprojects.ewallet.transactions.core.service.definition.TransactionService;
import com.makeprojects.ewallet.transactions.core.service.definition.TransactionValidationService;
import com.makeprojects.ewallet.transactions.database.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
//</editor-fold>

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionValidationService transactionValidationService;

    private static final String EMPTY_STRING = "";

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, @Qualifier("transaction") TransactionValidationService transactionValidationService) {
        this.transactionRepository = transactionRepository;
        this.transactionValidationService = transactionValidationService;
    }

    //<editor-fold desc="TransactionService implementation">
    @Override
    public Transaction get(UUID id) {
        String errorMsg = EMPTY_STRING;
        try {
            if (id == null) {
                errorMsg = String.format("UUID of transaction cannot be null.");
                log.error(errorMsg);
                throw new NullPointerException(errorMsg);
            }

            Optional<Transaction> optionalTransaction = this.transactionRepository.findById(id);
            if (optionalTransaction.isEmpty()) {
                errorMsg = String.format("Transaction with UUID %s is not found.", id);
                log.error(errorMsg);
                throw new NotFoundException(Transaction.class, "UUID", id);
            }

            log.info(String.format("Successfully retrieved transaction with UUID '%s'.", id));
            return optionalTransaction.get();
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while retrieving transaction with UUID '%s'. Exception: %s", id, e);
            log.error(errorMsg);
            throw e;
        }
    }

    @Override
    public List<Transaction> getAll() {
        try {
            List<Transaction> transactionList = this.transactionRepository.findAll();
            if (transactionList.isEmpty()) {
                log.error("No transaction found.");
            }

            log.info("Successfully retrieved transactions list.");
            return transactionList;
        } catch (Exception e) {
            log.error("Exception occurred while retrieving list transactions.");
            throw e;
        }
    }

    @Override
    public Transaction create(Transaction entity) {
        String errorMsg = EMPTY_STRING;
        try {
            if (entity == null) {
                log.error("Transaction entity cannot be null.");
                throw new NullPointerException("Transaction entity cannot be null.");
            }

            if ((entity.getTransactionId() != null) && (get(entity.getTransactionId()) != null)) {
                errorMsg = String.format("Transaction with UUID %s already exists.", entity.getTransactionId());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            if(!validateTransaction(entity)) {
                throw new InvalidTransactionException(String.format("Transaction with id %s is invalid.", entity.getTransactionId()));
            }

            entity.setWasSuccessful(true);
            Transaction createdTransaction = this.transactionRepository.save(entity);
            if (createdTransaction == null) {
                log.error("Failed to create an Transaction.");
                throw new RuntimeException("Failed to create an Transaction.");
            }

            log.info(String.format("Successfully created an transaction with UUID '%s'.", createdTransaction.getTransactionId()));
            return createdTransaction;
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while creating an transaction. Exception: %s", e);
            log.error(errorMsg);
            throw e;
        }
    }

    @Override
    public Transaction update(Transaction entity) {
        String errorMsg = EMPTY_STRING;
        try {
            if (entity == null) {
                log.error("Transaction entity which needs to be updated cannot be null.");
                throw new NullPointerException("Transaction entity cannot be null.");
            }

            if (get(entity.getTransactionId()) == null) {
                errorMsg = String.format("Transaction with UUID %s doesn't exist in database.", entity.getTransactionId());
                log.error(errorMsg);
                throw new NotFoundException(Transaction.class, "UUID", entity.getTransactionId());
            }

            Transaction updatedTransaction = this.transactionRepository.save(entity);
            if (updatedTransaction == null) {
                errorMsg = String.format("Failed to updated an Transaction with UUID '%s'.", entity.getTransactionId());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            log.info(String.format("Successfully updated an transaction with UUID '%s'.", updatedTransaction.getTransactionId()));
            return updatedTransaction;
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while creating an transaction. Exception: %s", e);
            log.error(errorMsg);
            throw e;
        }
    }

    @Override
    public void delete(UUID id) {
        String errorMsg = EMPTY_STRING;
        try {
            if (id == null) {
                log.error("Transaction entity UUID which needs to be deleted cannot be null.");
                throw new NullPointerException("Transaction entity UUID which needs to be deleted cannot be null.");
            }

            if (get(id) == null) {
                errorMsg = String.format("Transaction with UUID %s doesn't exist in database.", id);
                log.error(errorMsg);
                throw new NotFoundException(Transaction.class, "UUID", id);
            }

            this.transactionRepository.deleteById(id);
            log.info(String.format("Successfully deleted an transaction with UUID '%s'.", id));
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while deleting an transaction with UUID '%s'. Exception: %s", id, e);
            log.error(errorMsg);
            throw e;
        }
    }

    public List<Transaction> getMiniStatement(UUID accountId, Instant startDate, Instant endDate) {
        String errorMsg = EMPTY_STRING;
        try {
            if ((accountId == null) || (startDate == null) || (endDate == null)) {
                errorMsg = String.format("Parameter cannot be null. accountId: %s, startDate: %s and endDate: %s", accountId, startDate, endDate);
                log.error(errorMsg);
                throw new NullPointerException(errorMsg);
            }

            return this.transactionRepository.getTransactionsForAccountInDateRange(
                    accountId,
                    startDate,
                    endDate,
                    Sort.by(Sort.Order.desc("createdAt")));
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while fetching miniStatement of an Account with UUID '%s'. Exception: %s", accountId, e);
            log.error(errorMsg);
            throw e;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Private methods">
    private boolean validateTransaction(Transaction transaction) {
        if (!this.transactionValidationService.validate(transaction)) {
            log.error("Transaction is invalid.");
            return false;
        }
        return true;
    }
    //</editor-fold>
}
