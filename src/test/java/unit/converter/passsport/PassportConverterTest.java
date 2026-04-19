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
        var passportDto = new PassportDto("Series", "Number", "Citizenship", issueDate,
                expiredDate);

        var result = converter.convert(passportDto);

        assertNotNull(result);
        assertEquals("Series", passportDto.series());
        assertEquals("Number", passportDto.number());
        assertEquals("Citizenship", passportDto.citizenship());
        assertEquals(issueDate, passportDto.issueDate());
        assertEquals(expiredDate, passportDto.expiredDate());
    }
}
