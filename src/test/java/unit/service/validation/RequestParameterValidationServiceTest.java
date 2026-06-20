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
        assertThrows(ValidationException.class, () -> service.validateId(-5L));
    }

    @Test
    void validateIdNullValueExceptionErrorsSizeOne() {
        assertThrows(ValidationException.class, () -> service.validateId(null));
    }
}
