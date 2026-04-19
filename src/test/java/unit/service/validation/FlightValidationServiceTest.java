package unit.service.validation;

import dto.flight.CreateFlightRequest;
import dto.flight.UpdateFlightRequest;
import exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import validation.FlightValidationService;
import validation.RequestParameterValidationService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class FlightValidationServiceTest {
    @Mock
    private RequestParameterValidationService requestParameterValidationService;

    @InjectMocks
    private FlightValidationService flightValidationService;

    @Test
    void validateCreateRequestSuccess() {
        var request = new CreateFlightRequest(100, 50, null, null,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(3));

        assertDoesNotThrow(() -> flightValidationService.validateCreateRequest(request));
    }

    @Test
    void validateCreateRequestInvalidSeats() {
        var request = new CreateFlightRequest(10, 15, null, null,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2));

        var ex = assertThrows(ValidationException.class,
                () -> flightValidationService.validateCreateRequest(request));

        assertTrue(ex.getValidationErrorMessages().contains("The number of available seats cannot exceed the total number of seats"));
    }

    @Test
    void validateCreateRequestArrivalDateAfterDepartureDate() {
        var departureDate = LocalDateTime.now().plusDays(1);
        CreateFlightRequest request = new CreateFlightRequest(100, 100, null, null, departureDate,
                departureDate.minusHours(1)
        );

        ValidationException ex = assertThrows(ValidationException.class,
                () -> flightValidationService.validateCreateRequest(request));

        assertTrue(ex.getValidationErrorMessages().contains("The arrival date cannot be earlier than the departure date"));
    }

    @Test
    void validateUpdateRequestMultipleErrors() {
        var request = new UpdateFlightRequest(-1L, null, null, null,
                null, null, null);

        doThrow(new ValidationException(List.of("Invalid Id")))
                .when(requestParameterValidationService).validateId(-1L);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> flightValidationService.validateUpdateRequest(request));

        var errors = ex.getValidationErrorMessages();

        assertEquals(5, errors.size());
    }

    @Test
    void validateUpdateRequestPastDate() {
        UpdateFlightRequest request = new UpdateFlightRequest(
                1L, 100, 100, null, null,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

        var exception = assertThrows(ValidationException.class,
                () -> flightValidationService.validateUpdateRequest(request));

        assertEquals(1, exception.getValidationErrorMessages().size());
        assertTrue(exception.getValidationErrorMessages().contains("The departure date cannot be in the past"));
    }
}
