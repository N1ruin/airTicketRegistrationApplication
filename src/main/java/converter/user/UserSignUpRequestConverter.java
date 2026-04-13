package converter.user;

import domain.Role;
import domain.User;
import dto.user.UserSignUpRequest;
import converter.Converter;
import sequrity.PasswordEncoder;

public class UserSignUpRequestConverter implements Converter<UserSignUpRequest, User> {
    private final PasswordEncoder passwordEncoder;

    public UserSignUpRequestConverter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User convert(UserSignUpRequest source) {
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
