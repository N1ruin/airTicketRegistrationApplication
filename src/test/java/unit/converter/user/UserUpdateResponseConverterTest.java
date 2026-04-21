package unit.converter.user;

import converter.user.UserUpdateResponseConverter;
import domain.Role;
import domain.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserUpdateResponseConverterTest {
    private final UserUpdateResponseConverter converter = new UserUpdateResponseConverter();

    @Test
    void convertUserToUpdateUserResponseSuccess() {
        var user = new User();
        user.setId(10L);
        user.setEmail("updated@gmail.com");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setFatherName("FatherName");
        user.setRole(Role.USER);

        var result = converter.convert(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.id());
        assertEquals(user.getEmail(), result.email());
        assertEquals(user.getFirstName(), result.firstName());
        assertEquals(user.getLastName(), result.lastName());
        assertEquals(user.getFatherName(), result.fatherName());
        assertEquals(user.getRole(), result.role());
    }
}