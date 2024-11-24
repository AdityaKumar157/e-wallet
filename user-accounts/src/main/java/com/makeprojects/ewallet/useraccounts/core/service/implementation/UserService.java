package com.makeprojects.ewallet.useraccounts.core.service.implementation;

import com.makeprojects.ewallet.shared.exceptions.NotFoundException;
import com.makeprojects.ewallet.shared.database.model.Account;
import com.makeprojects.ewallet.shared.database.model.User;
import com.makeprojects.ewallet.useraccounts.database.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, AccountService accountService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(rollbackFor = Exception.class)
    public User addUser(User user) {
        try {
            User savedUser = this.userRepository.save(user);
            savedUser.setRole("USER");
            savedUser.setPassword(this.passwordEncoder.encode(savedUser.getPassword()));
            Account savedUserAccount = this.accountService.addAccount(savedUser);
            log.info(String.format("Successfully saved user with userId %s, accountId %s.", savedUser.getUserId(), savedUserAccount.getAccountId()));
            return savedUser;
        } catch (Exception e) {
            String error = String.format("Exception while saving user with userID %s. Exception: %s", user.getUserId(), e);
            log.error(error);
            throw e;
        }
    }

    public User getUserById(UUID userId) {
        try {
            Optional<User> optionalUser = this.userRepository.findById(userId);
            if(optionalUser.isEmpty()) {
                NotFoundException ex = new NotFoundException(User.class, "userId", userId);
                log.error(ex.getMessage());
                throw ex;
            }

            User user = optionalUser.get();
            log.info("Successfully fetched user with userId {}.", user.getUserId());
            return user;
        } catch (Exception e) {
            String error = String.format("Exception while fetching user with userID %s. Exception: %s", userId, e);
            log.error(error);
            throw e;
        }
    }
}
