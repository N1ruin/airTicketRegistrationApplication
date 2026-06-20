package converter.flight;

import domain.Flight;
import dto.flight.CreateFlightRequest;
import converter.Converter;

public class CreateFlightRequestConverter implements Converter<CreateFlightRequest, Flight> {
    @Override
    public Flight convert(CreateFlightRequest request) {
        var flight = new Flight();
        flight.setAllSeats(request.allSeats());
        flight.setFreeSeats(request.freeSeats());

        flight.setDepartureAirportCode(request.departureAirportCode());
        flight.setArrivalAirportCode(request.arrivalAirportCode());
        flight.setDepartureDate(request.departureDate());
        flight.setArrivalDate(request.arrivalDate());

        return flight;
    }
}
