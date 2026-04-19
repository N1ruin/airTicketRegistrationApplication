package unit.service.validation;

import exception.ValidationException;
import org.junit.jupiter.api.Test;
import validation.RequestParameterValidationService;

import static org.junit.jupiter.api.Assertions.*;

class RequestParameterValidationServiceTest {
    private final RequestParameterValidationService service = new RequestParameterValidationService();

    @Test
    void validateIdSuccess() {
        assertDoesNotThrow(() -> service.validateId(10L));
        assertDoesNotThrow(() -> service.validateId(1L));
    }

    @Test
    void validateIdNegativeValueExceptionErrorsSizeOne() {
        var exception = assertThrows(ValidationException.class, () -> service.validateId(-5L));

        var errors = exception.getValidationErrorMessages();
        assertEquals(1, errors.size());
    }

    @Test
    void validateIdNullValueExceptionErrorsSizeOne() {
        var exception = assertThrows(ValidationException.class, () -> service.validateId(null));

        assertEquals(1, exception.getValidationErrorMessages().size());
    }
}
