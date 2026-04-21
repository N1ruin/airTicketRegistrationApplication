package dto.ticket;

import dto.flight.FlightDto;
import dto.passenger.PassengerDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на оформление (создание) билета")
public record CreateTicketRequest(
        @Schema(description = "Класс обслуживания", example = "ECONOMY",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String serviceClass,

        @Schema(description = "Номер места", example = "24")
        Integer seatNumber,

        @Schema(description = "Данные рейса")
        FlightDto flight,

        @Schema(description = "Данные пассажира")
        PassengerDto passenger,

        @Schema(description = "Вес регистрируемого багажа (кг)", example = "23.0")
        Double baggageWeight,

        @Schema(description = "Вес ручной клади (кг)", example = "10.0")
        Double carryOnBaggageWeight) {
}
