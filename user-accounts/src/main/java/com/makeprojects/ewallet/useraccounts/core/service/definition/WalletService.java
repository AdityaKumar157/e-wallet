package com.makeprojects.ewallet.useraccounts.core.service.definition;

import com.makeprojects.ewallet.shared.core.definition.CRUDService;
import com.makeprojects.ewallet.shared.core.enums.transaction.TransactionEnums.TransactionType;
import com.makeprojects.ewallet.shared.database.model.BankAccount;
import com.makeprojects.ewallet.shared.database.model.Wallet;
import com.makeprojects.ewallet.shared.database.model.Transaction;
import com.makeprojects.ewallet.shared.database.model.User;
import com.makeprojects.ewallet.useraccounts.dto.DTAccount.DTAccountRequestDTO;
import com.makeprojects.ewallet.useraccounts.dto.DTAccount.DTAccountResponseDTO;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface WalletService extends CRUDService<Wallet> {

    Wallet addWallet(User user);

    Wallet getWalletByUserId(UUID userId);

    Wallet getWalletByUser(User user);

    List<Wallet> getAllWalletsByIds(List<UUID> walletIds);

    void saveWallets(Collection<Wallet> walletsCollection);

    List<Transaction> getMiniStatementOfUserWallet(UUID walletId);

    /**
     * Gets all linked bankAccounts
     * @param walletId UUID of wallet
     * @return List of BankAccount
     */
    List<DTAccountResponseDTO> getLinkedBankAccountsInWallet(UUID walletId);

    /**
     * Gets default BankAccount linked in Wallet
     * @param walletId UUID of walled
     * @return Default BankAccount
     */
    DTAccountResponseDTO getDefaultBankAccountInWallet(UUID walletId);

    /**
     * Unlinks a BankAccount from the wallet
     * @param accountId UUID of BankAccount
     * @return true is BankAccount is unlinked successfully from Wallet, false otherwise
     */
    boolean unlinkBankAccountFromWallet(UUID accountId);

    /**
     * Unlinks a BankAccount to the wallet
     * @param bankAccountRequestDTO BankAccount DTO which will be used to create BankAccount link to a wallet
     * @param walletId UUID of wallet to which BankAccount needs to be linked
     * @return linked BankAccount
     */
    BankAccount linkBankAccountInWallet(DTAccountRequestDTO bankAccountRequestDTO, UUID walletId);

    /**
     * Completes KYC of wallet
     * @param wallet Wallet obj
     * @return updated Wallet
     */
    Wallet completeKyc(Wallet wallet);

    /**
     * Transfers money between two different monetary accounts
     * @param accountId UUID of the account initiating the transaction
     * @param targetAccountId UUID of the recipient account
     * @param amount value of amount to be transferred
     * @param transactionType type of transaction, e.g. WALLET_TO_WALLET, WALLET_TO_BANK, BANK_TO_WALLET
     * @return true if transfer of money is successful, false otherwise
     */
    boolean transferMoney(UUID accountId, UUID targetAccountId, double amount, TransactionType transactionType);
}
