package dto.airport;

import dto.address.AddressDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Запрос на обновление данных аэропорта")
public record UpdateAirportRequest(
        @Schema(description = "Новое название аэропорта", example = "Пулково", nullable = true)
        @NotNull(message = "Airport name is required")
        @Pattern(regexp = "^[a-zA-Z0-9.\\s-]{3,100}$", message = "Invalid name format")
        String name,

        @NotNull(message = "Status is required")
        Boolean isWorked,

        @Schema(description = "Новые данные адреса", nullable = true)
        @NotNull(message = "Address is required")
        @Valid
        AddressDto addressDto) {
}
