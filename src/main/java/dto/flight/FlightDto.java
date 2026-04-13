package dto.flight;

import dto.airport.AirportDto;

import java.time.LocalDateTime;

public record FlightDto(
        Long id,
        Integer allSeats,
        Integer freeSeats,
        AirportDto departureAirportDto,
        AirportDto arrivalAirportDto,
        LocalDateTime departureDate,
        LocalDateTime arrivalDate) {
}
