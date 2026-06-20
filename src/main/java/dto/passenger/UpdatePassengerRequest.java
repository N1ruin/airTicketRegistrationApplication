package dto.passenger;

import dto.passport.PassportDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Запрос на обновление данных пассажира")
public record UpdatePassengerRequest(
        @Schema(description = "Id пассажира, данные которого обновляются", example = "10",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Passenger id is required")
        @Positive(message = "Passenger id must be positive")
        Long id,

        @Schema(description = "Новые данные паспорта", nullable = true)
        @NotNull(message = "Passport data is required")
        @Valid
        PassportDto passportDto) {
}
