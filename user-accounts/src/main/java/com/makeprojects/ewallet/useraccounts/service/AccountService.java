package com.makeprojects.ewallet.useraccounts.service;

import com.makeprojects.ewallet.shared.exceptions.NotFoundException;
import com.makeprojects.ewallet.shared.model.Transaction;
import com.makeprojects.ewallet.shared.model.Account;
import com.makeprojects.ewallet.shared.model.User;
import com.makeprojects.ewallet.transactions.service.TransactionService;
import com.makeprojects.ewallet.useraccounts.dto.TransactionDto;
import com.makeprojects.ewallet.useraccounts.mapper.TransactionMapper;
import com.makeprojects.ewallet.useraccounts.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @Autowired
    public AccountService(AccountRepository accountRepository, TransactionService transactionService, TransactionMapper transactionMapper) {
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
    }

    public Account addAccount(User user) {
        try {
            Account account = Account.builder()
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

    public Account getAccountById(UUID accountId) {
        try {
            Optional<Account> optionalAccount = this.accountRepository.findById(accountId);
            if(optionalAccount.isEmpty()) {
                NotFoundException ex = new NotFoundException(Account.class, "accountID", accountId);
                log.error(ex.getMessage());
                throw ex;
            }

            Account account = optionalAccount.get();
            log.info("Successfully fetched account with ID {}.", account.getAccountId());
            return account;
        } catch (Exception e) {
            String error = String.format("Exception while fetching account with ID %s. Exception: %s", accountId, e);
            log.error(error);
            throw e;
        }
    }

    public Account getAccountByUserId(UUID userId) {
        try {
            Optional<Account> optionalAccount = this.accountRepository.findByUserId(userId);
            if(optionalAccount.isEmpty()) {
                NotFoundException ex = new NotFoundException(Account.class, "userId", userId);
                log.error(ex.getMessage());
                throw ex;
            }

            Account account = optionalAccount.get();
            log.info("Successfully fetched account with userId {}.", account.getUser().getUserId());
            return account;
        } catch (Exception e) {
            String error = String.format("Exception while fetching account with userID %s. Exception: %s", userId, e);
            log.error(error);
            throw e;
        }
    }

    public Account getAccountByUser(User user) {
        try {
            return this.getAccountByUserId(user.getUserId());
        } catch (Exception e) {
            String error = String.format("Exception while fetching account with userID %s. Exception: %s", user.getUserId(), e);
            log.error(error);
            throw e;
        }
    }

    public void saveAccounts(Collection<Account> accountsCollection) {
        try {
            this.accountRepository.saveAll(accountsCollection);
            log.info("Saved all {} accounts.", accountsCollection.size());
        } catch (Exception e) {
            String error = String.format("Exception while saving one of the %s accounts. Exception: %s", accountsCollection.size(), e);
            log.error(error);
            throw e;
        }
    }

    @Transactional
    public Transaction sendMoney(TransactionDto transactionDto) {
        Transaction transaction = this.transactionMapper.mapToTransaction(transactionDto);
        Account senderAccount = transaction.getSenderAccount();
        Account receiverAccount = transaction.getReceiverAccount();
        senderAccount.send(receiverAccount, transaction.getAmount());
        Transaction createdTransaction = this.transactionService.createTransaction(transaction);
        this.accountRepository.saveAll(List.of(senderAccount, receiverAccount));
        return createdTransaction;
    }

    public List<Account> getAllAccountsByIds(List<UUID> accountIds) {
        return this.accountRepository.findAllById(accountIds);
    }
}
