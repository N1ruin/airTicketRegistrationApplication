package dto.flight;

import com.fasterxml.jackson.annotation.JsonFormat;
import dto.airport.AirportDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Общая информация о рейсе")
public record FlightDto(
        @Schema(description = "Id рейса", example = "12",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,

        @Schema(description = "Общее количество мест", example = "150")
        Integer allSeats,

        @Schema(description = "Количество свободных мест", example = "45")
        Integer freeSeats,

        @Schema(description = "Данные аэропорта вылета")
        AirportDto departureAirportDto,

        @Schema(description = "Данные аэропорта прибытия")
        AirportDto arrivalAirportDto,

        @Schema(description = "Дата и время вылета", example = "2024-11-20 12:00:00",
                type = "string", pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime departureDate,

        @Schema(description = "Дата и время прибытия", example = "2024-11-20 16:30:00",
                type = "string", pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime arrivalDate) {
}
