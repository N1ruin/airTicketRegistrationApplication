package dto.airport;

import dto.address.AddressDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Запрос на создание нового аэропорта")
public record CreateAirportRequest(
        @Schema(description = "Международный код аэропорта", example = "DME", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Airport code is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Code must be 3 uppercase letters")
        String code,

        @Schema(description = "Название аэропорта", example = "Домодедово")
        @NotNull(message = "Airport name is required")
        @Pattern(regexp = "^[a-zA-Z0-9.\\s-]{3,100}$", message = "Invalid name format")
        String name,

        @Schema(description = "Данные адреса для регистрации аэропорта")
        @NotNull(message = "Address is required")
        @Valid
        AddressDto addressDto) {
}
