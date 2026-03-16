package service;

import domain.User;
import dto.user.UserAuthenticationRequest;
import dto.user.UserAuthenticationResponse;
import dto.user.UserSignUpRequest;
import dto.user.UserSignUpResponse;
import exception.InvalidCredentialsException;
import exception.UserAlreadyExistException;
import exception.ValidationException;
import mapper.user.UserAuthenticationResponseMapper;
import mapper.user.UserSignUpRequestMapper;
import mapper.user.UserSignUpResponseMapper;
import repository.TransactionHelper;
import repository.UserRepository;
import sequrity.PasswordEncoder;
import validation.UserValidationService;

import java.time.LocalDateTime;

public class UserService {
    private final UserRepository userRepository;
    private final TransactionHelper transactionHelper;
    private final UserSignUpRequestMapper userSignUpMapper;
    private final UserSignUpResponseMapper userSignUpResponseMapper;
    private final UserValidationService userValidationService;
    private final PasswordEncoder passwordEncoder;
    private final UserAuthenticationResponseMapper userAuthenticationResponseMapper;

    public UserService(UserRepository userRepository, TransactionHelper transactionHelper,
                       UserSignUpRequestMapper userSignUpMapper, UserSignUpResponseMapper userSignUpResponseMapper,
                       UserValidationService userValidationService, PasswordEncoder passwordEncoder,
                       UserAuthenticationResponseMapper userAuthenticationResponseMapper) {
        this.userRepository = userRepository;
        this.transactionHelper = transactionHelper;
        this.userSignUpMapper = userSignUpMapper;
        this.userSignUpResponseMapper = userSignUpResponseMapper;
        this.userValidationService = userValidationService;
        this.passwordEncoder = passwordEncoder;
        this.userAuthenticationResponseMapper = userAuthenticationResponseMapper;
    }

    public UserSignUpResponse signUp(UserSignUpRequest request) {
        var validationErrorList = userValidationService.validateSignUpRequest(request);

        if (!validationErrorList.isEmpty()) {
            throw new ValidationException(validationErrorList);
        }

        var savedUser = saveUserTransactional(request);

        return userSignUpResponseMapper.map(savedUser);
    }

    public UserAuthenticationResponse signIn(UserAuthenticationRequest authenticationRequest) {
        var email = authenticationRequest.email();
        var rawPassword = authenticationRequest.password();

        return signInTransactional(email, rawPassword);
    }

    private User saveUserTransactional(UserSignUpRequest request) {
        return transactionHelper.executeInTransaction(() -> {
            userRepository.findByEmail(request.email())
                    .ifPresent(user -> {
                        throw new UserAlreadyExistException(request.email());
                    });

            var user = userSignUpMapper.map(request);

            return userRepository.save(user);
        });
    }

    private UserAuthenticationResponse signInTransactional(String email, String rawPassword) {
        return transactionHelper.executeInTransaction(() -> {
            var user = userRepository.findByEmail(email)
                    .orElseThrow(InvalidCredentialsException::new);

            var encodedPassword = user.getPasswordHash();
            if (passwordEncoder.verify(rawPassword, encodedPassword)) {
                user.setLastLogin(LocalDateTime.now());

                userRepository.update(user);

                return userAuthenticationResponseMapper.map(user);
            }
            throw new InvalidCredentialsException();
        });
    }
}
