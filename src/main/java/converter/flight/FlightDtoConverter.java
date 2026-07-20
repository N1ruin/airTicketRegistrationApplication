package converter.flight;

import converter.Converter;
import domain.Flight;
import dto.flight.FlightDto;

public class FlightDtoConverter implements Converter<FlightDto, Flight> {
    @Override
    public Flight convert(FlightDto flightDto) {
        var flight = new Flight();

        flight.setId(flightDto.id());
        flight.setSeatsCount(flightDto.seatsCount());
        flight.setFreeSeats(flightDto.freeSeats());
        flight.setDepartureAirportId(flightDto.departureAirportId());
        flight.setArrivalAirportId(flightDto.arrivalAirportId());
        flight.setDepartureDate(flightDto.departureDate());
        flight.setArrivalDate(flightDto.arrivalDate());

        return flight;
    }
}
