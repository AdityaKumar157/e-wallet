package com.makeprojects.ewallet.useraccounts.repository;

import com.makeprojects.ewallet.useraccounts.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
}
