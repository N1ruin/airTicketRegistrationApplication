package converter.flight;

import converter.Converter;
import domain.Flight;
import dto.flight.FlightDto;

public class FlightDtoConverter implements Converter<FlightDto, Flight> {
    @Override
    public Flight convert(FlightDto flightDto) {
        var flight = new Flight();

        flight.setId(flightDto.id());
        flight.setAllSeats(flightDto.allSeats());
        flight.setFreeSeats(flightDto.freeSeats());
        flight.setDepartureAirportCode(flightDto.departureAirportCode());
        flight.setArrivalAirportCode(flightDto.arrivalAirportCode());
        flight.setDepartureDate(flightDto.departureDate());
        flight.setArrivalDate(flightDto.arrivalDate());

        return flight;
    }
}
