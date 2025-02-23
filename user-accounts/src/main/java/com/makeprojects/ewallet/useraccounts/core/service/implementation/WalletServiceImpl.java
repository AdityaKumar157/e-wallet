package com.makeprojects.ewallet.useraccounts.core.service.implementation;

import com.makeprojects.ewallet.shared.exceptions.NotFoundException;
import com.makeprojects.ewallet.shared.database.model.Transaction;
import com.makeprojects.ewallet.shared.database.model.Wallet;
import com.makeprojects.ewallet.shared.database.model.User;
import com.makeprojects.ewallet.transactions.core.service.definition.TransactionService;
import com.makeprojects.ewallet.useraccounts.core.service.definition.WalletService;
import com.makeprojects.ewallet.useraccounts.dto.TransactionDto;
import com.makeprojects.ewallet.useraccounts.mapper.TransactionMapper;
import com.makeprojects.ewallet.useraccounts.database.repository.WalletRepository;
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
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    private static final String EMPTY_STRING = "";

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository, TransactionService transactionService, TransactionMapper transactionMapper) {
        this.walletRepository = walletRepository;
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

            Optional<Wallet> optionalAccount = this.walletRepository.findById(id);
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
            List<Wallet> accountList = this.walletRepository.findAll();
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

            Wallet createdAccount = this.walletRepository.save(entity);
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

            Wallet updatedAccount = this.walletRepository.save(entity);
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

            this.walletRepository.deleteById(id);
            log.info(String.format("Successfully deleted an account with UUID '%s'.", id));
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while deleting an account with UUID '%s'. Exception: %s", id, e);
            log.error(errorMsg);
            throw e;
        }
    }

    @Override
    public Wallet addWallet(User user) {
        try {
            Wallet wallet = Wallet.builder()
                    .user(user)
                    .build();

            this.walletRepository.save(wallet);
            log.info("Wallet with ID {} for user with ID {} is created successfully.", wallet.getAccountId(), user.getUserId());
            return wallet;
        } catch (Exception e) {
            String error = String.format("Exception while creating account for user with ID %s. Exception: %s", user.getUserId(), e);
            log.error(error);
            throw e;
        }
    }

    @Override
    public Wallet getWalletByUserId(UUID userId) {
        try {
            Optional<Wallet> optionalWallet = this.walletRepository.findByUserId(userId);
            if(optionalWallet.isEmpty()) {
                NotFoundException ex = new NotFoundException(Wallet.class, "userId", userId);
                log.error(ex.getMessage());
                throw ex;
            }

            Wallet wallet = optionalWallet.get();
            log.info("Successfully fetched wallet with userId {}.", wallet.getUser().getUserId());
            return wallet;
        } catch (Exception e) {
            String error = String.format("Exception while fetching wallet with userID %s. Exception: %s", userId, e);
            log.error(error);
            throw e;
        }
    }

    @Override
    public Wallet getWalletByUser(User user) {
        try {
            return this.getWalletByUserId(user.getUserId());
        } catch (Exception e) {
            String error = String.format("Exception while fetching wallet with userID %s. Exception: %s", user.getUserId(), e);
            log.error(error);
            throw e;
        }
    }

    @Override
    public List<Wallet> getAllWalletsByIds(List<UUID> walletIds) {
        return this.walletRepository.findAllById(walletIds);
    }

    @Override
    public void saveWallets(Collection<Wallet> walletsCollection) {
        try {
            this.walletRepository.saveAll(walletsCollection);
            log.info("Saved all {} wallets.", walletsCollection.size());
        } catch (Exception e) {
            String error = String.format("Exception while saving one of the %s wallets. Exception: %s", walletsCollection.size(), e);
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
        this.walletRepository.saveAll(List.of(senderAccount, receiverAccount));
        return createdTransaction;
    }

    @Override
    public List<Transaction> getMiniStatementOfUserWallet(UUID walletId) {
        return this.transactionService.getMiniStatement(walletId, Instant.now().minus(7, ChronoUnit.DAYS), Instant.now());
    }
    //</editor-fold>
}
