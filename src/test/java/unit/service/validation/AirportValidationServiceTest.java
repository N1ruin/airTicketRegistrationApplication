package unit.service.validation;

import dto.address.AddressDto;
import dto.airport.CreateAirportRequest;
import dto.airport.UpdateAirportRequest;
import exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import validation.AddressValidationService;
import validation.AirportValidationService;
import validation.RequestParameterValidationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirportValidationServiceTest {
    @Mock
    private AddressValidationService addressValidationService;
    @Mock
    private RequestParameterValidationService requestParameterValidationService;
    @InjectMocks
    private AirportValidationService airportValidationService;

    @Test
    void validateCreateRequestSuccess() {
        var addressDto = mock(AddressDto.class);
        var request = new CreateAirportRequest("FFF", "AIRPORT", addressDto);

        assertDoesNotThrow(() -> airportValidationService.validateCreateRequest(request));

        verify(addressValidationService).validate(eq(addressDto), any());
    }

    @Test
    void validateCreateRequestInvalidFormats() {
        CreateAirportRequest request = new CreateAirportRequest("jfk", "Ab", mock(AddressDto.class));

        var ex = assertThrows(ValidationException.class, () -> airportValidationService.validateCreateRequest(request));

        assertTrue(ex.getValidationErrorMessages().stream().anyMatch(s -> s.contains("Invalid name format")));
        assertTrue(ex.getValidationErrorMessages().stream().anyMatch(s -> s.contains("Invalid code format")));
    }

    @Test
    void validateUpdateRequestIdAndNameErrors() {
        var request = new UpdateAirportRequest(-1L, null, "LAX", mock(AddressDto.class));

        doThrow(new ValidationException(List.of("ID must be positive")))
                .when(requestParameterValidationService).validateId(-1L);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> airportValidationService.validateUpdateRequest(request));

        assertEquals(2, ex.getValidationErrorMessages().size());
    }

    @Test
    void validateCodeBoundaryValue() {
        var requestOne = new CreateAirportRequest(" AAAs fsa", "JF", mock(AddressDto.class));
        var requestTwo = new CreateAirportRequest("BBB f", "JFKX", mock(AddressDto.class));

        assertThrows(ValidationException.class, () -> airportValidationService.validateCreateRequest(requestOne));
        assertThrows(ValidationException.class, () -> airportValidationService.validateCreateRequest(requestTwo));
    }
}
