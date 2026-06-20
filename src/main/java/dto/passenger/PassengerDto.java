package dto.passenger;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация о пассажире")
public record PassengerDto(
        @Schema(description = "Id пассажира", example = "10",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,

        Long passportId,

        @Schema(description = "Id пользователя-владельца профиля", example = "1")
        Long userId) {
}
