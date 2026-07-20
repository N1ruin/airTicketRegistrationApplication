package dto.flight;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import validation.annotation.ValidDateRange;

import java.time.ZonedDateTime;

@Schema(description = "Запрос на обновление данных существующего рейса")
@ValidDateRange(startDate = "departureDate", endDate = "arrivalDate")
public record UpdateFlightRequest(
        @Schema(description = "ID рейса, данные которого необходимо обновить", example = "123",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Flight id is required")
        @Positive(message = "Flight id must be positive")
        Long id,

        @Schema(description = "Новое общее количество мест", example = "150", nullable = true)
        @NotNull(message = "Seats count is required")
        @Positive(message = "Seats must be positive")
        int seatsCount,

        @Schema(description = "Обновленное количество свободных мест", example = "10", nullable = true)
        @NotNull(message = "Free seats count is required")
        @Positive(message = "Free seats must be positive")
        int freeSeats,

        @Schema(description = "ID нового аэропорта вылета", nullable = true)
        @NotEmpty(message = "Departure airport id is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Code must be 3 uppercase letters")
        String departureAirportId,

        @Schema(description = "ID нового аэропорта прибытия", nullable = true)
        @NotEmpty(message = "Arrival airport id is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Code must be 3 uppercase letters")
        String arrivalAirportId,

        @Schema(description = "Новая дата и время вылета", example = "2024-11-20T16:30:00+03:00",
                type = "string", pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", nullable = true)
        @NotNull(message = "Departure date is required")
        @Future(message = "Departure date must be in the future")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        ZonedDateTime departureDate,

        @Schema(description = "Новая дата и время прибытия", example = "2024-11-20T16:30:00+03:00",
                type = "string", pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", nullable = true)
        @NotNull(message = "Arrival date is required")
        @Future(message = "Arrival date must be in the future")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        ZonedDateTime arrivalDate) {
}
