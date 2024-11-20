package com.makeprojects.ewallet.useraccounts.mapper;

import com.makeprojects.ewallet.shared.model.Account;
import com.makeprojects.ewallet.shared.model.Transaction;
import com.makeprojects.ewallet.useraccounts.dto.TransactionDto;
import com.makeprojects.ewallet.useraccounts.repository.AccountRepository;
import com.makeprojects.ewallet.useraccounts.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class TransactionMapper {

    private final AccountRepository accountRepository;

    @Autowired
    public TransactionMapper(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Transaction mapToTransaction(TransactionDto transactionDto) {
        List<Account> accounts = this.accountRepository.findAllById(List.of(transactionDto.getSenderAccId(), transactionDto.getReceiverAccId()));
        Account senderAccount = accounts.stream().filter(acc -> acc.getAccountId() == transactionDto.getSenderAccId()).toList().getFirst();
        Account receiverAccount = accounts.stream().filter(acc -> acc.getAccountId() == transactionDto.getReceiverAccId()).toList().getFirst();

        return Transaction.builder()
                .senderAccount(senderAccount)
                .receiverAccount(receiverAccount)
                .amount(transactionDto.getAmount())
                .createdAt(Instant.now())
                .build();
    }
}
