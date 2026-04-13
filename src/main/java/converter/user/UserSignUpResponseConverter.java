package converter.user;

import domain.User;
import dto.user.UserSignUpResponse;
import converter.Converter;

public class UserSignUpResponseConverter implements Converter<User, UserSignUpResponse> {
    @Override
    public UserSignUpResponse convert(User user) {
        return new UserSignUpResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.getFatherName(), user.getRole());
    }
}
