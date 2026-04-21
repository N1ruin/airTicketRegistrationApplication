package dto.flight;

import com.fasterxml.jackson.annotation.JsonFormat;
import dto.airport.AirportDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Запрос на создание нового рейса")
public record CreateFlightRequest(
        @Schema(description = "Общее количество мест на рейсе", example = "180",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Integer allSeats,

        @Schema(description = "Количество свободных мест", example = "180")
        Integer freeSeats,

        @Schema(description = "Аэропорт вылета")
        AirportDto departureAirportDto,

        @Schema(description = "Аэропорт прибытия")
        AirportDto arrivalAirportDto,

        @Schema(description = "Дата и время вылета", example = "2024-12-31 23:50:00",
                type = "string", pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime departureDate,

        @Schema(description = "Дата и время прибытия", example = "2025-01-01 04:30:00",
                type = "string", pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime arrivalDate) {
}
