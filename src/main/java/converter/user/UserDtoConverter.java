package converter.user;

import domain.User;
import converter.Converter;
import dto.user.UserDto;

public class UserDtoConverter implements Converter<User, UserDto> {
    @Override
    public UserDto convert(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getRole(), user.isBlocked());
    }
}
