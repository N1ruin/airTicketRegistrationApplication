package unit.service.validation;

import dto.passenger.CreatePassengerRequest;
import dto.passenger.UpdatePassengerRequest;
import dto.passport.PassportDto;
import exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import validation.PassengerValidationService;
import validation.PassportValidationService;
import validation.RequestParameterValidationService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerValidationServiceTest {
    @Mock
    private RequestParameterValidationService requestParameterValidationService;
    @Mock
    private PassportValidationService passportValidationService;
    @InjectMocks
    private PassengerValidationService passengerValidationService;

    @Test
    void validateCreateRequestNullRequestOneErrorMessage() {
        var exception = assertThrows(ValidationException.class,
                () -> passengerValidationService.validateCreateRequest(null));

        assertEquals(1, exception.getValidationErrorMessages().size());
    }

    @Test
    void validateUpdateRequestThreeErrors() {
        var request = new UpdatePassengerRequest(
                -1L, "Ivan", "Ivanov", null, true,
                LocalDate.now().plusDays(1), null);

        doThrow(new ValidationException(List.of("error")))
                .when(requestParameterValidationService).validateId(-1L);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> passengerValidationService.validateUpdateRequest(request));

        var errors = exception.getValidationErrorMessages();

        assertEquals(3, errors.size());
    }

    @Test
    void validateCreateRequestCallPassportServiceOrThreeNameError() {
        var passportDto = mock(PassportDto.class);
        var request = new CreatePassengerRequest("Ivan123", "Ivanov123", "Ivanovich123",
                true, LocalDate.now().minusYears(10), passportDto);

        var exeption = assertThrows(ValidationException.class,
                () -> passengerValidationService.validateCreateRequest(request));

        assertEquals(3, exeption.getValidationErrorMessages().size());
        verify(passportValidationService).validatePassportDto(eq(passportDto), any());
    }

    @Test
    void validateBirthDateInvalidOld() {
        var request = new UpdatePassengerRequest(
                1L, "Ivan123", "Ivanov123", null, true,
                LocalDate.now().minusYears(121), mock(PassportDto.class)
        );

        var exception = assertThrows(ValidationException.class,
                () -> passengerValidationService.validateUpdateRequest(request));

        assertTrue(exception.getValidationErrorMessages().contains("Invalid birth date (too old)"));
    }

    @Test
    void validateNamesValid() {
        CreatePassengerRequest request = new CreatePassengerRequest("Anne-Marie", "Иванов",
                "Кукукович",
                true, null, null);

        assertDoesNotThrow(() -> passengerValidationService.validateCreateRequest(request));
    }

    @Test
    void validateNamesInvalid() {
        CreatePassengerRequest request = new CreatePassengerRequest("Ivan123", " Ivanov",
                "Petrov-", true, null, null);

        var exception = assertThrows(ValidationException.class,
                () -> passengerValidationService.validateCreateRequest(request));

        var errors = exception.getValidationErrorMessages();

        assertTrue(errors.contains("First name invalid format"));
        assertTrue(errors.contains("Last name invalid format"));
    }

    @Test
    void validateNamesIsBlank() {
        PassportDto passport = mock(PassportDto.class);
        CreatePassengerRequest request = new CreatePassengerRequest("", "   ", null,
                true, null, passport);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> passengerValidationService.validateCreateRequest(request));

        assertTrue(exception.getValidationErrorMessages().stream().anyMatch(s -> s.contains("must not be empty")));
    }
}
