package converter.ticket;

import converter.Converter;
import domain.Ticket;
import dto.ticket.CreateTicketRequest;

public class CreateTicketRequestConverter implements Converter<CreateTicketRequest, Ticket> {
    @Override
    public Ticket convert(CreateTicketRequest request) {
        var ticket = new Ticket();

        ticket.setTicketRank(request.ticketRank());
        ticket.setSeatNumber(request.seatNumber());
        ticket.setFlightId(request.flightId());
        ticket.setPassengerId(request.passengerId());
        ticket.setBaggageWeight(request.baggageWeight());
        ticket.setCarryOnBaggageWeight(request.carryOnBaggageWeight());

        return ticket;
    }
}
