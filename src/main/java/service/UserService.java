package service;

import converter.user.UserDtoConverter;
import domain.Role;
import domain.User;
import dto.user.UserDto;
import exception.EntityNotFoundException;
import exception.InvalidCredentialsException;
import exception.UserAlreadyExistException;
import repository.TransactionHelper;
import repository.UserRepository;
import sequrity.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

public class UserService {
    private final UserRepository userRepository;
    private final TransactionHelper transactionHelper;
    private final PasswordEncoder passwordEncoder;
    private final UserDtoConverter userDtoConverter;

    public UserService(UserRepository userRepository, TransactionHelper transactionHelper, PasswordEncoder passwordEncoder,
                       UserDtoConverter userDtoConverter) {
        this.userRepository = userRepository;
        this.transactionHelper = transactionHelper;
        this.passwordEncoder = passwordEncoder;
        this.userDtoConverter = userDtoConverter;
    }

    public User signUp(User user) {
        return signUpTransactional(user);
    }

    public UserDto signIn(String email, String rawPassword) {
        return signInTransactional(email, rawPassword);
    }

    public List<User> findAll() {
        return findAllTransactional();
    }

    public User findById(Long id) {
        return findByIdTransactional(id);
    }

    public User signUpAdmin(User user) {
        return signUpAdminTransactional(user);
    }

    public void block(Long id) {
        blockTransactional(id);
    }

    public void unblock(Long id) {
        unblockTransactional(id);
    }

    public void update(Long id, String newPassword, String firstName, String lastName, String fatherName) {
        updateTransactional(id, newPassword, firstName, lastName, fatherName);
    }

    private User signUpTransactional(User user) {
        return transactionHelper.executeInTransaction(() -> {
            checkUserByEmailExist(user.getEmail());

            user.setRole(Role.USER);
            user.setBlocked(false);

            return userRepository.save(user);
        });
    }

    private User signUpAdminTransactional(User user) {
        return transactionHelper.executeInTransaction(() -> {
            checkUserByEmailExist(user.getEmail());

            user.setRole(Role.ADMIN);
            user.setBlocked(false);

            return userRepository.save(user);
        });
    }

    private UserDto signInTransactional(String email, String rawPassword) {
        return transactionHelper.executeInTransaction(() -> {
            var user = userRepository.findByEmail(email)
                    .orElseThrow(InvalidCredentialsException::new);

            var encodedPassword = user.getPasswordHash();
            if (passwordEncoder.verify(rawPassword, encodedPassword)) {
                user.setLastLogin(LocalDateTime.now());

                userRepository.update(user);

                return userDtoConverter.convert(user);
            }

            throw new InvalidCredentialsException();
        });
    }

    private void blockTransactional(Long id) {
        transactionHelper.executeInTransaction(() -> {
            var user = getUserById(id);

            if (user.isBlocked()) {
                return;
            }

            user.setBlocked(true);
            userRepository.update(user);
        });
    }

    private void unblockTransactional(Long id) {
        transactionHelper.executeInTransaction(() -> {
            var user = getUserById(id);

            if (!user.isBlocked()) {
                return;
            }

            user.setBlocked(false);

            userRepository.update(user);
        });
    }

    private void checkUserByEmailExist(String email) {
        userRepository.findByEmail(email)
                .ifPresent(existedUser -> {
                    throw new UserAlreadyExistException(existedUser.getEmail());
                });
    }

    private void updateTransactional(Long id, String newPassword, String firstName, String lastName, String fatherName) {
        transactionHelper.executeInTransaction(() -> {
            var user = getUserById(id);

            var passwordHash = passwordEncoder.encode(newPassword);

            user.setPasswordHash(passwordHash);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setFatherName(fatherName);

            userRepository.update(user);
        });
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(id)));
    }

    private User findByIdTransactional(long id) {
        return transactionHelper.executeInTransaction(() -> userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Passenger with id %d not found".formatted(id))));
    }

    private List<User> findAllTransactional() {
        return transactionHelper.executeInTransaction(userRepository::findAll);
    }
}
