package converter.flight;

import domain.Flight;
import dto.flight.CreateFlightRequest;
import converter.Converter;
import converter.airport.AirportDtoConverter;

public class CreateFlightRequestConverter implements Converter<CreateFlightRequest, Flight> {
    private final AirportDtoConverter airportDtoConverter;

    public CreateFlightRequestConverter(AirportDtoConverter airportDtoConverter) {
        this.airportDtoConverter = airportDtoConverter;
    }

    @Override
    public Flight convert(CreateFlightRequest request) {
        var flight = new Flight();
        flight.setAllSeats(request.allSeats());
        flight.setFreeSeats(request.freeSeats());

        var departureAirport = airportDtoConverter.convert(request.departureAirportDto());
        flight.setDepartureAirport(departureAirport);

        var arrivarAirport = airportDtoConverter.convert(request.arrivalAirportDto());
        flight.setArrivalAirport(arrivarAirport);

        flight.setDepartureDate(request.departureDate());
        flight.setArrivalDate(request.arrivalDate());
        flight.setDepartureDate(request.departureDate());
        flight.setArrivalDate(request.arrivalDate());

        return flight;
    }
}
