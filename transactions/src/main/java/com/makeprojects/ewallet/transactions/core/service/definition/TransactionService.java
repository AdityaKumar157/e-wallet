package com.makeprojects.ewallet.transactions.core.service.definition;

import com.makeprojects.ewallet.shared.core.definition.CRUDService;
import com.makeprojects.ewallet.shared.database.model.Transaction;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TransactionService extends CRUDService<Transaction> {

    List<Transaction> getMiniStatement(UUID accountId, Instant startDate, Instant endDate);
}
