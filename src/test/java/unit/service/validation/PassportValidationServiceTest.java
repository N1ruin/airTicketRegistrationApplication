package unit.service.validation;

import dto.passport.PassportDto;
import org.junit.jupiter.api.Test;
import validation.PassportValidationService;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PassportValidationServiceTest {
    private final PassportValidationService service = new PassportValidationService();

    @Test
    void validatePassportDtoSuccessErrorListEmpty() {
        PassportDto dto = new PassportDto("AB", "1234567", "RUS",
                LocalDate.now().minusYears(1), LocalDate.now().plusYears(5));
        var errors = new ArrayList<String>();

        service.validatePassportDto(dto, errors);

        assertTrue(errors.isEmpty());
    }

    @Test
    void validatePassportDtoNullFieldsErrorListSizeFour() {
        PassportDto dto = new PassportDto(null, null, null, null, null);
        var errors = new ArrayList<String>();

        service.validatePassportDto(dto, errors);

        assertEquals(4, errors.size());
    }

    @Test
    void validatePassportDtoInvalidDataErrorListSizeFive() {
        PassportDto dto = new PassportDto("INVALID_SERIES_LONG", "123", "RUSSIA",
                LocalDate.now().plusDays(1), LocalDate.now().minusDays(1));
        var errors = new ArrayList<String>();

        service.validatePassportDto(dto, errors);

        assertEquals(5, errors.size());
    }

    @Test
    void validatePassportDtoTodayIssueDate() {
        PassportDto dto = new PassportDto(
                "SERIES", "NUMBER123", "BY", LocalDate.now(),
                LocalDate.now().plusDays(1));
        var errors = new ArrayList<String>();

        service.validatePassportDto(dto, errors);

        assertFalse(errors.contains("Invalid issue date"));
    }
}
