package dto.passenger;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import validation.annotation.ValidDateRange;

import java.time.LocalDate;

@Schema(description = "Запрос на создание нового пассажира")
@ValidDateRange(startDate = "passportIssueDate", endDate = "passportExpiredDate")
public record CreatePassengerRequest(
        @Schema(description = "Имя пассажира", example = "Иван",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "First name is required")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ]+([\\s-][a-zA-Zа-яА-ЯёЁ]+)*$", message = "Invalid first name format")
        String firstName,

        @Schema(description = "Фамилия пассажира", example = "Иванов",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "Last name is required")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ]+([\\s-][a-zA-Zа-яА-ЯёЁ]+)*$", message = "Invalid last name format")
        String lastName,

        @Schema(description = "Отчество пассажира", example = "Иванович", nullable = true)
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ]+([\\s-][a-zA-Zа-яА-ЯёЁ]+)*$", message = "Invalid father name format")
        String fatherName,

        @Schema(description = "Пол (true — мужской, false — женский)", example = "true")
        @NotNull(message = "Gender is required")
        boolean isMale,

        @Schema(description = "Серия паспорта", example = "4510")
        @NotEmpty(message = "Passport series is required")
        @Pattern(regexp = "^[A-Z0-9А-Я]{4,12}$", message = "Passport series must contain 4-12 letters or numbers")
        String passportSeries,

        @Schema(description = "Номер паспорта", example = "123456")
        @NotEmpty(message = "Passport number is required")
        @Pattern(regexp = "^[A-Z0-9А-Я]{5,20}$", message = "Passport number must be between 5 and 20 characters")
        String passportNumber,

        @Schema(description = "Гражданство", example = "РФ")
        @NotEmpty(message = "Citizenship is required")
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
        LocalDate passportIssueDate,

        @Schema(description = "Дата окончания срока действия", example = "2030-05-20",
                type = "string", pattern = "yyyy-MM-dd")
        @NotNull(message = "Expired date is required")
        @Future(message = "Expired date must be in the future")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate passportExpiredDate) {
}
