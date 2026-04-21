package converter.flight;

import converter.airport.AirportDtoConverter;
import domain.Flight;
import dto.flight.UpdateFlightRequest;
import converter.Converter;

public class UpdateFlightRequestConverter implements Converter<UpdateFlightRequest, Flight> {
    private final AirportDtoConverter airportDtoConverter;

    public UpdateFlightRequestConverter(AirportDtoConverter airportDtoConverter) {
        this.airportDtoConverter = airportDtoConverter;
    }

    @Override
    public Flight convert(UpdateFlightRequest request) {
        var flight = new Flight();
        flight.setId(request.id());
        flight.setAllSeats(request.allSeats());
        flight.setFreeSeats(request.freeSeats());
        flight.setDepartureAirport(airportDtoConverter.convert(request.departureAirportDto()));
        flight.setArrivalAirport(airportDtoConverter.convert(request.arrivalAirportDto()));
        flight.setDepartureDate(request.departureDate());
        flight.setArrivalDate(request.arrivalDate());

        return flight;
    }
}
