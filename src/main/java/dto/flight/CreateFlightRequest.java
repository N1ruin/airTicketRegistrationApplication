package dto.flight;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import validation.annotation.ValidDateRange;

import java.time.ZonedDateTime;

@Schema(description = "Запрос на создание нового рейса")
@ValidDateRange(startDate = "departureDate", endDate = "arrivalDate")
public record CreateFlightRequest(
        @Schema(description = "Общее количество мест на рейсе", example = "180",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "All seats count is required")
        @Positive(message = "All seats must be positive")
        Integer allSeats,

        @Schema(description = "Количество свободных мест", example = "180")
        @NotNull(message = "Free seats count is required")
        @Positive(message = "Free seats must be positive")
        Integer freeSeats,

        @Schema(description = "Код аэропорта вылета")
        @NotNull(message = "Departure airport code is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Code must be 3 uppercase letters")
        String departureAirportCode,

        @Schema(description = "Код аэропорта прибытия")
        @NotNull(message = "Arrival airport code is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Code must be 3 uppercase letters")
        String arrivalAirportCode,

        @Schema(description = "Дата и время вылета", example = "2024-11-20T13:30:00+03:00",
                type = "string", pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        @NotNull(message = "Departure date is required")
        @Future(message = "Departure date must be in the future")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        ZonedDateTime departureDate,

        @Schema(description = "Дата и время прибытия", example = "2024-11-20T16:30:00+03:00",
                type = "string", pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        @NotNull(message = "Arrival date is required")
        @Future(message = "Arrival date must be in the future")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        ZonedDateTime arrivalDate) {
}
