package dto.airport;

import dto.address.AddressDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Результат обновления данных аэропорта")
public record UpdateAirportResponse(
        @Schema(description = "Id обновленного аэропорта", example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,
        @Schema(description = "Обновленный международный код аэропорта", example = "LED")
        String code,
        @Schema(description = "Обновленное название аэропорта", example = "Пулково")
        String name,
        @Schema(description = "Обновленные данные адреса")
        AddressDto addressDto) {
}
