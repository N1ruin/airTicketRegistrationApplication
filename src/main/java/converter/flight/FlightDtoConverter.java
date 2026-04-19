package converter.flight;

import domain.Flight;
import dto.flight.FlightDto;
import converter.Converter;
import converter.airport.AirportDtoConverter;

public class FlightDtoConverter implements Converter<FlightDto, Flight> {
    private final AirportDtoConverter airportDtoConverter;

    public FlightDtoConverter(AirportDtoConverter airportDtoConverter) {
        this.airportDtoConverter = airportDtoConverter;
    }

    @Override
    public Flight convert(FlightDto flightDto) {
        var flight = new Flight();

        flight.setId(flightDto.id());
        flight.setAllSeats(flightDto.allSeats());
        flight.setFreeSeats(flightDto.freeSeats());
        var departureAirport = airportDtoConverter.convert(flightDto.departureAirportDto());
        flight.setDepartureAirport(departureAirport);
        var arrivalAirport = airportDtoConverter.convert(flightDto.arrivalAirportDto());
        flight.setArrivalAirport(arrivalAirport);
        flight.setDepartureDate(flightDto.departureDate());
        flight.setArrivalDate(flightDto.arrivalDate());

        return flight;
    }
}
