package service;

import domain.User;
import dto.user.UserSignUpRequest;
import exception.UserWithEmailExistException;
import mapper.user.UserSignUpMapper;
import repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;
    private final UserSignUpMapper userSignUpMapper;


    public UserService(UserRepository userRepository, UserSignUpMapper userSignUpMapper) {
        this.userRepository = userRepository;
        this.userSignUpMapper = userSignUpMapper;
    }

    public User sigUp(UserSignUpRequest request) {
        var userByEmail = userRepository.findByEmail(request.email());

        if (userByEmail.isPresent()) {
            throw new UserWithEmailExistException(request.email());
        }

        var user = userSignUpMapper.map(request);

        userRepository.save(user);

        return userRepository.save(user);
    }
}
