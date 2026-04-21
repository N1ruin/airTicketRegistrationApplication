package dto.flight;

import com.fasterxml.jackson.annotation.JsonFormat;
import dto.airport.AirportDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Информация о созданном рейсе")
public record CreateFlightResponse(
        @Schema(description = "Id рейса", example = "500",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,

        @Schema(description = "Общее количество пассажирских мест", example = "200")
        Integer allSeats,

        @Schema(description = "Количество доступных свободных мест", example = "200")
        Integer freeSeats,

        @Schema(description = "Подробная информация об аэропорте вылета")
        AirportDto departureAirportDto,

        @Schema(description = "Подробная информация об аэропорте прибытия")
        AirportDto arrivalAirportDto,

        @Schema(description = "Дата и время отправления рейса", example = "2024-10-15 10:00:00",
                type = "string", pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime departureDate,

        @Schema(description = "Дата и время прибытия рейса", example = "2024-10-15 14:30:00",
                type = "string", pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime arrivalDate) {
}
