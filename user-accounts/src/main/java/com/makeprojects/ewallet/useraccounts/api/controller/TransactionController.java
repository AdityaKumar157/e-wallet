package com.makeprojects.ewallet.useraccounts.api.controller;

import com.makeprojects.ewallet.shared.database.model.Transaction;
import com.makeprojects.ewallet.useraccounts.dto.TransactionDto;
import com.makeprojects.ewallet.useraccounts.core.service.implementation.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/txn")
public class TransactionController {

    private final AccountServiceImpl accountService;

    @Autowired
    public TransactionController(AccountServiceImpl accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/add")
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionDto transactionDto) {
        Transaction createdTransaction = this.accountService.sendMoney(transactionDto);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    @GetMapping("/statement/{accountId}")
    public ResponseEntity<?> getMiniStatement(@PathVariable UUID accountId) {
        if(!this.accountService.isLoggedInUserAccount(accountId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is invalid");
        }
        List<Transaction> miniStatement = this.accountService.getMiniStatementOfUerAccount(accountId);
        return ResponseEntity.ok(miniStatement);
    }
}
