package unit.converter.user;

import converter.user.UserSignUpRequestConverter;
import dto.user.UserSignUpRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sequrity.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class UserSignUpRequestConverterTest {
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserSignUpRequestConverter converter;

    @Test
    void convertRequestToUserSuccess() {
        var request = new UserSignUpRequest("testemail123@gmail.com", "testpassword123",
                "FirstName", "LastName", "FatherName");
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPass");

        var result = converter.convert(request);

        assertNotNull(result);
        assertEquals("testemail123@gmail.com", result.getEmail());
        assertEquals("encodedPass", result.getPasswordHash());
        assertEquals("FirstName", result.getFirstName());
        assertEquals("LastName", result.getLastName());
        assertEquals("FatherName", result.getFatherName());
    }
}
