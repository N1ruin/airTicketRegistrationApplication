package validation;

import dto.passport.PassportDto;

import java.time.LocalDate;
import java.util.List;

public class PassportValidationService {
    private static final String SERIES_PATTERN = "^[A-Z0-9А-Я]{0,12}$";
    private static final String NUMBER_PATTERN = "^[A-Z0-9А-Я]{5,20}$";
    private static final String CITIZENSHIP_PATTERN = "^[A-Z]{2,3}$";

    public void validatePassportDto(PassportDto passportDto, List<String> errors) {
        if (passportDto.series() != null) {
            validateSeries(passportDto.series(), errors);
        }

        validateNumber(passportDto.number(), errors);
        validateCitizenship(passportDto.citizenship(), errors);
        validateExpriredDate(passportDto.expiredDate(), errors);
    }

    private void validateSeries(String series, List<String> errors) {
        if (!series.matches(SERIES_PATTERN)) {
            errors.add("Passport series must contain only letters or numbers (up to 12 characters).");
        }
    }

    private void validateNumber(String number, List<String> errors) {
        if (!number.matches(NUMBER_PATTERN)) {
            errors.add("Passport number must be between 5 and 20 characters (letters and numbers only).");
        }
    }

    private void validateCitizenship(String citizenship, List<String> errors) {
        if (!citizenship.matches(CITIZENSHIP_PATTERN)) {
            errors.add("Citizenship must be a valid 2 or 3-letter ISO country code.");
        }
    }

    private void validateExpriredDate(LocalDate expiredDate, List<String> errors) {
        if (expiredDate.isBefore(LocalDate.now())) {
            errors.add("Passport expired.");
        }
    }
}
