package unit.service.validation;

import dto.user.UpdateUserRequest;
import dto.user.UserSignUpRequest;
import exception.ValidationException;
import org.junit.jupiter.api.Test;
import validation.UserValidationService;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationServiceTest {
    private final UserValidationService service = new UserValidationService();

    @Test
    void validateSignUpSuccess() {
        var request = new UserSignUpRequest(
                "valid@mail.com", "secret123", "Ivan", "Ivanov", "Ivanovich");

        assertDoesNotThrow(() -> service.validateSignUpRequest(request));
    }

    @Test
    void validateSignUpInvalidData() {
        var request = new UserSignUpRequest(
                "bad-email", "12345", "", "  ", "Ivanovich"
        );

        var exception = assertThrows(ValidationException.class,
                () -> service.validateSignUpRequest(request));

        var errors = exception.getValidationErrorMessages();

        assertEquals(3, errors.size());
    }

    @Test
    void validateUpdateSuccess() {
        var request = new UpdateUserRequest(1L, "newPassword", "Petr", "Petrov",
                "Petrovich");

        assertDoesNotThrow(() -> service.validateUpdateRequest(request));
    }

    @Test
    void validateUpdateInvalidPassword() {
        var request = new UpdateUserRequest(1L, "123", "Petr", "Petrov",
                "Petrovich");
        var exception = assertThrows(ValidationException.class,
                () -> service.validateUpdateRequest(request));

        assertEquals(1, exception.getValidationErrorMessages().size());
    }
}
