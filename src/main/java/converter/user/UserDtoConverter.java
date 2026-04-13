package converter.user;

import domain.User;
import converter.Converter;
import dto.user.UserDto;
import converter.ListConverter;

import java.util.List;

public class UserDtoConverter implements Converter<User, UserDto>, ListConverter<User, UserDto> {
    @Override
    public UserDto convert(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.getFatherName(), user.getRole(), user.isBlocked());
    }

    @Override
    public List<UserDto> convertAll(List<User> users) {
        return users.stream()
                .map(this::convert)
                .toList();
    }
}
