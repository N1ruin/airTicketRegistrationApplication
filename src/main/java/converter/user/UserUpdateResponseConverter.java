package converter.user;

import converter.Converter;
import domain.User;
import dto.user.UpdateUserResponse;

public class UserUpdateResponseConverter implements Converter<User, UpdateUserResponse> {
    @Override
    public UpdateUserResponse convert(User user) {
        return new UpdateUserResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.getFatherName(), user.getRole());
    }
}
