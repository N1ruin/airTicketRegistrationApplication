package dto.passport;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import validation.annotation.ValidDateRange;

import java.time.LocalDate;

@Schema(description = "Данные паспорта")
@ValidDateRange(startDate = "issueDate", endDate = "expiredDate")
public record PassportDto(
        @Schema(description = "Имя пассажира", example = "Иван",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "First name is required")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ]+([\\s-][a-zA-Zа-яА-ЯёЁ]+)*$", message = "Invalid first name format")
        String firstName,

        @Schema(description = "Фамилия пассажира", example = "Иванов",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Last name is required")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ]+([\\s-][a-zA-Zа-яА-ЯёЁ]+)*$", message = "Invalid last name format")
        String lastName,

        @Schema(description = "Отчество пассажира", example = "Иванович", nullable = true)
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ]+([\\s-][a-zA-Zа-яА-ЯёЁ]+)*$", message = "Invalid father name format")
        String fatherName,

        @Schema(description = "Пол (true — мужской, false — женский)", example = "true")
        @NotNull(message = "Gender is required")
        Boolean isMale,

        @Schema(description = "Серия паспорта", example = "4510")
        @NotNull(message = "Passport series is required")
        @Pattern(regexp = "^[A-Z0-9А-Я]{4,12}$", message = "Passport series must contain 4-12 letters or numbers")
        String series,

        @Schema(description = "Номер паспорта", example = "123456")
        @NotNull(message = "Passport number is required")
        @Pattern(regexp = "^[A-Z0-9А-Я]{5,20}$", message = "Passport number must be between 5 and 20 characters")
        String number,

        @Schema(description = "Гражданство", example = "РФ")
        @NotNull(message = "Citizenship is required")
        @Pattern(regexp = "^[A-Z]{2,3}$", message = "Citizenship must be a valid 2 or 3-letter ISO country code")
        String citizenship,

        @Schema(description = "Дата рождения", example = "1990-05-15",
                type = "string", pattern = "yyyy-MM-dd")
        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate birthDate,

        @Schema(description = "Дата выдачи", example = "2010-05-20",
                type = "string", pattern = "yyyy-MM-dd")
        @NotNull(message = "Issue date is required")
        @Past(message = "Issue date must be in the past")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate issueDate,

        @Schema(description = "Дата окончания срока действия", example = "2030-05-20",
                type = "string", pattern = "yyyy-MM-dd")
        @NotNull(message = "Expired date is required")
        @Future(message = "Expired date must be in the future")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate expiredDate) {
}
