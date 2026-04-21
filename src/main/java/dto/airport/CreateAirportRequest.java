package dto.airport;

import dto.address.AddressDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на создание нового аэропорта")
public record CreateAirportRequest(
        @Schema(description = "Международный код аэропорта", example = "DME",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String code,
        @Schema(description = "Название аэропорта", example = "Домодедово")
        String name,
        @Schema(description = "Данные адреса для регистрации аэропорта")
        AddressDto addressDto) {
}
