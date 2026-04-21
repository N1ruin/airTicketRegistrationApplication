package dto.passenger;

import com.fasterxml.jackson.annotation.JsonFormat;
import dto.passport.PassportDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Запрос на создание записи о пассажире")
public record CreatePassengerRequest(
        @Schema(description = "Имя пассажира", example = "Иван",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String firstName,

        @Schema(description = "Фамилия пассажира", example = "Иванов",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String lastName,

        @Schema(description = "Отчество пассажира", example = "Иванович", nullable = true)
        String fatherName,

        @Schema(description = "Пол (true — мужской, false — женский)", example = "true")
        boolean male,

        @Schema(description = "Дата рождения", example = "1990-05-15",
                type = "string", pattern = "yyyy-MM-dd")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate birthDate,

        @Schema(description = "Данные паспорта пассажира")
        PassportDto passportDto) {
}
