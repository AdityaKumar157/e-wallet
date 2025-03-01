package com.makeprojects.ewallet.useraccounts.core.service.definition;

import com.makeprojects.ewallet.shared.core.definition.CRUDService;
import com.makeprojects.ewallet.shared.database.model.BankAccount;
import com.makeprojects.ewallet.shared.database.model.Wallet;
import com.makeprojects.ewallet.shared.database.model.Transaction;
import com.makeprojects.ewallet.shared.database.model.User;
import com.makeprojects.ewallet.useraccounts.dto.DTAccount.DTAccountRequestDTO;
import com.makeprojects.ewallet.useraccounts.dto.DTAccount.DTAccountResponseDTO;
import com.makeprojects.ewallet.useraccounts.dto.TransactionDto;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface WalletService extends CRUDService<Wallet> {

    Wallet addWallet(User user);

    Wallet getWalletByUserId(UUID userId);

    Wallet getWalletByUser(User user);

    List<Wallet> getAllWalletsByIds(List<UUID> walletIds);

    void saveWallets(Collection<Wallet> walletsCollection);

    Transaction sendMoney(TransactionDto transactionDto);

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
}
