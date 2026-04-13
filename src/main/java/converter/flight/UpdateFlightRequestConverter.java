package converter.flight;

import domain.Flight;
import dto.flight.UpdateFlightRequest;
import converter.Converter;

public class UpdateFlightRequestConverter implements Converter<UpdateFlightRequest, Flight> {
    @Override
    public Flight convert(UpdateFlightRequest request) {
        var flight = new Flight();
        flight.setId(request.id());
        flight.setAllSeats(request.allSeats());
        flight.setFreeSeats(request.freeSeats());
        flight.setDepartureDate(request.departureDate());
        flight.setArrivalDate(request.arrivalDate());

        return flight;
    }
}
