package dto.flight;

import com.fasterxml.jackson.annotation.JsonFormat;
import dto.airport.AirportDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Запрос на обновление данных существующего рейса")
public record UpdateFlightRequest(
        @Schema(description = "Id рейса, данные которого необходимо обновить", example = "123",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,

        @Schema(description = "Новое общее количество мест", example = "150", nullable = true)
        Integer allSeats,

        @Schema(description = "Обновленное количество свободных мест", example = "10", nullable = true)
        Integer freeSeats,

        @Schema(description = "Новые данные аэропорта вылета", nullable = true)
        AirportDto departureAirportDto,

        @Schema(description = "Новые данные аэропорта прибытия", nullable = true)
        AirportDto arrivalAirportDto,

        @Schema(description = "Новая дата и время вылета", example = "2024-11-20 12:00:00",
                type = "string", pattern = "yyyy-MM-dd HH:mm:ss", nullable = true)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime departureDate,

        @Schema(description = "Новая дата и время прибытия", example = "2024-11-20 16:30:00",
                type = "string", pattern = "yyyy-MM-dd HH:mm:ss", nullable = true)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime arrivalDate) {
}
