package com.makeprojects.ewallet.useraccounts.api.controller;

import com.makeprojects.ewallet.shared.database.model.Transaction;
import com.makeprojects.ewallet.useraccounts.core.service.definition.WalletService;
import com.makeprojects.ewallet.useraccounts.dto.TransactionDto;
import com.makeprojects.ewallet.useraccounts.core.service.implementation.WalletServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/txn")
public class TransactionController {

    private final WalletService walletService;

    @Autowired
    public TransactionController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/add")
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionDto transactionDto) {
        Transaction createdTransaction = this.walletService.sendMoney(transactionDto);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    @GetMapping("/statement/{walletId}")
    public ResponseEntity<?> getMiniStatement(@PathVariable UUID walletId) {
        if(!((WalletServiceImpl) this.walletService).isLoggedInUserAccount(walletId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is invalid");
        }
        List<Transaction> miniStatement = this.walletService.getMiniStatementOfUserWallet(walletId);
        return ResponseEntity.ok(miniStatement);
    }
}
