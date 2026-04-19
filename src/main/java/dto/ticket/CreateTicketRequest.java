package dto.ticket;

import dto.flight.FlightDto;
import dto.passenger.PassengerDto;

import java.time.LocalDateTime;

public record CreateTicketRequest(String serviceClass,
                                  Integer seatNumber,
                                  FlightDto flight,
                                  PassengerDto passenger,
                                  Double baggageWeight,
                                  Double carryOnBaggageWeight) {
}
