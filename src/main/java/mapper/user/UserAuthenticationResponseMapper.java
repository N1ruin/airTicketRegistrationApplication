package mapper.user;

import domain.User;
import dto.user.UserAuthenticationResponse;

public class UserAuthenticationResponseMapper implements Mapper<User, UserAuthenticationResponse> {
    @Override
    public UserAuthenticationResponse map(User user) {
        return new UserAuthenticationResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.getFatherName());
    }
}
