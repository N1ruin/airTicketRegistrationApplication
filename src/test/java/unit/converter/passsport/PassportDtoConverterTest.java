package unit.converter.passsport;

import converter.passsport.PassportDtoConverter;
import domain.Passport;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PassportDtoConverterTest {
    private final PassportDtoConverter passportDtoConverter = new PassportDtoConverter();

    @Test
    void convertPassportToPassportDtoSuccess() {
        var issueDate = LocalDate.now();
        var expiredDate = LocalDate.now().plusYears(5);
        var passport = new Passport();
        passport.setSeries("Series");
        passport.setNumber("Number");
        passport.setCitizenship("Citizenship");
        passport.setIssueDate(issueDate);
        passport.setExpiredDate(expiredDate);

        var result = passportDtoConverter.convert(passport);

        assertNotNull(result);
        assertEquals("Series", passport.getSeries());
        assertEquals("Number", passport.getNumber());
        assertEquals("Citizenship", passport.getCitizenship());
        assertEquals(issueDate, passport.getIssueDate());
        assertEquals(expiredDate, passport.getExpiredDate());
    }
}
