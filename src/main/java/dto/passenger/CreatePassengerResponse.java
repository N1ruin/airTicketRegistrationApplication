package dto.passenger;

import com.fasterxml.jackson.annotation.JsonFormat;
import dto.passport.PassportDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Ответ с данными созданного пассажира")
public record CreatePassengerResponse(
        @Schema(description = "Id пассажира", example = "10",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,

        @Schema(description = "Имя", example = "Иван")
        String firstName,

        @Schema(description = "Фамилия", example = "Иванов")
        String lastName,

        @Schema(description = "Отчество", example = "Иванович", nullable = true)
        String fatherName,

        @Schema(description = "Пол (true — мужской, false — женский)", example = "true")
        boolean male,

        @Schema(description = "Дата рождения", example = "1990-05-15",
                type = "string", pattern = "yyyy-MM-dd")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate birthDate,

        @Schema(description = "Данные паспорта")
        PassportDto passportDto,

        @Schema(description = "ID пользователя, к которому привязан пассажир", example = "1")
        Long userId) {
}
