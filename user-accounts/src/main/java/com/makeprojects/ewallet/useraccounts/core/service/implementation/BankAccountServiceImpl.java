package com.makeprojects.ewallet.useraccounts.core.service.implementation;

import com.makeprojects.ewallet.shared.core.definition.AutonomousTransactionAccount;
import com.makeprojects.ewallet.shared.core.definition.DelegatedTransactionAccount;
import com.makeprojects.ewallet.shared.database.model.BankAccount;
import com.makeprojects.ewallet.shared.database.model.Wallet;
import com.makeprojects.ewallet.shared.exceptions.NotFoundException;
import com.makeprojects.ewallet.useraccounts.core.service.definition.BankAccountService;
import com.makeprojects.ewallet.useraccounts.core.service.definition.WalletService;
import com.makeprojects.ewallet.useraccounts.database.repository.BankAccountRepository;
import com.makeprojects.ewallet.useraccounts.database.repository.WalletRepository;
import com.makeprojects.ewallet.useraccounts.dto.DTAccount.DTAccountRequestDTO;
import com.makeprojects.ewallet.useraccounts.dto.DelegatedTransactionAccountDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private BankAccountRepository bankAccountRepository;
    private WalletRepository walletRepository;

    private static final String EMPTY_STRING = "";

    @Autowired
    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, WalletRepository walletRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.walletRepository = walletRepository;
    }

    /**
     * Retrieves the BankAccount type entity from database for the specified id.
     * @param id UUID of BankAccount
     * @return entity of type BankAccount
     */
    @Override
    public BankAccount get(UUID id) {
        String errorMsg = EMPTY_STRING;
        try {
            if (id == null) {
                errorMsg = String.format("UUID of bank account cannot be null.");
                log.error(errorMsg);
                throw new NullPointerException(errorMsg);
            }

            Optional<BankAccount> optionalAccount = this.bankAccountRepository.findById(id);
            if (optionalAccount.isEmpty()) {
                errorMsg = String.format("BankAccount with UUID %s is not found.", id);
                log.error(errorMsg);
                throw new NotFoundException(BankAccount.class, "UUID", id);
            }

            log.info(String.format("Successfully retrieved BankAccount with UUID '%s'.", id));
            return optionalAccount.get();
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while retrieving BankAccount with UUID '%s'. Exception: %s", id, e);
            log.error(errorMsg);
            throw e;
        }
    }

    /**
     * Retrieves list of all entities from database of type BankAccount.
     * @return list of type BankAccount
     */
    @Override
    public List<BankAccount> getAll() {
        try {
            List<BankAccount> accountList = this.bankAccountRepository.findAll();
            if (accountList.isEmpty()) {
                log.error("No bank account found.");
            }

            log.info("Successfully retrieved bank accounts list.");
            return accountList;
        } catch (Exception e) {
            log.error("Exception occurred while retrieving list of bank accounts.");
            throw e;
        }
    }

    /**
     * Creates/Adds a new entity of type BankAccount in database
     * @param entity Entity of type BankAccount which needs to be added in database
     * @return BankAccount entity which is created in database
     */
    @Override
    public BankAccount create(BankAccount entity) {
        String errorMsg = EMPTY_STRING;
        try {
            if (entity == null) {
                log.error("BankAccount entity cannot be null.");
                throw new NullPointerException("BankAccount entity cannot be null.");
            }

            if ((entity.getAccountId() != null) && (get(entity.getAccountId()) != null)) {
                errorMsg = String.format("BankAccount with UUID %s already exists.", entity.getAccountId());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            BankAccount createdAccount = this.bankAccountRepository.save(entity);
            if (createdAccount == null) {
                log.error("Failed to create an BankAccount.");
                throw new RuntimeException("Failed to create an BankAccount.");
            }

            log.info(String.format("Successfully created an BankAccount with UUID '%s'.", createdAccount.getAccountId()));
            return createdAccount;
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while creating an BankAccount. Exception: %s", e);
            log.error(errorMsg);
            throw e;
        }
    }

    /**
     * Updates the existing entity of type BankAccount in database
     * @param entity Entity of type BankAccount which needs to be updated/saved in database
     * @return BankAccount entity which is updated in database
     */
    @Override
    public BankAccount update(BankAccount entity) {
        String errorMsg = EMPTY_STRING;
        try {
            if (entity == null) {
                log.error("BankAccount entity which needs to be updated cannot be null.");
                throw new NullPointerException("BankAccount entity cannot be null.");
            }

            if (get(entity.getAccountId()) == null) {
                errorMsg = String.format("BankAccount with UUID %s doesn't exist in database.", entity.getAccountId());
                log.error(errorMsg);
                throw new NotFoundException(BankAccount.class, "UUID", entity.getAccountId());
            }

            BankAccount updatedAccount = this.bankAccountRepository.save(entity);
            if (updatedAccount == null) {
                errorMsg = String.format("Failed to updated an BankAccount with UUID '%s'.", entity.getAccountId());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            log.info(String.format("Successfully updated an BankAccount with UUID '%s'.", updatedAccount.getAccountId()));
            return updatedAccount;
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while creating an BankAccount. Exception: %s", e);
            log.error(errorMsg);
            throw e;
        }
    }

    /**
     * Deletes the existing entity with specified UUID
     * @param id UUID of entity BankAccount
     */
    @Override
    public void delete(UUID id) {
        String errorMsg = EMPTY_STRING;
        try {
            if (id == null) {
                log.error("BankAccount entity UUID which needs to be deleted cannot be null.");
                throw new NullPointerException("BankAccount entity UUID which needs to be deleted cannot be null.");
            }

            if (get(id) == null) {
                errorMsg = String.format("BankAccount with UUID %s doesn't exist in database.", id);
                log.error(errorMsg);
                throw new NotFoundException(BankAccount.class, "UUID", id);
            }

            this.bankAccountRepository.deleteById(id);
            log.info(String.format("Successfully deleted a BankAccount with UUID '%s'.", id));
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while deleting a BankAccount with UUID '%s'. Exception: %s", id, e);
            log.error(errorMsg);
            throw e;
        }
    }

    /**
     * adds a bank account and links it with specified Wallet
     * @param accountDTO BankAccount DTO received in request
     * @param walletId wallet UUID which will link created bank account
     * @return created BankAccount object
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DelegatedTransactionAccount addDTAccount(DTAccountRequestDTO accountDTO, UUID walletId) {
        try {
            BankAccount bankAccount = BankAccount.builder()
                    .accountHolderName(accountDTO.getAccountHolderName())
                    .accountNumber(accountDTO.getAccountNumber())
                    .bankName(accountDTO.getBankName())
                    .ifscCode(accountDTO.getIfscCode())
                    .upiId(accountDTO.getUpiId())
                    .accountStatus(accountDTO.getAccountStatus())
                    .build();

            Optional<Wallet> optionalWallet = this.walletRepository.findById(walletId);
            if(optionalWallet.isEmpty()) {
                throw new NotFoundException(Wallet.class);
            }

            (optionalWallet.get()).linkBankAccount(bankAccount);

            this.bankAccountRepository.save(bankAccount);
            log.info("BankAccount with ID {} for wallet with ID {} is created successfully.", bankAccount.getAccountId(), walletId);
            return bankAccount;
        } catch (Exception e) {
            String error = String.format("Exception while creating BankAccount for wallet with ID %s. Exception: %s", walletId, e);
            log.error(error);
            throw e;
        }
    }

    /**
     * Gets BankAccounts linked with specified wallet ID.
     * @param walletId UUID of wallet
     * @return List of BankAccount linked with specified wallet ID
     */
    @Override
    public List<DelegatedTransactionAccountDTO> getDTAccountsInfoByATId(UUID walletId) {
        try {
            List<DelegatedTransactionAccountDTO> linkedBankAccountDTOs = this.bankAccountRepository.findByWallet_WalletId(walletId);
            if(linkedBankAccountDTOs.isEmpty()) {
                NotFoundException ex = new NotFoundException(BankAccount.class, "walletId", walletId);
                log.error(ex.getMessage());
                throw ex;
            }

            log.info("Successfully fetched linkedBankAccounts with walletId {}.", walletId);
            return linkedBankAccountDTOs;
        } catch (Exception e) {
            String error = String.format("Exception while fetching linkedBankAccounts with walletId %s. Exception: %s", walletId, e);
            log.error(error);
            throw e;
        }
    }

    /**
     * Gets BankAccounts linked with specified Wallet.
     * @param wallet wallet to get check linked Bank accounts
     * @return List of BankAccount linked with specified wallet
     */
    @Override
    public List<DelegatedTransactionAccountDTO> getDTAccountsByAT(AutonomousTransactionAccount wallet) {
        try {
            return this.getDTAccountsInfoByATId(wallet.getAccountId());
        } catch (Exception e) {
            String error = String.format("Exception while fetching BankAccounts with walletID %s. Exception: %s", wallet.getAccountId(), e);
            log.error(error);
            throw e;
        }
    }
}
