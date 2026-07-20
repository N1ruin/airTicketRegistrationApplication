package converter.user;

import domain.User;
import dto.user.UserSignUpRequest;
import converter.Converter;
import security.PasswordEncoder;

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

        return user;
    }
}
