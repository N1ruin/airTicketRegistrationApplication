package dto.passenger;

import com.fasterxml.jackson.annotation.JsonFormat;
import dto.passport.PassportDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Запрос на обновление данных пассажира")
public record UpdatePassengerRequest(
        @Schema(description = "Id пассажира, данные которого обновляются", example = "10",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,

        @Schema(description = "Новое имя", example = "Иван", nullable = true)
        String firstName,

        @Schema(description = "Новая фамилия", example = "Иванов", nullable = true)
        String lastName,

        @Schema(description = "Новое отчество", example = "Иванович", nullable = true)
        String fatherName,

        @Schema(description = "Пол (true — мужской, false — женский)", example = "true")
        boolean male,

        @Schema(description = "Новая дата рождения", example = "1990-05-15",
                type = "string", pattern = "yyyy-MM-dd", nullable = true)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate birthDate,

        @Schema(description = "Новые данные паспорта", nullable = true)
        PassportDto passportDto) {
}
