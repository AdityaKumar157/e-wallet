package com.makeprojects.ewallet.useraccounts.api.controller;

import com.makeprojects.ewallet.shared.database.model.BankAccount;
import com.makeprojects.ewallet.shared.exceptions.NotFoundException;
import com.makeprojects.ewallet.shared.database.model.Wallet;
import com.makeprojects.ewallet.useraccounts.core.service.definition.WalletService;
import com.makeprojects.ewallet.useraccounts.dto.DTAccount.DTAccountRequestDTO;
import com.makeprojects.ewallet.useraccounts.dto.DTAccount.DTAccountResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/get-info")
    public ResponseEntity<Wallet> getWalletInfoByUserId(@RequestParam UUID userId) {
        try {
            Wallet wallet = this.walletService.getWalletByUserId(userId);
            return ResponseEntity.ok(wallet);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get-info/{walletId}")
    public ResponseEntity<Wallet> getWalletInfoById(@PathVariable UUID walletId) {
        try {
            Wallet wallet = this.walletService.get(walletId);
            return ResponseEntity.ok(wallet);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/completeKyc/{walletId}")
    public ResponseEntity<Wallet> completeKycOfWallet(@PathVariable UUID walletId) {
        try {
            Wallet wallet = this.walletService.get(walletId);
            Wallet updatedWallet = this.walletService.completeKyc(wallet);
            return ResponseEntity.ok(updatedWallet);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/linkBankAccount/{walletId}")
    public ResponseEntity<BankAccount> linkBankAccount(@PathVariable UUID walletId, @RequestBody DTAccountRequestDTO bankAccountRequestDTO) {
        try {
            BankAccount linkedBankAccount = this.walletService.linkBankAccountInWallet(bankAccountRequestDTO, walletId);
            return ResponseEntity.ok(linkedBankAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/linkBankAccounts/{walletId}")
    public ResponseEntity<List<DTAccountResponseDTO>> getLinkedBankAccounts(@PathVariable UUID walletId) {
        try {
            List<DTAccountResponseDTO> linkedBankAccounts = this.walletService.getLinkedBankAccountsInWallet(walletId);
            return ResponseEntity.ok(linkedBankAccounts);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
