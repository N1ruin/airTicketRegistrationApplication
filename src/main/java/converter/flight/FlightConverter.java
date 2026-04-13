package converter.flight;

import converter.airport.AirportConverter;
import domain.Flight;
import dto.flight.FlightDto;
import converter.ListConverter;
import converter.Converter;

import java.util.List;

public class FlightConverter implements Converter<Flight, FlightDto>, ListConverter<Flight, FlightDto> {
    private final AirportConverter airportConverter;

    public FlightConverter(AirportConverter airportConverter) {
        this.airportConverter = airportConverter;
    }

    @Override
    public FlightDto convert(Flight flight) {
        var id = flight.getId();
        var allSeats = flight.getAllSeats();
        var freeSeats = flight.getFreeSeats();
        var departureAirportDto = airportConverter.convert(flight.getDepartureAirport());
        var arrivalAirportDto = airportConverter.convert(flight.getArrivalAirport());
        var departureDate = flight.getDepartureDate();
        var arrivalDate = flight.getArrivalDate();

        return new FlightDto(id, allSeats, freeSeats, departureAirportDto, arrivalAirportDto, departureDate, arrivalDate);
    }

    @Override
    public List<FlightDto> convertAll(List<Flight> flights) {
        return flights.stream()
                .map(this::convert)
                .toList();
    }
}
