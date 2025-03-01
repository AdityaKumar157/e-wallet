package com.makeprojects.ewallet.useraccounts.database.repository;

import com.makeprojects.ewallet.shared.database.model.BankAccount;
import com.makeprojects.ewallet.useraccounts.dto.DelegatedTransactionAccountDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {

    @Query("select b from BankAccount b where b.wallet.walletId = ?1")
    List<BankAccount> findBankAccountsByWalletId(UUID walletId);

    @Query("select b from BankAccount b where b.wallet.walletId = ?1")
    List<DelegatedTransactionAccountDTO> findByWallet_WalletId(UUID walletId);


}
