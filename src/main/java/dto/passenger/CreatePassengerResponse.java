package dto.passenger;

import com.fasterxml.jackson.annotation.JsonFormat;
import dto.passport.PassportDto;

import java.time.LocalDate;

public record CreatePassengerResponse(Long id,
                                      String firstName,
                                      String lastName,
                                      String fatherName,
                                      boolean male,
                                      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                                      LocalDate birthDate,
                                      PassportDto passportDto) {
}
