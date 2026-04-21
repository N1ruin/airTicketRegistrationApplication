package dto.passenger;

import com.fasterxml.jackson.annotation.JsonFormat;
import dto.passport.PassportDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Результат обновления данных пассажира")
public record UpdatePassengerResponse(
        @Schema(description = "Id обновленного пассажира", example = "10",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,

        @Schema(description = "Обновленное имя", example = "Иван")
        String firstName,

        @Schema(description = "Обновленная фамилия", example = "Иванов")
        String lastName,

        @Schema(description = "Обновленное отчество", example = "Иванович", nullable = true)
        String fatherName,

        @Schema(description = "Пол (true — мужской, false — женский)", example = "true")
        boolean male,

        @Schema(description = "Актуальная дата рождения", example = "1990-05-15",
                type = "string", pattern = "yyyy-MM-dd")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate birthDate,

        @Schema(description = "Актуальные данные паспорта")
        PassportDto passportDto) {
}
