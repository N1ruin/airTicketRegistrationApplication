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
        @NotNull(message = "Seats count count is required")
        @Positive(message = "Seats count must be positive")
        int seatsCount,

        @Schema(description = "Количество свободных мест", example = "180")
        @NotNull(message = "Free seats count is required")
        @Positive(message = "Free seats must be positive")
        int freeSeats,

        @Schema(description = "ID аэропорта вылета")
        @NotEmpty(message = "Departure airport id is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Code must be 3 uppercase letters")
        String departureAirportId,

        @Schema(description = "ID аэропорта прибытия")
        @NotEmpty(message = "Arrival airport id is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Code must be 3 uppercase letters")
        String arrivalAirportId,

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
