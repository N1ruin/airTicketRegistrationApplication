package dto.ticket;


import domain.ServiceClass;
import domain.TicketStatus;
import dto.flight.FlightDto;
import dto.passenger.PassengerDto;

import java.time.LocalDateTime;

public record TicketDto(TicketStatus ticketStatus,
                        Long ticketNumber,
                        ServiceClass serviceClass,
                        Integer seatNumber,
                        FlightDto flight,
                        PassengerDto passenger,
                        LocalDateTime purchaseDate,
                        Double baggageWeight,
                        Double carryOnBaggageWeight) {
}
