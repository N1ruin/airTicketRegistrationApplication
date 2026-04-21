package dto.flight;

import com.fasterxml.jackson.annotation.JsonFormat;
import dto.airport.AirportDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Результат обновления данных рейса")
public record UpdateFlightResponse(
        @Schema(description = "Id обновленного рейса", example = "777",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,

        @Schema(description = "Актуальное общее количество мест", example = "150")
        Integer allSeats,

        @Schema(description = "Актуальное количество свободных мест", example = "10")
        Integer freeSeats,

        @Schema(description = "Актуальные данные аэропорта вылета")
        AirportDto departureAirportDto,

        @Schema(description = "Актуальные данные аэропорта прибытия")
        AirportDto arrivalAirportDto,

        @Schema(description = "Актуальная дата и время вылета", example = "2024-11-20 12:00:00",
                type = "string", pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime departureDate,

        @Schema(description = "Актуальная дата и время прибытия", example = "2024-11-20 16:30:00",
                type = "string", pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime arrivalDate) {
}
