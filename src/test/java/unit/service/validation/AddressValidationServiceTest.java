package unit.service.validation;

import dto.address.AddressDto;
import org.junit.jupiter.api.Test;
import validation.AddressValidationService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AddressValidationServiceTest {
    private final AddressValidationService service = new AddressValidationService();

    @Test
    void validateSuccess() {
        var dto = new AddressDto("Country", "City", "Street", 10);
        var errors = new ArrayList<String>();

        service.validate(dto, errors);

        assertTrue(errors.isEmpty());
    }

    @Test
    void validateInvalidFormats() {
        AddressDto dto = new AddressDto("Country123", "City32$", "fads$%", 15);
        var errors = new ArrayList<String>();

        service.validate(dto, errors);

        assertEquals(3, errors.size());
        assertTrue(errors.contains("Country should contain only letters, spaces, or hyphens."));
        assertTrue(errors.contains("City should contain letters, numbers, spaces, or hyphens."));
        assertTrue(errors.contains("Street should contain letters, numbers, spaces, dots, comma or hyphens."));
    }

    @Test
    void validateHouseNumberNull() {
        var dtoNull = new AddressDto("Country", "City", "Street", null);
        var errors = new ArrayList<String>();

        service.validate(dtoNull, errors);

        assertEquals(1, errors.size());
        assertTrue(errors.contains("House number cannot be null"));
    }

    @Test
    void validateHouseNumberNegative() {
        AddressDto dtoNegative = new AddressDto("Country", "City", "Street", -5);
        var errors = new ArrayList<String>();

        service.validate(dtoNegative, errors);

        assertEquals(1, errors.size());
        assertTrue(errors.contains("The house -5 number cannot be negative."));
    }

    @Test
    void validateStreetPatterns() {
        AddressDto dto = new AddressDto("Country", "City", "13 avenue", 1);
        var errors = new ArrayList<String>();

        service.validate(dto, errors);

        assertTrue(errors.isEmpty());
    }
}
