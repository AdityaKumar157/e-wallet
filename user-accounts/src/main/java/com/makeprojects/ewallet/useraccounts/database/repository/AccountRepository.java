package com.makeprojects.ewallet.useraccounts.database.repository;

import com.makeprojects.ewallet.shared.database.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Wallet, UUID> {

    @Query("select a from Account a where a.user.userId = ?1")
    Optional<Wallet> findByUserId(UUID userId);
}
