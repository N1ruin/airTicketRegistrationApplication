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
        var birthDate = LocalDate.of(1990, 1, 1);
        var passport = new Passport();
        passport.setFirstName("Иван");
        passport.setLastName("Петров");
        passport.setFatherName("Сергеевич");
        passport.setMale(true);
        passport.setSeries("4510");
        passport.setNumber("123456");
        passport.setCitizenship("РФ");
        passport.setBirthDate(birthDate);
        passport.setIssueDate(issueDate);
        passport.setExpiredDate(expiredDate);

        var result = passportDtoConverter.convert(passport);

        assertNotNull(result);
        assertEquals("Иван", result.firstName());
        assertEquals("Петров", result.lastName());
        assertEquals("Сергеевич", result.fatherName());
        assertTrue(result.isMale());
        assertEquals("4510", result.series());
        assertEquals("123456", result.number());
        assertEquals("РФ", result.citizenship());
        assertEquals(birthDate, result.birthDate());
        assertEquals(issueDate, result.issueDate());
        assertEquals(expiredDate, result.expiredDate());
    }
}
