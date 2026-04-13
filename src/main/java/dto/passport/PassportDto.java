package dto.passport;

import java.time.LocalDate;

public record PassportDto(String series,
                          String number,
                          String citizenship,
                          LocalDate issueDate,
                          LocalDate expiredDate) {
}
