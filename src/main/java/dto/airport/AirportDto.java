package dto.airport;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Schema(description = "Информация об аэропорте")
public record AirportDto(
        @Schema(description = "Международный код аэропорта", example = "SVO")
        String code,

        @Schema(description = "Название аэропорта", example = "Шереметьево имени А.С. Пушкина")
        String name,

        @Schema(description = "Страна", example = "Россия")
        @NotNull(message = "Country is required")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ\\s-]{1,70}$",
                message = "Country should contain only letters, spaces, or hyphens")
        String country,

        @Schema(description = "Город", example = "Москва")
        @NotNull(message = "City is required")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ0-9\\s-]{1,100}$",
                message = "City should contain letters, numbers, spaces, or hyphens")
        String city,

        @Schema(description = "Улица", example = "Ленина")
        @NotNull(message = "Street is required")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ0-9\\s-.,]{1,100}$",
                message = "Street should contain letters, numbers, spaces, dots, comma or hyphens")
        String street,

        @Schema(description = "Номер дома", example = "10")
        @NotNull(message = "House number is required")
        @Pattern(regexp = "^\\d+[\\s\\w\\d.,-]*$",
                message = "Некорректный формат номера дома")
        String houseNumber) {
}
