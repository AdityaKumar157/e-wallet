package com.makeprojects.ewallet.transactions.database.repository;

import com.makeprojects.ewallet.shared.database.model.Transaction;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("""
            select t from Transaction t
            where t.senderAccount.walletId = ?1 or t.receiverAccount.walletId = ?1 and t.createdAt between ?2 and ?3""")
    List<Transaction> getTransactionsForAccountInDateRange(UUID accountId, Instant createdAtStart, Instant createdAtEnd, Sort sort);
}
