package converter.flight;

import domain.Flight;
import dto.flight.CreateFlightRequest;
import converter.Converter;

public class CreateFlightRequestConverter implements Converter<CreateFlightRequest, Flight> {
    @Override
    public Flight convert(CreateFlightRequest request) {
        var flight = new Flight();
        flight.setSeatsCount(request.seatsCount());
        flight.setFreeSeats(request.freeSeats());

        flight.setDepartureAirportId(request.departureAirportId());
        flight.setArrivalAirportId(request.arrivalAirportId());
        flight.setDepartureDate(request.departureDate());
        flight.setArrivalDate(request.arrivalDate());

        return flight;
    }
}
