package dto.ticket;

import dto.flight.FlightDto;
import dto.passenger.PassengerDto;

import java.time.LocalDateTime;

public record CreateTicketResponse(Long id,
                                   Integer ticketNumber,
                                   String serviceClass,
                                   Integer seatNumber,
                                   FlightDto flight,
                                   PassengerDto passenger,
                                   LocalDateTime purchaseDate,
                                   Double baggageWeight,
                                   Double carryOnBaggageWeight) {
}
