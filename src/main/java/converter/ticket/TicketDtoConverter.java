package converter.ticket;

import domain.Ticket;
import dto.ticket.TicketDto;
import converter.ListConverter;
import converter.Converter;
import converter.flight.FlightConverter;
import converter.passenger.PassengerDtoConverter;

import java.util.List;

public class TicketDtoConverter implements Converter<Ticket, TicketDto>, ListConverter<Ticket, TicketDto> {
    private final FlightConverter flightConverter;
    private final PassengerDtoConverter passengerDtoConverter;

    public TicketDtoConverter(FlightConverter flightConverter, PassengerDtoConverter passengerDtoConverter) {
        this.flightConverter = flightConverter;
        this.passengerDtoConverter = passengerDtoConverter;
    }

    @Override
    public TicketDto convert(Ticket ticket) {
        var ticketNumber = ticket.getTicketNumber();
        var serviceClass = ticket.getServiceClass().name();
        var seatNumber = ticket.getSeatNumber();
        var flightDto = flightConverter.convert(ticket.getFlight());
        var passengerDto = passengerDtoConverter.convert(ticket.getPassenger());
        var purchaseDate = ticket.getPurchaseDate();
        var baggageWeight = ticket.getBaggageWeight();
        var carryOnBaggageWeight = ticket.getCarryOnBaggageWeight();

        return new TicketDto(ticketNumber, serviceClass, seatNumber, flightDto, passengerDto, purchaseDate,
                baggageWeight, carryOnBaggageWeight);
    }

    @Override
    public List<TicketDto> convertAll(List<Ticket> tickets) {
        return tickets.stream()
                .map(this::convert)
                .toList();
    }
}
