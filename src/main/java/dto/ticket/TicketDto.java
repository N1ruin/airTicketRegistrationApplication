package dto.ticket;


import com.fasterxml.jackson.annotation.JsonFormat;
import domain.TicketRank;
import domain.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

@Schema(description = "Полная информация о билете")
public record TicketDto(
        @Schema(description = "Id билета", example = "12")
        Long id,

        @Schema(description = "Текущий статус билета ", example = "ACTIVE")
        TicketStatus ticketStatus,

        @Schema(description = "Уникальный номер билета", example = "100020003000")
        long ticketNumber,

        @Schema(description = "Класс обслуживания", example = "ECONOMY")
        TicketRank ticketRank,

        @Schema(description = "Номер места", example = "12")
        int seatNumber,

        @Schema(description = "Id рейса")
        Long flightId,

        @Schema(description = "Id пассажира")
        Long passengerId,

        @Schema(description = "Дата и время приобретения", example = "2024-11-20T12:00:00+03:00",
                type = "string", pattern = "yyyy-MM-dd HH:mm:ssXXX")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ssXXX")
        ZonedDateTime purchaseDate,

        @Schema(description = "Допустимый вес багажа (кг)", example = "23.5")
        double baggageWeight,

        @Schema(description = "Допустимый вес ручной клади (кг)", example = "8.0")
        double carryOnBaggageWeight) {
}
