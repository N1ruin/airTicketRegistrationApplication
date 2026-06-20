package unit.converter.passsport;

import converter.passsport.PassportConverter;
import dto.passport.PassportDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PassportConverterTest {
    private final PassportConverter converter = new PassportConverter();

    @Test
    void convertPassportDtoToPassportSuccess() {
        var issueDate = LocalDate.now();
        var expiredDate = LocalDate.now().plusYears(5);
        var birthDate = LocalDate.of(1990, 1, 1);
        var passportDto = new PassportDto(
                "Иван",
                "Петров",
                "Сергеевич",
                true,
                "4510",
                "123456",
                "РФ",
                birthDate,
                issueDate,
                expiredDate);
        var result = converter.convert(passportDto);

        assertNotNull(result);
        assertEquals("Иван", result.getFirstName());
        assertEquals("Петров", result.getLastName());
        assertEquals("Сергеевич", result.getFatherName());
        assertTrue(result.isMale());
        assertEquals("4510", result.getSeries());
        assertEquals("123456", result.getNumber());
        assertEquals("РФ", result.getCitizenship());
        assertEquals(birthDate, result.getBirthDate());
        assertEquals(issueDate, result.getIssueDate());
        assertEquals(expiredDate, result.getExpiredDate());
    }
}
