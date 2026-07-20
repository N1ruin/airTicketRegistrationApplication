package dto.ticket;

import domain.TicketRank;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Запрос на оформление (создание) билета")
public record CreateTicketRequest(
        @Schema(description = "Класс обслуживания", example = "ECONOMY",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Ticket rank is required")
        TicketRank ticketRank,

        @Schema(description = "Номер места", example = "24")
        @NotNull(message = "Seat number is required")
        @Positive(message = "Seat number must be positive")
        int seatNumber,

        @Schema(description = "Id рейса")
        @NotNull(message = "Flight id is required")
        @Positive(message = "Flight id must be positive")
        Long flightId,

        @Schema(description = "Id пассажира")
        @NotNull(message = "Passenger id is required")
        @Positive(message = "Passenger id must be positive")
        Long passengerId,

        @Schema(description = "Вес регистрируемого багажа (кг)", example = "23.0")
        @NotNull(message = "Baggage weight is required")
        @DecimalMin(value = "0.0", message = "Baggage weight cannot be negative")
        double baggageWeight,

        @Schema(description = "Вес ручной клади (кг)", example = "10.0")
        @NotNull(message = "Carry-on baggage weight is required")
        @DecimalMin(value = "0.0", message = "Carry-on baggage weight cannot be negative")
        double carryOnBaggageWeight) {
}
