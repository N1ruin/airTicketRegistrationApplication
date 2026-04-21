package dto.airport;

import dto.address.AddressDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на обновление данных аэропорта")
public record UpdateAirportRequest(
        @Schema(description = "Id аэропорта, данные которого обновляются", example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,
        @Schema(description = "Новый международный код аэропорта", example = "LED",
                nullable = true)
        String code,
        @Schema(description = "Новое название аэропорта", example = "Пулково",
                nullable = true)
        String name,
        @Schema(description = "Новые данные адреса",
                nullable = true)
        AddressDto addressDto) {
}
