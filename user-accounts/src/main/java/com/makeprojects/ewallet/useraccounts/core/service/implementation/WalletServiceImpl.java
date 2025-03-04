package com.makeprojects.ewallet.useraccounts.core.service.implementation;

import com.makeprojects.ewallet.shared.core.definition.DelegatedTransactionAccount;
import com.makeprojects.ewallet.shared.core.enums.AccountEnums;
import com.makeprojects.ewallet.shared.core.enums.transaction.TransactionEnums.TransactionType;
import com.makeprojects.ewallet.shared.database.model.BankAccount;
import com.makeprojects.ewallet.shared.exceptions.NotFoundException;
import com.makeprojects.ewallet.shared.database.model.Wallet;
import com.makeprojects.ewallet.shared.database.model.User;
import com.makeprojects.ewallet.useraccounts.core.service.definition.BankAccountService;
import com.makeprojects.ewallet.useraccounts.core.service.definition.WalletService;
import com.makeprojects.ewallet.useraccounts.dto.DTAccount.DTAccountRequestDTO;
import com.makeprojects.ewallet.useraccounts.dto.DTAccount.DTAccountResponseDTO;
import com.makeprojects.ewallet.useraccounts.database.repository.WalletRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final BankAccountService bankAccountService;

    private static final String EMPTY_STRING = "";

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository, BankAccountService bankAccountService) {
        this.walletRepository = walletRepository;
        this.bankAccountService = bankAccountService;
    }

    public boolean isLoggedInUserAccount(UUID accountId) {
        String tokenUsername = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Wallet requestAccount = this.get(accountId);
        String requestUsername = requestAccount.getUser().getUserName();

        return tokenUsername.equals(requestUsername);
    }

    //<editor-fold desc="WalletService Implementation">
    @Override
    public Wallet get(UUID id) {
        String errorMsg = EMPTY_STRING;
        try {
            if (id == null) {
                errorMsg = String.format("UUID of wallet cannot be null.");
                log.error(errorMsg);
                throw new NullPointerException(errorMsg);
            }

            Optional<Wallet> optionalWallet = this.walletRepository.findById(id);
            if (optionalWallet.isEmpty()) {
                errorMsg = String.format("Wallet with UUID %s is not found.", id);
                log.error(errorMsg);
                throw new NotFoundException(Wallet.class, "UUID", id);
            }

            log.info(String.format("Successfully retrieved wallet with UUID '%s'.", id));
            return optionalWallet.get();
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while retrieving wallet with UUID '%s'. Exception: %s", id, e);
            log.error(errorMsg);
            throw e;
        }
    }

    @Override
    public List<Wallet> getAll() {
        try {
            List<Wallet> walletList = this.walletRepository.findAll();
            if (walletList.isEmpty()) {
                log.error("No wallet found.");
            }

            log.info("Successfully retrieved wallets list.");
            return walletList;
        } catch (Exception e) {
            log.error("Exception occurred while retrieving list of wallets.");
            throw e;
        }
    }

    @Override
    public Wallet create(Wallet entity) {
        String errorMsg = EMPTY_STRING;
        try {
            if (entity == null) {
                log.error("Wallet entity cannot be null.");
                throw new NullPointerException("Wallet entity cannot be null.");
            }

            if ((entity.getAccountId() != null) && (get(entity.getAccountId()) != null)) {
                errorMsg = String.format("Wallet with UUID %s already exists.", entity.getAccountId());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            Wallet createdWallet = this.walletRepository.save(entity);
            if (createdWallet == null) {
                log.error("Failed to create a Wallet.");
                throw new RuntimeException("Failed to create a Wallet.");
            }

            log.info(String.format("Successfully created a Wallet with UUID '%s'.", createdWallet.getAccountId()));
            return createdWallet;
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while creating a Wallet. Exception: %s", e);
            log.error(errorMsg);
            throw e;
        }
    }

    @Override
    public Wallet update(Wallet entity) {
        String errorMsg = EMPTY_STRING;
        try {
            if (entity == null) {
                log.error("Wallet entity which needs to be updated cannot be null.");
                throw new NullPointerException("Wallet entity cannot be null.");
            }

            if (get(entity.getAccountId()) == null) {
                errorMsg = String.format("Wallet with UUID %s doesn't exist in database.", entity.getAccountId());
                log.error(errorMsg);
                throw new NotFoundException(Wallet.class, "UUID", entity.getAccountId());
            }

            Wallet updatedWallet = this.walletRepository.save(entity);
            if (updatedWallet == null) {
                errorMsg = String.format("Failed to updated a Wallet with UUID '%s'.", entity.getAccountId());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            log.info(String.format("Successfully updated a Wallet with UUID '%s'.", updatedWallet.getAccountId()));
            return updatedWallet;
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while creating a Wallet. Exception: %s", e);
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

//    @Override
//    public List<Transaction> getMiniStatementOfUserWallet(UUID walletId) {
////        return this.transactionService.getMiniStatement(walletId, Instant.now().minus(7, ChronoUnit.DAYS), Instant.now());
//        throw new RuntimeException();
//    }

    /**
     * Gets all linked bankAccounts
     * @param walletId UUID of wallet
     * @return List of BankAccount
     */
    @Override
    public List<DTAccountResponseDTO> getLinkedBankAccountsInWallet(UUID walletId) {
        try {
            Wallet wallet = this.get(walletId);

            List<BankAccount> linkedBankAccountList = wallet.getLinkedBankAccounts();
            if (linkedBankAccountList.isEmpty()) {
                log.error("No linked BankAccount found.");
            }

            List<DTAccountResponseDTO> bankAccountDTOs = linkedBankAccountList.stream()
                                                                .map(acc ->
                                                                    DTAccountResponseDTO.builder()
                                                                                .bankAccountId(acc.getAccountId())
                                                                                .accountNumber(acc.getAccountNumber())
                                                                                .bankName(acc.getBankName())
                                                                                .build()
                                                                ).collect(Collectors.toList());

            log.info("Successfully retrieved linked BankAccount list.");
            return bankAccountDTOs;
        } catch (Exception e) {
            log.error("Exception occurred while retrieving list of linked BankAccount.");
            throw e;
        }
    }

    /**
     * Gets default BankAccount linked in Wallet\
     * @param walletId UUID of walled
     * @return Default BankAccount
     */
    @Override
    public DTAccountResponseDTO getDefaultBankAccountInWallet(UUID walletId) {
        try {
            Wallet wallet = this.get(walletId);
            BankAccount bankAccount = wallet.getDefaultBankAccount();

            log.info("Successfully fetched default bankAccount from wallet with UUID {}.", wallet.getAccountId());

            return DTAccountResponseDTO.builder()
                    .bankAccountId(bankAccount.getAccountId())
                    .accountNumber(bankAccount.getAccountNumber())
                    .bankName(bankAccount.getBankName())
                    .build();
        } catch (Exception e) {
            String error = String.format("Exception while default bankAccount from wallet with UUID '%s'. Exception: %s", walletId, e);
            log.error(error);
            throw e;
        }
    }

    /**
     * Unlinks a BankAccount from the wallet
     * @param accountId UUID of BankAccount
     * @return true is BankAccount is unlinked successfully from Wallet, false otherwise
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlinkBankAccountFromWallet(UUID accountId) {
        String errorMsg = EMPTY_STRING;
        try {
            BankAccount bankAccount = this.bankAccountService.get(accountId);
            Wallet parentWallet = bankAccount.getWallet();

            if (parentWallet == null) {
                log.error(String.format("BankAccount with UUID '%s' is not linked to any wallet.", accountId));
                throw new RuntimeException(String.format("BankAccount with UUID '%s' is not linked to any wallet.", accountId));
            }

            parentWallet.unlinkBankAccount(bankAccount);
            this.update(parentWallet);
            log.info(String.format("Successfully unlinked BankAccount with UUID '%s' from wallet with UUID '%s'.", accountId, parentWallet.getAccountId()));
            return true;
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while unlinking a bankAccount with UUID '%s'. Exception: %s", accountId, e);
            log.error(errorMsg);
            throw e;
        }
    }

    /**
     * Unlinks a BankAccount to the wallet
     * @param bankAccountRequestDTO BankAccount DTO which will be used to create BankAccount link to a wallet
     * @param walletId UUID of wallet to which BankAccount needs to be linked
     * @return linked BankAccount
     */
    @Override
    public BankAccount linkBankAccountInWallet(DTAccountRequestDTO bankAccountRequestDTO, UUID walletId) {
        String errorMsg = EMPTY_STRING;
        try {
            DelegatedTransactionAccount DTAccount = this.bankAccountService.addDTAccount(bankAccountRequestDTO, walletId);
            if(DTAccount instanceof BankAccount bankAccount) {
                return bankAccount;
            }

            throw new RuntimeException("Could not add/link BankAccount to Wallet.");
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while linking a new bankAccount with wallet UUID '%s'. Exception: %s", walletId, e);
            log.error(errorMsg);
            throw e;
        }
    }

    /**
     * Completes KYC of wallet
     * @param wallet Wallet obj
     * @return updated Wallet
     */
    @Override
    public Wallet completeKyc(Wallet wallet) {
        try {
            wallet.setKycComplete(true);
            wallet.setAccountStatus(AccountEnums.AccountStatus.ACTIVE);

            return this.update(wallet);
        } catch (RuntimeException e) {
            log.error(String.format("Exception while completing KYC of Walled with UUID '%s'. Exception: %s", wallet.getAccountId(), e));
            throw e;
        }
    }

    /**
     * Transfers money between two different monetary accounts
     * @param accountId UUID of the account initiating the transaction
     * @param targetAccountId UUID of the recipient account
     * @param amount value of amount to be transferred
     * @param transactionType type of transaction, e.g. WALLET_TO_WALLET, WALLET_TO_BANK, BANK_TO_WALLET
     * @return true if transfer of money is successful, false otherwise
     */
    @Override
    public boolean transferMoney(UUID accountId, UUID targetAccountId, double amount, TransactionType transactionType) {
        try {
            if (accountId == null || targetAccountId == null) {
                log.error(String.format("Either accountID '%s' or targetAccount ID '%s' is null.", accountId, targetAccountId));
                return false;
            }

            if (amount <= 0.0) {
                log.error(String.format("Amount '%s' is 0 or less than 0.", amount));
                return false;
            }

            boolean isSuccess = transferMoneyCore(accountId, targetAccountId, amount, transactionType);

            log.info(String.format("Successfully transferred money %s from account '%s' to account '%s'.", amount, accountId, targetAccountId));
            return isSuccess;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Private methods">
    private boolean transferMoneyCore(UUID accountId, UUID targetAccountId, double amount, TransactionType transactionType) {
        boolean isSuccess = false;

        for (int i=0; i<3; i++) {       // re-try 3 times
            try {
                switch (transactionType) {
                    case WALLET_TO_WALLET:
                        isSuccess = handleWalletToWalletTransaction(accountId, targetAccountId, amount);
                        break;

                    case BANK_TO_WALLET:
                        isSuccess = handleBankToWalletTransaction(accountId, targetAccountId, amount);
                        break;

                    case WALLET_TO_BANK:
                        isSuccess = handleWalletToBankTransaction(accountId, targetAccountId, amount);
                        break;

                    default:
                        break;
                }

                if(isSuccess) return true;
            } catch (OptimisticLockException opLE) {
                log.warn(String.format("Optimistic Locking failure for accountID '%s', targetAccountId '%s', retrying... Attempt: %s", accountId, targetAccountId, i+1));
            }
        }

        log.error(String.format("Failed to transfer money from account UUID '%s' to account UUID '%s' after re-trying 3 times", accountId, targetAccountId));
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    private boolean handleWalletToWalletTransaction(UUID walletId, UUID targetWalletId, double amount) throws OptimisticLockException {
        boolean result = false;

        Wallet wallet = this.get(walletId);
        Wallet targetWallet = this.get(targetWalletId);

        wallet.transfer(targetWallet, amount, false);
        this.saveWallets(List.of(wallet, targetWallet));
        result = true;

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    private boolean handleBankToWalletTransaction(UUID bankAccountId, UUID targetWalletId, double amount) throws OptimisticLockException {
        boolean result = false;

        BankAccount bankAccount = this.bankAccountService.get(bankAccountId);
        Wallet targetWallet = this.get(targetWalletId);

        // ToDo: write logic  to deduct amount from BankAccount

        targetWallet.setBalance(targetWallet.getBalance() + amount);
        this.update(targetWallet);
        result = true;

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    private boolean handleWalletToBankTransaction(UUID walletId, UUID targetBankAccountId, double amount) throws OptimisticLockException {
        boolean result = false;

        BankAccount targetBankAccount = this.bankAccountService.get(targetBankAccountId);
        Wallet wallet = this.get(walletId);

        wallet.setBalance(wallet.getBalance() - amount);

        // ToDo: write logic  to credit amount in BankAccount

        this.update(wallet);
        result = true;

        return result;
    }
    //</editor-fold>
}
