package service;

import domain.Role;
import domain.User;
import exception.ApplicationException;
import repository.impl.UserRepository;
import security.PasswordEncoder;
import util.CurrentUserHolder;
import util.TransactionHelper;

import java.time.LocalDateTime;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;

public class UserService {
    private final UserRepository userRepository;
    private final TransactionHelper transactionHelper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, TransactionHelper transactionHelper,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.transactionHelper = transactionHelper;
        this.passwordEncoder = passwordEncoder;
    }

    public User signUp(User user, Role role) {
        return transactionHelper.executeInTransaction(() -> {
            checkUserByEmailExist(user.getEmail());

            user.setRole(role);
            user.setBlocked(false);

            return userRepository.create(user);
        });
    }

    public User signIn(String email, String rawPassword) {
        return transactionHelper.executeInTransaction(() -> {
            var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ApplicationException("Invalid email or password", SC_UNAUTHORIZED));

            var encodedPassword = user.getPasswordHash();
            if (!passwordEncoder.verify(rawPassword, encodedPassword)) {
                throw new ApplicationException("Invalid email or password", SC_UNAUTHORIZED);
            }

            user.setLastLogin(LocalDateTime.now());

            return userRepository.update(user);
        });
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Passenger not found. ID: %d".formatted(id), SC_NOT_FOUND));
    }

    public void block(Long id) {
        transactionHelper.executeInTransaction(() -> {
            var user = findById(id);

            if (user.isBlocked()) {
                return;
            }

            user.setBlocked(true);
            userRepository.update(user);
        });
    }

    public void unblock(Long id) {
        transactionHelper.executeInTransaction(() -> {
            var user = findById(id);

            if (!user.isBlocked()) {
                return;
            }

            user.setBlocked(false);

            userRepository.update(user);
        });
    }

    public User update(String newPassword) {
        return transactionHelper.executeInTransaction(() -> {
            var currentUserId = CurrentUserHolder.getCurrentUserId();

            var user = findById(currentUserId);

            var passwordHash = passwordEncoder.encode(newPassword);
            user.setPasswordHash(passwordHash);

            return userRepository.update(user);
        });
    }

    private void checkUserByEmailExist(String email) {
        userRepository.findByEmail(email)
                .ifPresent(existedUser -> {
                    throw new ApplicationException(existedUser.getEmail(), SC_CONFLICT);
                });
    }
}
