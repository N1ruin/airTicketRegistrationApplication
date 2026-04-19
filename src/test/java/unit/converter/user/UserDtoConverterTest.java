package unit.converter.user;

import converter.user.UserDtoConverter;
import domain.Role;
import domain.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoConverterTest {
    private final UserDtoConverter userDtoConverter = new UserDtoConverter();

    @Test
    void convertUserToUserDtoSuccess() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setFatherName("FatherName");
        user.setRole(Role.USER);
        user.setBlocked(false);
        user.setEmail("testemail123@gmail.com");

        var result = userDtoConverter.convert(user);

        assertNotNull(result);
        assertEquals(1L, user.getId());
        assertEquals("FirstName", result.firstName());
        assertEquals("LastName", result.lastName());
        assertEquals("FatherName", result.fatherName());
        assertEquals(Role.USER, result.role());
        assertFalse(result.isBlocked());
        assertEquals("testemail123@gmail.com", result.email());
    }
}
