package unit.service.validation;

import domain.ServiceClass;
import dto.ticket.CreateTicketRequest;
import exception.ValidationException;
import org.junit.jupiter.api.Test;
import validation.TicketValidationService;

import static org.junit.jupiter.api.Assertions.*;

class TicketValidationServiceTest {
    private final TicketValidationService service = new TicketValidationService();

    @Test
    void validateCreateRequestSuccess() {
        var request = new CreateTicketRequest(ServiceClass.STANDARD.name(), 10, null, null,
                10.0, 5.0);

        assertDoesNotThrow(() -> service.validateCreateRequest(request));
    }

    @Test
    void validateCreateRequestMinimumValidValuesSuccess() {
        var request = new CreateTicketRequest(ServiceClass.ECONOMY.name(), 1, null, null,
                0.0, 0.0);

        assertDoesNotThrow(() -> service.validateCreateRequest(request));
    }

    @Test
    void validateCreateRequest_ThrowsException_WhenFieldsAreNull() {
        var request = new CreateTicketRequest(null, null, null, null,
                null, null);

        var exception = assertThrows(ValidationException.class,
                () -> service.validateCreateRequest(request));

        assertEquals(3, exception.getValidationErrorMessages().size());
    }

    @Test
    void validateCreateRequest_ThrowsException_WhenValuesNegative() {
        var request = new CreateTicketRequest(ServiceClass.BUSINESS.name(), 0, null, null,
                -1.0, -5.0);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.validateCreateRequest(request));

        assertEquals(3, exception.getValidationErrorMessages().size());
    }
}
