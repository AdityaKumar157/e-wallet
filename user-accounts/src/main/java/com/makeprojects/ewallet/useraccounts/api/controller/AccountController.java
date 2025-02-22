package com.makeprojects.ewallet.useraccounts.api.controller;

import com.makeprojects.ewallet.shared.exceptions.NotFoundException;
import com.makeprojects.ewallet.shared.database.model.Wallet;
import com.makeprojects.ewallet.useraccounts.core.service.definition.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/get-info")
    public ResponseEntity<Wallet> getAccountInfoByUserId(@RequestParam UUID userId) {
        try {
            Wallet account = this.accountService.getAccountByUserId(userId);
            return ResponseEntity.ok(account);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get-info/{accountId}")
    public ResponseEntity<Wallet> getAccountInfoById(@PathVariable UUID accountId) {
        try {
            Wallet account = this.accountService.get(accountId);
            return ResponseEntity.ok(account);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
