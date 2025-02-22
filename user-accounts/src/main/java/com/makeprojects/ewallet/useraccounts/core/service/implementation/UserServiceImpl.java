package com.makeprojects.ewallet.useraccounts.core.service.implementation;

import com.makeprojects.ewallet.shared.exceptions.NotFoundException;
import com.makeprojects.ewallet.shared.database.model.Wallet;
import com.makeprojects.ewallet.shared.database.model.User;
import com.makeprojects.ewallet.useraccounts.core.service.definition.AccountService;
import com.makeprojects.ewallet.useraccounts.core.service.definition.UserService;
import com.makeprojects.ewallet.useraccounts.database.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    private static final String EMPTY_STRING = "";

    @Autowired
    public UserServiceImpl(UserRepository userRepository, AccountService accountService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
    }

    //<editor-fold desc="UserService Implementation">
    @Override
    public User get(UUID id) {
        String errorMsg = EMPTY_STRING;
        try {
            if (id == null) {
                errorMsg = String.format("UUID of User cannot be null.");
                log.error(errorMsg);
                throw new NullPointerException(errorMsg);
            }

            Optional<User> optionalUser = this.userRepository.findById(id);
            if (optionalUser.isEmpty()) {
                errorMsg = String.format("User with UUID %s is not found.", id);
                log.error(errorMsg);
                throw new NotFoundException(User.class, "UUID", id);
            }

            log.info(String.format("Successfully retrieved User with UUID '%s'.", id));
            return optionalUser.get();
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while retrieving User with UUID '%s'. Exception: %s", id, e);
            log.error(errorMsg);
            throw e;
        }
    }

    @Override
    public List<User> getAll() {
        try {
            List<User> userList = this.userRepository.findAll();
            if (userList.isEmpty()) {
                log.error("No User found.");
            }

            log.info("Successfully retrieved Users list.");
            return userList;
        } catch (Exception e) {
            log.error("Exception occurred while retrieving list of Users.");
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User create(User entity) {
        String errorMsg = EMPTY_STRING;
        try {
            if (entity == null) {
                log.error("User entity cannot be null.");
                throw new NullPointerException("User entity cannot be null.");
            }

            if ((entity.getUserId() != null) && (get(entity.getUserId()) != null)) {
                errorMsg = String.format("User with UUID %s already exists.", entity.getUserId());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            entity.setRole("USER");
            entity.setPassword(this.passwordEncoder.encode(entity.getPassword()));
            User createdUser = this.userRepository.save(entity);
            if (createdUser == null) {
                log.error("Failed to create an user.");
                throw new RuntimeException("Failed to create an user.");
            }

            Wallet savedUserAccount = this.accountService.addAccount(createdUser);
            log.info(String.format("Successfully saved user with userId %s, accountId %s.", createdUser.getUserId(), savedUserAccount.getAccountId()));

            return createdUser;
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while creating an user. Exception: %s", e);
            log.error(errorMsg);
            throw e;
        }
    }

    @Override
    public User update(User entity) {
        String errorMsg = EMPTY_STRING;
        try {
            if (entity == null) {
                log.error("User entity which needs to be updated cannot be null.");
                throw new NullPointerException("User entity cannot be null.");
            }

            if (get(entity.getUserId()) == null) {
                errorMsg = String.format("User with UUID %s doesn't exist in database.", entity.getUserId());
                log.error(errorMsg);
                throw new NotFoundException(User.class, "UUID", entity.getUserId());
            }

            User updatedUser = this.userRepository.save(entity);
            if (updatedUser == null) {
                errorMsg = String.format("Failed to update an User with UUID '%s'.", entity.getUserId());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            log.info(String.format("Successfully updated an user with UUID '%s'.", updatedUser.getUserId()));
            return updatedUser;
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while creating an user. Exception: %s", e);
            log.error(errorMsg);
            throw e;
        }
    }

    @Override
    public void delete(UUID id) {
        String errorMsg = EMPTY_STRING;
        try {
            if (id == null) {
                log.error("User entity UUID which needs to be deleted cannot be null.");
                throw new NullPointerException("User entity UUID which needs to be deleted cannot be null.");
            }

            if (get(id) == null) {
                errorMsg = String.format("User with UUID %s doesn't exist in database.", id);
                log.error(errorMsg);
                throw new NotFoundException(User.class, "UUID", id);
            }

            this.userRepository.deleteById(id);
            log.info(String.format("Successfully deleted an user with UUID '%s'.", id));
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while deleting an user with UUID '%s'. Exception: %s", id, e);
            log.error(errorMsg);
            throw e;
        }
    }
    //</editor-fold>
}
