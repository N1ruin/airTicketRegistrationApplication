package converter.ticket;

import domain.Ticket;
import dto.ticket.TicketDto;
import converter.Converter;

public class TicketDtoConverter implements Converter<Ticket, TicketDto> {
    @Override
    public TicketDto convert(Ticket ticket) {
        var ticketId = ticket.getId();
        var ticketStatus = ticket.getTicketStatus();
        var ticketNumber = ticket.getTicketNumber();
        var ticketRank = ticket.getTicketRank();
        var seatNumber = ticket.getSeatNumber();
        var flightId = ticket.getFlightId();
        var passengerId = ticket.getPassengerId();
        var purchaseDate = ticket.getPurchaseDate();
        var baggageWeight = ticket.getBaggageWeight();
        var carryOnBaggageWeight = ticket.getCarryOnBaggageWeight();

        return new TicketDto(ticketId,
                ticketStatus,
                ticketNumber,
                ticketRank,
                seatNumber,
                flightId,
                passengerId,
                purchaseDate,
                baggageWeight,
                carryOnBaggageWeight);
    }
}
