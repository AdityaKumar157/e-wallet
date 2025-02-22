package com.makeprojects.ewallet.useraccounts.core.service.implementation;

import com.makeprojects.ewallet.shared.exceptions.NotFoundException;
import com.makeprojects.ewallet.shared.database.model.Transaction;
import com.makeprojects.ewallet.shared.database.model.Wallet;
import com.makeprojects.ewallet.shared.database.model.User;
import com.makeprojects.ewallet.transactions.core.service.definition.TransactionService;
import com.makeprojects.ewallet.useraccounts.core.service.definition.AccountService;
import com.makeprojects.ewallet.useraccounts.dto.TransactionDto;
import com.makeprojects.ewallet.useraccounts.mapper.TransactionMapper;
import com.makeprojects.ewallet.useraccounts.database.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    private static final String EMPTY_STRING = "";

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, TransactionService transactionService, TransactionMapper transactionMapper) {
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
    }

    public boolean isLoggedInUserAccount(UUID accountId) {
        String tokenUsername = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Wallet requestAccount = this.get(accountId);
        String requestUsername = requestAccount.getUser().getUserName();

        return tokenUsername.equals(requestUsername);
    }

    //<editor-fold desc="AccountService Implementation">
    @Override
    public Wallet get(UUID id) {
        String errorMsg = EMPTY_STRING;
        try {
            if (id == null) {
                errorMsg = String.format("UUID of account cannot be null.");
                log.error(errorMsg);
                throw new NullPointerException(errorMsg);
            }

            Optional<Wallet> optionalAccount = this.accountRepository.findById(id);
            if (optionalAccount.isEmpty()) {
                errorMsg = String.format("Account with UUID %s is not found.", id);
                log.error(errorMsg);
                throw new NotFoundException(Wallet.class, "UUID", id);
            }

            log.info(String.format("Successfully retrieved account with UUID '%s'.", id));
            return optionalAccount.get();
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while retrieving account with UUID '%s'. Exception: %s", id, e);
            log.error(errorMsg);
            throw e;
        }
    }

    @Override
    public List<Wallet> getAll() {
        try {
            List<Wallet> accountList = this.accountRepository.findAll();
            if (accountList.isEmpty()) {
                log.error("No account found.");
            }

            log.info("Successfully retrieved accounts list.");
            return accountList;
        } catch (Exception e) {
            log.error("Exception occurred while retrieving list of accounts.");
            throw e;
        }
    }

    @Override
    public Wallet create(Wallet entity) {
        String errorMsg = EMPTY_STRING;
        try {
            if (entity == null) {
                log.error("Account entity cannot be null.");
                throw new NullPointerException("Account entity cannot be null.");
            }

            if ((entity.getAccountId() != null) && (get(entity.getAccountId()) != null)) {
                errorMsg = String.format("Account with UUID %s already exists.", entity.getAccountId());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            Wallet createdAccount = this.accountRepository.save(entity);
            if (createdAccount == null) {
                log.error("Failed to create an account.");
                throw new RuntimeException("Failed to create an Account.");
            }

            log.info(String.format("Successfully created an account with UUID '%s'.", createdAccount.getAccountId()));
            return createdAccount;
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while creating an account. Exception: %s", e);
            log.error(errorMsg);
            throw e;
        }
    }

    @Override
    public Wallet update(Wallet entity) {
        String errorMsg = EMPTY_STRING;
        try {
            if (entity == null) {
                log.error("Account entity which needs to be updated cannot be null.");
                throw new NullPointerException("Account entity cannot be null.");
            }

            if (get(entity.getAccountId()) == null) {
                errorMsg = String.format("Account with UUID %s doesn't exist in database.", entity.getAccountId());
                log.error(errorMsg);
                throw new NotFoundException(Wallet.class, "UUID", entity.getAccountId());
            }

            Wallet updatedAccount = this.accountRepository.save(entity);
            if (updatedAccount == null) {
                errorMsg = String.format("Failed to updated an Account with UUID '%s'.", entity.getAccountId());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            log.info(String.format("Successfully updated an account with UUID '%s'.", updatedAccount.getAccountId()));
            return updatedAccount;
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while creating an account. Exception: %s", e);
            log.error(errorMsg);
            throw e;
        }
    }

    @Override
    public void delete(UUID id) {
        String errorMsg = EMPTY_STRING;
        try {
            if (id == null) {
                log.error("Account entity UUID which needs to be deleted cannot be null.");
                throw new NullPointerException("Account entity UUID which needs to be deleted cannot be null.");
            }

            if (get(id) == null) {
                errorMsg = String.format("Account with UUID %s doesn't exist in database.", id);
                log.error(errorMsg);
                throw new NotFoundException(Wallet.class, "UUID", id);
            }

            this.accountRepository.deleteById(id);
            log.info(String.format("Successfully deleted an account with UUID '%s'.", id));
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while deleting an account with UUID '%s'. Exception: %s", id, e);
            log.error(errorMsg);
            throw e;
        }
    }

    @Override
    public Wallet addAccount(User user) {
        try {
            Wallet account = Wallet.builder()
                    .user(user)
                    .build();

            this.accountRepository.save(account);
            log.info("Account with ID {} for user with ID {} is created successfully.", account.getAccountId(), user.getUserId());
            return account;
        } catch (Exception e) {
            String error = String.format("Exception while creating account for user with ID %s. Exception: %s", user.getUserId(), e);
            log.error(error);
            throw e;
        }
    }

    @Override
    public Wallet getAccountByUserId(UUID userId) {
        try {
            Optional<Wallet> optionalAccount = this.accountRepository.findByUserId(userId);
            if(optionalAccount.isEmpty()) {
                NotFoundException ex = new NotFoundException(Wallet.class, "userId", userId);
                log.error(ex.getMessage());
                throw ex;
            }

            Wallet account = optionalAccount.get();
            log.info("Successfully fetched account with userId {}.", account.getUser().getUserId());
            return account;
        } catch (Exception e) {
            String error = String.format("Exception while fetching account with userID %s. Exception: %s", userId, e);
            log.error(error);
            throw e;
        }
    }

    @Override
    public Wallet getAccountByUser(User user) {
        try {
            return this.getAccountByUserId(user.getUserId());
        } catch (Exception e) {
            String error = String.format("Exception while fetching account with userID %s. Exception: %s", user.getUserId(), e);
            log.error(error);
            throw e;
        }
    }

    @Override
    public List<Wallet> getAllAccountsByIds(List<UUID> accountIds) {
        return this.accountRepository.findAllById(accountIds);
    }

    @Override
    public void saveAccounts(Collection<Wallet> accountsCollection) {
        try {
            this.accountRepository.saveAll(accountsCollection);
            log.info("Saved all {} accounts.", accountsCollection.size());
        } catch (Exception e) {
            String error = String.format("Exception while saving one of the %s accounts. Exception: %s", accountsCollection.size(), e);
            log.error(error);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Transaction sendMoney(TransactionDto transactionDto) {
        Transaction transaction = this.transactionMapper.mapToTransaction(transactionDto);
        Wallet senderAccount = transaction.getSenderAccount();
        Wallet receiverAccount = transaction.getReceiverAccount();
        Transaction createdTransaction = this.transactionService.create(transaction);
        senderAccount.send(receiverAccount, transaction.getAmount());
        this.accountRepository.saveAll(List.of(senderAccount, receiverAccount));
        return createdTransaction;
    }

    @Override
    public List<Transaction> getMiniStatementOfUserAccount(UUID accountId) {
        return this.transactionService.getMiniStatement(accountId, Instant.now().minus(7, ChronoUnit.DAYS), Instant.now());
    }
    //</editor-fold>
}
