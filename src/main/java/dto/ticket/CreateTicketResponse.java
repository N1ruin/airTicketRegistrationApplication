package dto.ticket;

import com.fasterxml.jackson.annotation.JsonFormat;
import dto.flight.FlightDto;
import dto.passenger.PassengerDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Ответ с данными оформленного билета")
public record CreateTicketResponse(
        @Schema(description = "Id билета в системе", example = "5001",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,

        @Schema(description = "Уникальный номер билета", example = "123456789012")
        Long ticketNumber,

        @Schema(description = "Класс обслуживания", example = "ECONOMY")
        String serviceClass,

        @Schema(description = "Номер места", example = "24")
        Integer seatNumber,

        @Schema(description = "Информация о рейсе")
        FlightDto flight,

        @Schema(description = "Информация о пассажире")
        PassengerDto passenger,

        @Schema(description = "Дата и время покупки билета", example = "2024-05-20 15:30:00",
                type = "string", pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime purchaseDate,

        @Schema(description = "Вес багажа (кг)", example = "23.0")
        Double baggageWeight,

        @Schema(description = "Вес ручной клади (кг)", example = "10.0")
        Double carryOnBaggageWeight) {
}
