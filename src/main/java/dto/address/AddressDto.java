package dto.address;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Данные адреса")
public record AddressDto(
        @Schema(description = "Страна", example = "Россия")
        String country,
        @Schema(description = "Город", example = "Москва")
        String city,
        @Schema(description = "Улица", example = "Ленина")
        String street,
        @Schema(description = "Номер дома", example = "10")
        Integer houseNumber) {
}
