package com.makeprojects.ewallet.useraccounts.core.service.definition;

import com.makeprojects.ewallet.shared.core.definition.CRUDService;
import com.makeprojects.ewallet.shared.database.model.Account;
import com.makeprojects.ewallet.shared.database.model.Transaction;
import com.makeprojects.ewallet.shared.database.model.User;
import com.makeprojects.ewallet.useraccounts.dto.TransactionDto;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface AccountService extends CRUDService<Account> {

    Account addAccount(User user);

    Account getAccountByUserId(UUID userId);

    Account getAccountByUser(User user);

    List<Account> getAllAccountsByIds(List<UUID> accountIds);

    void saveAccounts(Collection<Account> accountsCollection);

    Transaction sendMoney(TransactionDto transactionDto);

    List<Transaction> getMiniStatementOfUserAccount(UUID accountId);
}
