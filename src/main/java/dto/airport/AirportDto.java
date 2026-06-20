package dto.airport;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация об аэропорте")
public record AirportDto(
        @Schema(description = "Международный код аэропорта", example = "SVO")
        String code,

        @Schema(description = "Название аэропорта", example = "Шереметьево имени А.С. Пушкина")
        String name,

        @Schema(description = "Id адреса", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long addressId) {
}
