package unit.converter.ticket;

import converter.ticket.TicketDtoConverter;
import domain.*;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TicketDtoConverterTest {
    private final TicketDtoConverter ticketDtoConverter = new TicketDtoConverter();

    @Test
    void convertTicketToTicketDtoSuccess() {
        var purchaseDate = ZonedDateTime.now();
        var ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketNumber(123L);
        ticket.setTicketStatus(TicketStatus.CONFIRMED);
        ticket.setTicketRank(TicketRank.STANDARD);
        ticket.setSeatNumber(12);
        ticket.setFlightId(1L);
        ticket.setPassengerId(2L);
        ticket.setBaggageWeight(10.0);
        ticket.setCarryOnBaggageWeight(5.0);
        ticket.setPurchaseDate(purchaseDate);

        var result = ticketDtoConverter.convert(ticket);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(TicketStatus.CONFIRMED, result.ticketStatus());
        assertEquals(123L, result.ticketNumber());
        assertEquals(TicketRank.STANDARD, result.ticketRank());
        assertEquals(12, result.seatNumber());
        assertEquals(1L, result.flightId());
        assertEquals(2L, result.passengerId());
        assertEquals(10.0, result.baggageWeight());
        assertEquals(5.0, result.carryOnBaggageWeight());
        assertEquals(purchaseDate, result.purchaseDate());
    }

    @Test
    void convertTicketListToTicketDtoList() {
        var ticketOne = new Ticket();
        ticketOne.setId(1L);
        ticketOne.setTicketNumber(100L);
        ticketOne.setFlightId(10L);
        ticketOne.setPassengerId(20L);
        ticketOne.setTicketRank(TicketRank.STANDARD);
        ticketOne.setTicketStatus(TicketStatus.CONFIRMED);
        var ticketTwo = new Ticket();
        ticketTwo.setId(2L);
        ticketTwo.setTicketNumber(200L);
        ticketTwo.setFlightId(30L);
        ticketTwo.setPassengerId(40L);
        ticketTwo.setTicketRank(TicketRank.BUSINESS);
        ticketTwo.setTicketStatus(TicketStatus.PENDING);

        var result = ticketDtoConverter.convertAll(List.of(ticketOne, ticketTwo));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.getFirst().id());
        assertEquals(100L, result.getFirst().ticketNumber());
        assertEquals(10L, result.getFirst().flightId());
        assertEquals(20L, result.getFirst().passengerId());
        assertEquals(TicketRank.STANDARD, result.get(0).ticketRank());
        assertEquals(TicketStatus.CONFIRMED, result.get(0).ticketStatus());
        assertEquals(2L, result.get(1).id());
        assertEquals(200L, result.get(1).ticketNumber());
        assertEquals(30L, result.get(1).flightId());
        assertEquals(40L, result.get(1).passengerId());
        assertEquals(TicketRank.BUSINESS, result.get(1).ticketRank());
        assertEquals(TicketStatus.PENDING, result.get(1).ticketStatus());
    }
}
