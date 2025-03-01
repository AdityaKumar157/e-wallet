package com.makeprojects.ewallet.shared.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.makeprojects.ewallet.shared.core.definition.AutonomousTransactionAccount;
import com.makeprojects.ewallet.shared.core.enums.AccountEnums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@With
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Wallet implements AutonomousTransactionAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID walletId;

    @JsonIgnore
    @OneToOne
    @JsonIncludeProperties({"userId", "name"})
    private User user;

    @Builder.Default
    private double balance = 0.0D;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    private boolean kycComplete = false;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<BankAccount> linkedBankAccounts;

    @ManyToOne
    @JoinColumn(name = "default_bank_account_id", nullable = true)
    private BankAccount defaultBankAccount;

    public void send(Wallet receiverAccount, double amount) {
        if (this.equals(receiverAccount)) {
            this.balance += amount;
        } else {
            this.balance -= amount;
            receiverAccount.balance += amount;
        }
    }

    /**
     * Sets the default bank account for transactions.
     * @param bankAccount The BankAccount object to set as default.
     * @return true if successful, false if the account is not linked.
     */
    public boolean setDefaultBankAccount(BankAccount bankAccount) {
        if (linkedBankAccounts.contains(bankAccount)) {
            this.defaultBankAccount = bankAccount;
            return true;
        }
        return false;
    }

    /**
     * Unlinks a bank account from the wallet.
     * Prevents unlinking if it is the default bank account.
     * @param bankAccount The BankAccount object to remove.
     * @return true if successfully unlinked, false if the account is the default or not found.
     */
    public boolean unlinkBankAccount(BankAccount bankAccount) {
        if (bankAccount.equals(defaultBankAccount)) {
            return false; // Prevent unlinking the default account
        }
        return linkedBankAccounts.remove(bankAccount);
    }

    /**
     * Links a bank account to the wallet
     * @param bankAccount BankAccount obj to link
     * @return true if linking is successful, otherwise false
     */
    public boolean linkBankAccount(BankAccount bankAccount) {
        if(this.linkedBankAccounts.contains(bankAccount)) {
            return false; // BankAccount is already linked
        }

        bankAccount.setWallet(this);
        this.linkedBankAccounts.add(bankAccount);
        return true;
    }

    /**
     * Adds funds to the account
     * @param amount value to be added
     * @param fromAccount funding source account
     * @return true if amount is added, otherwise false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deposit(double amount, AutonomousTransactionAccount fromAccount) {
        if(fromAccount == null || fromAccount.getBalance() < amount || !fromAccount.isActive()) {
            return false;
        }

        if(this.equals(fromAccount) || !isActive()) {
            return false;
        }

        double fromAccountBalance = fromAccount.getBalance();
        fromAccountBalance -= amount;
        fromAccount.setBalance(fromAccountBalance);

        double thisAccountBalance = this.getBalance();
        thisAccountBalance += amount;
        this.setBalance(thisAccountBalance);

        return true;
    }

    /**
     * Withdraws funds (returns false if insufficient balance)
     *
     * @param amount    value to be withdrawn
     * @param toAccount receiving account
     * @return true if amount is withdrawn, otherwise false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean withdraw(double amount, AutonomousTransactionAccount toAccount) {
        if(toAccount == null || !toAccount.isActive()) {
            return false;
        }

        if(this.equals(toAccount) || !isActive() || this.getBalance() < amount) {
            return false;
        }

        double thisAccountBalance = this.getBalance();
        thisAccountBalance -= amount;
        this.setBalance(thisAccountBalance);

        double toAccountBalance = toAccount.getBalance();
        toAccountBalance += amount;
        toAccount.setBalance(toAccountBalance);

        return true;
    }

    /**
     * Transfers money to another account
     * @param targetAccount target account for transferring account
     * @param amount value to be transferred
     * @param receive true to receive, false to send
     * @return true to receive from targetAccount, false to send to targetAccount
     */
    @Override
    public boolean transfer(AutonomousTransactionAccount targetAccount, double amount, boolean receive) {
        try {
            if(targetAccount instanceof Wallet wallet) {
                send(wallet, amount);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Unique identifier for the account
     * @return UUID of account
     */
    @Override
    public UUID getAccountId() {
        return walletId;
    }

    /**
     * Name of the account owner
     * @return name of the account holder as a simple String
     */
    @Override
    public String getAccountHolderName() {
        try {
            if(user == null) {
                throw new NullPointerException();
            }

            return user.getName();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the account is active
     * @return true if active, otherwise false
     */
    @Override
    public boolean isActive() {
        return isKycComplete() && accountStatus == AccountStatus.ACTIVE;
    }
}
