package unit.converter.ticket;

import converter.flight.FlightConverter;
import converter.passenger.PassengerDtoConverter;
import converter.ticket.TicketDtoConverter;
import domain.*;
import dto.flight.FlightDto;
import dto.passenger.PassengerDto;
import dto.ticket.TicketDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketDtoConverterTest {
    @Mock
    private FlightConverter flightConverter;
    @Mock
    private PassengerDtoConverter passengerDtoConverter;
    @InjectMocks
    @Spy
    private TicketDtoConverter ticketDtoConverter;

    @Test
    void convertTicketToTicketDtoSuccess() {
        var purchaseDate = LocalDateTime.now();
        var flightMock = mock(Flight.class);
        var flightDtoMock = mock(FlightDto.class);
        var passengerMock = mock(Passenger.class);
        var passengerDtoMock = mock(PassengerDto.class);
        var ticket = new Ticket();
        ticket.setTicketNumber(1L);
        ticket.setTicketStatus(TicketStatus.ACTIVE);
        ticket.setServiceClass(ServiceClass.STANDARD);
        ticket.setSeatNumber(12);
        ticket.setBaggageWeight(10.0);
        ticket.setCarryOnBaggageWeight(5.0);
        ticket.setPurchaseDate(purchaseDate);
        ticket.setFlight(flightMock);
        ticket.setPassenger(passengerMock);
        when(passengerDtoConverter.convert(passengerMock)).thenReturn(passengerDtoMock);
        when(flightConverter.convert(flightMock)).thenReturn(flightDtoMock);

        var result = ticketDtoConverter.convert(ticket);

        assertNotNull(result);
        assertEquals(TicketStatus.ACTIVE, result.ticketStatus());
        assertEquals(ServiceClass.STANDARD, result.serviceClass());
        assertEquals(flightDtoMock, result.flight());
        assertEquals(passengerDtoMock, result.passenger());
        assertEquals(1, result.ticketNumber());
        assertEquals(12, result.seatNumber());
        assertEquals(10.0, result.baggageWeight());
        assertEquals(5.0, result.carryOnBaggageWeight());
        assertEquals(purchaseDate, result.purchaseDate());
        verify(flightConverter).convert(flightMock);
        verify(passengerDtoConverter).convert(passengerMock);
    }

    @Test
    void convertTicketListToTicketDtoList() {
        var ticketOne = mock(Ticket.class);
        var ticketTwo = mock(Ticket.class);
        var ticketDtoOne = mock(TicketDto.class);
        var ticketDtoTwo = mock(TicketDto.class);
        doReturn(ticketDtoOne).when(ticketDtoConverter).convert(ticketOne);
        doReturn(ticketDtoTwo).when(ticketDtoConverter).convert(ticketTwo);

        var result = ticketDtoConverter.convertAll(List.of(ticketOne, ticketTwo));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(ticketDtoOne, result.get(0));
        assertEquals(ticketDtoTwo, result.get(1));
        verify(ticketDtoConverter, times(2)).convert(any());
    }
}
