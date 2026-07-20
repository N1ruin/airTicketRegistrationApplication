package dto.flight;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

@Schema(description = "Общая информация о рейсе")
public record FlightDto(
        @Schema(description = "Id рейса", example = "12",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,

        @Schema(description = "Общее количество мест", example = "150")
        int seatsCount,

        @Schema(description = "Количество свободных мест", example = "45")
        int freeSeats,

        @Schema(description = "ID аэропорта вылета")
        String departureAirportId,

        @Schema(description = "ID аэропорта прибытия")
        String arrivalAirportId,

        @Schema(description = "Дата и время вылета",
                example = "2024-11-20T12:00:00+03:00",
                type = "string",
                format = "date-time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        ZonedDateTime departureDate,

        @Schema(description = "Дата и время прибытия",
                example = "2024-11-20T16:30:00+03:00",
                type = "string",
                format = "date-time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        ZonedDateTime arrivalDate) {
}
