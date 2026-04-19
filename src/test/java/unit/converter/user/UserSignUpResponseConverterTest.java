package unit.converter.user;

import converter.user.UserSignUpResponseConverter;
import domain.Role;
import domain.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserSignUpResponseConverterTest {
    private final UserSignUpResponseConverter converter = new UserSignUpResponseConverter();

    @Test
    void convertUserToSignUpResponseSuccess() {
        var user = new User();
        user.setId(1L);
        user.setEmail("testemail@gmail.com");
        user.setRole(Role.USER);
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setFatherName("FatherName");

        var result = converter.convert(user);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("testemail@gmail.com", result.email());
        assertEquals("FirstName", result.firstName());
        assertEquals("LastName", result.lastName());
        assertEquals("FatherName", result.fatherName());
        assertEquals(Role.USER, result.role());
    }
}
