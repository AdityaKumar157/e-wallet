package com.makeprojects.ewallet.useraccounts.mapper;

import com.makeprojects.ewallet.shared.exceptions.AccountNotFoundException;
import com.makeprojects.ewallet.shared.model.Account;
import com.makeprojects.ewallet.shared.model.Transaction;
import com.makeprojects.ewallet.useraccounts.dto.TransactionDto;
import com.makeprojects.ewallet.useraccounts.repository.AccountRepository;
import com.makeprojects.ewallet.useraccounts.service.AccountService;
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
        List<Account> accounts = this.accountRepository.findAllById(List.of(transactionDto.getSenderAccId(), transactionDto.getReceiverAccId()));
        Account senderAccount = null;
        Account receiverAccount = null;

        try {
            senderAccount = accounts.stream().filter(acc -> acc.getAccountId().equals(transactionDto.getSenderAccId())).toList().getFirst();
            receiverAccount = accounts.stream().filter(acc -> acc.getAccountId().equals(transactionDto.getReceiverAccId())).toList().getFirst();
        } catch (NoSuchElementException ex) {
            throw new AccountNotFoundException(Account.class, ex);
        }

        return Transaction.builder()
                .senderAccount(senderAccount)
                .receiverAccount(receiverAccount)
                .amount(transactionDto.getAmount())
                .createdAt(Instant.now())
                .build();
    }
}
