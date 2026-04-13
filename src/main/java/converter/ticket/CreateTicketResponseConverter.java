package converter.ticket;

import domain.Ticket;
import dto.ticket.CreateTicketResponse;
import converter.Converter;
import converter.flight.FlightConverter;
import converter.passenger.PassengerDtoConverter;

public class CreateTicketResponseConverter implements Converter<Ticket, CreateTicketResponse> {
    private final FlightConverter flightConverter;
    private final PassengerDtoConverter passengerDtoConverter;

    public CreateTicketResponseConverter(FlightConverter flightConverter, PassengerDtoConverter passengerDtoConverter) {
        this.flightConverter = flightConverter;
        this.passengerDtoConverter = passengerDtoConverter;
    }

    @Override
    public CreateTicketResponse convert(Ticket ticket) {
        var id = ticket.getId();
        var ticketNumber = ticket.getTicketNumber();
        var serviceClass = ticket.getServiceClass().name();
        var seatNumber = ticket.getSeatNumber();
        var flightDto = flightConverter.convert(ticket.getFlight());
        var passengerDto = passengerDtoConverter.convert(ticket.getPassenger());
        var purchaseDate = ticket.getPurchaseDate();
        var baggageWeight = ticket.getBaggageWeight();
        var carryOnBaggageWeight = ticket.getCarryOnBaggageWeight();

        return new CreateTicketResponse(id, ticketNumber, serviceClass, seatNumber, flightDto, passengerDto, purchaseDate,
                baggageWeight, carryOnBaggageWeight);
    }
}
