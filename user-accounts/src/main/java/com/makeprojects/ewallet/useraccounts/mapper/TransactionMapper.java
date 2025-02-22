package com.makeprojects.ewallet.useraccounts.mapper;

import com.makeprojects.ewallet.shared.exceptions.AccountNotFoundException;
import com.makeprojects.ewallet.shared.database.model.Wallet;
import com.makeprojects.ewallet.shared.database.model.Transaction;
import com.makeprojects.ewallet.useraccounts.dto.TransactionDto;
import com.makeprojects.ewallet.useraccounts.database.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class TransactionMapper {

    private final AccountRepository accountRepository;

    @Autowired
    public TransactionMapper(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Transaction mapToTransaction(TransactionDto transactionDto) {
        List<Wallet> accounts = this.accountRepository.findAllById(List.of(transactionDto.getSenderAccId(), transactionDto.getReceiverAccId()));
        Wallet senderAccount = null;
        Wallet receiverAccount = null;

        try {
            senderAccount = accounts.stream().filter(acc -> acc.getAccountId().equals(transactionDto.getSenderAccId())).toList().getFirst();
            receiverAccount = accounts.stream().filter(acc -> acc.getAccountId().equals(transactionDto.getReceiverAccId())).toList().getFirst();
        } catch (NoSuchElementException ex) {
            throw new AccountNotFoundException(Wallet.class, ex);
        }

        return Transaction.builder()
                .senderAccount(senderAccount)
                .receiverAccount(receiverAccount)
                .amount(transactionDto.getAmount())
                .createdAt(Instant.now())
                .build();
    }
}
