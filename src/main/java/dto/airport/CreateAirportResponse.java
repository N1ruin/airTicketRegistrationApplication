package dto.airport;

import dto.address.AddressDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ после успешного создания аэропорта")
public record CreateAirportResponse(
        @Schema(description = "Id созданного аэропорта", example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,
        @Schema(description = "Международный код аэропорта (IATA)", example = "DME")
        String code,
        @Schema(description = "Название аэропорта", example = "Домодедово")
        String name,
        @Schema(description = "Данные адреса созданного аэропорта")
        AddressDto addressDto) {
}
