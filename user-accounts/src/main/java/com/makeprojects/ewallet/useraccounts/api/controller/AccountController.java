package com.makeprojects.ewallet.useraccounts.api.controller;

import com.makeprojects.ewallet.shared.exceptions.NotFoundException;
import com.makeprojects.ewallet.shared.database.model.Wallet;
import com.makeprojects.ewallet.useraccounts.core.service.definition.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/wallet")
public class AccountController {

    private final WalletService walletService;

    @Autowired
    public AccountController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/get-info")
    public ResponseEntity<Wallet> getAccountInfoByUserId(@RequestParam UUID userId) {
        try {
            Wallet wallet = this.walletService.getWalletByUserId(userId);
            return ResponseEntity.ok(wallet);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get-info/{walletId}")
    public ResponseEntity<Wallet> getAccountInfoById(@PathVariable UUID walletId) {
        try {
            Wallet wallet = this.walletService.get(walletId);
            return ResponseEntity.ok(wallet);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
