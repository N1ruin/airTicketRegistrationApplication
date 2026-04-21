package dto.ticket;


import com.fasterxml.jackson.annotation.JsonFormat;
import domain.ServiceClass;
import domain.TicketStatus;
import dto.flight.FlightDto;
import dto.passenger.PassengerDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Полная информация о билете")
public record TicketDto(
        @Schema(description = "Id билета", example = "12")
        Long id,
        @Schema(description = "Текущий статус билета ", example = "ACTIVE")
        TicketStatus ticketStatus,

        @Schema(description = "Уникальный номер билета", example = "100020003000")
        Long ticketNumber,

        @Schema(description = "Класс обслуживания", example = "ECONOMY")
        ServiceClass serviceClass,

        @Schema(description = "Номер места", example = "12")
        Integer seatNumber,

        @Schema(description = "Информация о рейсе")
        FlightDto flight,

        @Schema(description = "Информация о пассажире")
        PassengerDto passenger,

        @Schema(description = "Дата и время приобретения", example = "2024-06-12 14:20:00",
                type = "string", pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime purchaseDate,

        @Schema(description = "Допустимый вес багажа (кг)", example = "23.5")
        Double baggageWeight,

        @Schema(description = "Допустимый вес ручной клади (кг)", example = "8.0")
        Double carryOnBaggageWeight) {
}
