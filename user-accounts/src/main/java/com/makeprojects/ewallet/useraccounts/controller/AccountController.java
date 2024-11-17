package com.makeprojects.ewallet.useraccounts.controller;

import com.makeprojects.ewallet.shared.exceptions.NotFoundException;
import com.makeprojects.ewallet.useraccounts.model.Account;
import com.makeprojects.ewallet.useraccounts.service.AccountService;
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
    public ResponseEntity<Account> getAccountInfoByUserId(@RequestParam UUID userId) {
        try {
            Account account = this.accountService.getAccountByUserId(userId);
            return ResponseEntity.ok(account);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get-info/{accountId}")
    public ResponseEntity<Account> getAccountInfoById(@PathVariable UUID accountId) {
        try {
            Account account = this.accountService.getAccountById(accountId);
            return ResponseEntity.ok(account);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
