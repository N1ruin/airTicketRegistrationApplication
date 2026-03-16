package mapper.user;

import domain.Role;
import domain.User;
import dto.user.UserSignUpRequest;
import sequrity.PasswordEncoder;

public class UserSignUpRequestMapper implements Mapper<UserSignUpRequest, User> {
    private final PasswordEncoder passwordEncoder;

    public UserSignUpRequestMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User map(UserSignUpRequest source) {
        var user = new User();

        var encodedPassword = passwordEncoder.encode(source.password());

        user.setEmail(source.email());
        user.setPasswordHash(encodedPassword);
        user.setFirstName(source.firstName());
        user.setLastName(source.lastName());
        user.setFatherName(source.fatherName());
        user.setRole(Role.USER);
        user.setBlocked(false);

        return user;
    }
}
