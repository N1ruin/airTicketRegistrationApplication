package converter.flight;

import converter.airport.AirportConverter;
import domain.Flight;
import dto.flight.CreateFlightResponse;
import converter.Converter;

public class CreateFlightResponseConverter implements Converter<Flight, CreateFlightResponse> {
    private final AirportConverter airportConverter;

    public CreateFlightResponseConverter(AirportConverter airportConverter) {
        this.airportConverter = airportConverter;
    }

    @Override
    public CreateFlightResponse convert(Flight flight) {
        var id = flight.getId();
        var allSeats = flight.getAllSeats();
        var freeSeats = flight.getFreeSeats();
        var departureAirportDto = airportConverter.convert(flight.getDepartureAirport());
        var arrivalAirportDto = airportConverter.convert(flight.getArrivalAirport());
        var departureDate = flight.getDepartureDate();
        var arrivalDate = flight.getArrivalDate();

        return new CreateFlightResponse(id, allSeats, freeSeats, departureAirportDto, arrivalAirportDto, departureDate,
                arrivalDate);
    }
}
