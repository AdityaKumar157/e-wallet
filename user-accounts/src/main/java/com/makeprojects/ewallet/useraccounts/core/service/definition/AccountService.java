package com.makeprojects.ewallet.useraccounts.core.service.definition;

import com.makeprojects.ewallet.shared.core.definition.CRUDService;
import com.makeprojects.ewallet.shared.database.model.Wallet;
import com.makeprojects.ewallet.shared.database.model.Transaction;
import com.makeprojects.ewallet.shared.database.model.User;
import com.makeprojects.ewallet.useraccounts.dto.TransactionDto;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface AccountService extends CRUDService<Wallet> {

    Wallet addAccount(User user);

    Wallet getAccountByUserId(UUID userId);

    Wallet getAccountByUser(User user);

    List<Wallet> getAllAccountsByIds(List<UUID> accountIds);

    void saveAccounts(Collection<Wallet> accountsCollection);

    Transaction sendMoney(TransactionDto transactionDto);

    List<Transaction> getMiniStatementOfUserAccount(UUID accountId);
}
