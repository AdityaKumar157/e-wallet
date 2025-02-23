package com.makeprojects.ewallet.useraccounts.core.service.definition;

import com.makeprojects.ewallet.shared.core.definition.CRUDService;
import com.makeprojects.ewallet.shared.database.model.Wallet;
import com.makeprojects.ewallet.shared.database.model.Transaction;
import com.makeprojects.ewallet.shared.database.model.User;
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
}
