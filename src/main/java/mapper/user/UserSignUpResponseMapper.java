package mapper.user;

import domain.User;
import dto.user.UserSignUpResponse;

public class UserSignUpResponseMapper implements Mapper<User, UserSignUpResponse> {
    @Override
    public UserSignUpResponse map(User user) {
        return new UserSignUpResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.getFatherName());
    }
}
