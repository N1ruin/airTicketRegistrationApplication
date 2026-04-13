package converter.flight;

import converter.airport.AirportConverter;
import domain.Flight;
import dto.flight.UpdateFlightResponse;
import converter.Converter;

public class UpdateFlightResponseConverter implements Converter<Flight, UpdateFlightResponse> {
    private final AirportConverter airportConverter;

    public UpdateFlightResponseConverter(AirportConverter airportConverter) {
        this.airportConverter = airportConverter;
    }

    @Override
    public UpdateFlightResponse convert(Flight flight) {
        var id = flight.getId();
        var allSeats = flight.getAllSeats();
        var freeSeats = flight.getFreeSeats();
        var departureAirportDto = airportConverter.convert(flight.getDepartureAirport());
        var arrivalAirportDto = airportConverter.convert(flight.getArrivalAirport());
        var departureDate = flight.getDepartureDate();
        var arrivalDate = flight.getArrivalDate();

        return new UpdateFlightResponse(id, allSeats, freeSeats, departureAirportDto, arrivalAirportDto, departureDate,
                arrivalDate);
    }
}
