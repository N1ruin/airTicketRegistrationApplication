package converter.ticket;

import domain.ServiceClass;
import domain.Ticket;
import dto.ticket.CreateTicketRequest;
import converter.Converter;
import converter.flight.FlightDtoConverter;
import converter.passenger.PassengerConverter;

public class CreateTicketRequestConverter implements Converter<CreateTicketRequest, Ticket> {
    private final FlightDtoConverter flightDtoConverter;
    private final PassengerConverter passengerConverter;

    public CreateTicketRequestConverter(FlightDtoConverter flightDtoConverter, PassengerConverter passengerConverter) {
        this.flightDtoConverter = flightDtoConverter;
        this.passengerConverter = passengerConverter;
    }

    @Override
    public Ticket convert(CreateTicketRequest request) {
        var ticket = new Ticket();

        ticket.setServiceClass(ServiceClass.valueOf(request.serviceClass()));
        ticket.setSeatNumber(request.seatNumber());

        var flight = flightDtoConverter.convert(request.flight());
        ticket.setFlight(flight);

        var passenger = passengerConverter.convert(request.passenger());
        ticket.setPassenger(passenger);

        ticket.setBaggageWeight(request.baggageWeight());
        ticket.setCarryOnBaggageWeight(request.carryOnBaggageWeight());

        return ticket;
    }
}
