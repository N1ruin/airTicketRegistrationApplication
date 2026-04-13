package dto.flight;

import dto.airport.AirportDto;

import java.time.LocalDateTime;

public record CreateFlightRequest(
        Integer allSeats,
        Integer freeSeats,
        AirportDto departureAirportDto,
        AirportDto arrivalAirportDto,
        LocalDateTime departureDate,
        LocalDateTime arrivalDate) {
}
