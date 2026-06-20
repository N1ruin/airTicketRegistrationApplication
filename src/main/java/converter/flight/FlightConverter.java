package converter.flight;

import converter.Converter;
import domain.Flight;
import dto.flight.FlightDto;

public class FlightConverter implements Converter<Flight, FlightDto> {
    @Override
    public FlightDto convert(Flight flight) {
        var id = flight.getId();
        var allSeats = flight.getAllSeats();
        var freeSeats = flight.getFreeSeats();
        var departureAirportCode = flight.getDepartureAirportCode();
        var arrivalAirportCode = flight.getArrivalAirportCode();
        var departureDate = flight.getDepartureDate();
        var arrivalDate = flight.getArrivalDate();

        return new FlightDto(id,
                allSeats,
                freeSeats,
                departureAirportCode,
                arrivalAirportCode,
                departureDate,
                arrivalDate);
    }
}
