package unit.service;

import domain.*;
import exception.EntityAlreadyExistException;
import exception.EntityNotFoundException;
import exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.TicketRepository;
import repository.TransactionHelper;
import service.AirportService;
import service.FlightService;
import service.PassengerService;
import service.TicketService;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private PassengerService passengerService;
    @Mock
    private AirportService airportService;
    @Mock
    private FlightService flightService;
    @Mock
    private TransactionHelper transactionHelper;
    @InjectMocks
    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        lenient().when(transactionHelper.executeInTransaction(any(Supplier.class)))
                .thenAnswer(invocation -> ((Supplier<?>) invocation.getArgument(0)).get());

        lenient().doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(transactionHelper).executeInTransaction(any(Runnable.class));
    }

    @Test
    void saveSuccess() {
        Long userId = 1L;
        var passenger = new Passenger();
        passenger.setId(10L);
        passenger.setUserId(userId);

        var flight = new Flight();
        flight.setId(20L);
        flight.setFreeSeats(50);
        var airport = new Airport();
        airport.setCode("MSQ");
        flight.setDepartureAirport(airport);
        var ticket = new Ticket();
        ticket.setPassenger(passenger);
        ticket.setFlight(flight);
        ticket.setSeatNumber(5);
        when(passengerService.findById(10L)).thenReturn(passenger);
        when(ticketRepository.findByFlightIdAndPassengerId(20L, 10L)).thenReturn(Optional.empty());
        when(ticketRepository.findByFlightIdAndSeatNumber(20L, 5)).thenReturn(Optional.empty());
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> {
            Ticket t = i.getArgument(0);
            t.setId(100L);
            return t;
        });

        var result = ticketService.save(ticket, userId);

        assertNotNull(result.getId());
        assertEquals(TicketStatus.ACTIVE, result.getTicketStatus());
        assertEquals(49, flight.getFreeSeats());
        verify(flightService).update(flight);
        verify(passengerService).updateFavoriteAirports(10L, "MSQ");
        verify(ticketRepository).save(ticket);
    }

    @Test
    void saveThrowsExceptionIfSeatAlreadyTaken() {
        Long userId = 1L;
        var passenger = new Passenger();
        passenger.setId(10L);
        passenger.setUserId(userId);
        var flight = new Flight();
        flight.setId(20L);
        flight.setFreeSeats(10);
        var ticket = new Ticket();
        ticket.setPassenger(passenger);
        ticket.setFlight(flight);
        ticket.setSeatNumber(5);
        when(passengerService.findById(10L)).thenReturn(passenger);
        when(ticketRepository.findByFlightIdAndPassengerId(any(), any())).thenReturn(Optional.empty());
        when(ticketRepository.findByFlightIdAndSeatNumber(20L, 5)).thenReturn(Optional.of(new Ticket()));

        assertThrows(EntityAlreadyExistException.class, () -> ticketService.save(ticket, userId));

        verify(ticketRepository, never()).save(any());
        verify(flightService, never()).update(any());
    }

    @Test
    void saveThrowsValidationExceptionWhenPassengerNotBelongsToUser() {
        Long currentUserId = 1L;
        Long otherUserId = 2L;
        var passenger = new Passenger();
        passenger.setId(10L);
        passenger.setUserId(otherUserId);
        var flight = new Flight();
        flight.setId(20L);
        var ticket = new Ticket();
        ticket.setPassenger(passenger);
        ticket.setFlight(flight);

        when(passengerService.findById(10L)).thenReturn(passenger);

        assertThrows(ValidationException.class, () -> ticketService.save(ticket, currentUserId));
    }

    @Test
    void saveSuccessWhenOnlyOneSeatLeft() {
        Long userId = 1L;
        var passenger = new Passenger();
        passenger.setId(10L);
        passenger.setUserId(userId);
        var flight = new Flight();
        flight.setId(20L);
        flight.setFreeSeats(1);
        flight.setDepartureAirport(new Airport());
        var ticket = new Ticket();
        ticket.setPassenger(passenger);
        ticket.setFlight(flight);
        ticket.setSeatNumber(99);

        when(passengerService.findById(10L)).thenReturn(passenger);
        when(ticketRepository.findByFlightIdAndPassengerId(any(), any())).thenReturn(Optional.empty());
        when(ticketRepository.findByFlightIdAndSeatNumber(any(), any())).thenReturn(Optional.empty());
        when(ticketRepository.save(any())).thenReturn(ticket);

        ticketService.save(ticket, userId);

        assertEquals(0, flight.getFreeSeats());
        verify(flightService).update(flight);
    }

    @Test
    void saveSuccessIfPreviousTicketWasRefunded() {
        Long userId = 1L;
        var passenger = new Passenger();
        passenger.setId(10L);
        passenger.setUserId(userId);
        var flight = new Flight();
        flight.setId(20L);
        flight.setFreeSeats(10);
        flight.setDepartureAirport(new Airport());
        var oldTicket = new Ticket();
        oldTicket.setTicketStatus(TicketStatus.REFUNDED);
        var newTicket = new Ticket();
        newTicket.setPassenger(passenger);
        newTicket.setFlight(flight);
        newTicket.setSeatNumber(50);

        when(passengerService.findById(10L)).thenReturn(passenger);
        when(ticketRepository.findByFlightIdAndPassengerId(20L, 10L))
                .thenReturn(Optional.of(oldTicket));
        when(ticketRepository.findByFlightIdAndSeatNumber(any(), any()))
                .thenReturn(Optional.empty());
        when(ticketRepository.save(any())).thenReturn(newTicket);

        var result = ticketService.save(newTicket, userId);

        assertNotNull(result);
        assertEquals(TicketStatus.ACTIVE, result.getTicketStatus());
        verify(ticketRepository).save(newTicket);
    }

    @Test
    void refundSuccess() {
        Long userId = 1L;
        Long ticketId = 100L;

        var passenger = new Passenger();
        passenger.setId(10L);
        passenger.setUserId(userId);

        var flight = new Flight();
        flight.setFreeSeats(10);
        var airport = new Airport();
        airport.setCode("JFK");
        flight.setDepartureAirport(airport);
        var ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setPassenger(passenger);
        ticket.setFlight(flight);
        ticket.setTicketStatus(TicketStatus.ACTIVE);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(airportService.findByCode("JFK")).thenReturn(new Airport());

        ticketService.refund(ticketId, userId);

        assertEquals(TicketStatus.REFUNDED, ticket.getTicketStatus());
        assertEquals(11, flight.getFreeSeats());
        verify(ticketRepository).deleteById(ticketId);
        verify(flightService).update(flight);
    }

    @Test
    void refundThrowsValidationExceptionWhenUserMismatch() {
        Long currentUserId = 1L;
        var passenger = new Passenger();
        passenger.setUserId(99L);
        var ticket = new Ticket();
        ticket.setPassenger(passenger);

        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(ticket));

        assertThrows(ValidationException.class, () -> ticketService.refund(100L, currentUserId));
    }

    @Test
    void refundThrowsNotFoundException() {
        Long ticketId = 1L;
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ticketService.refund(ticketId));

        verify(ticketRepository, never()).deleteById(anyLong());
        verify(flightService, never()).update(any());
    }

    @Test
    void findAllByUserIdSuccess() {
        Long userId = 1L;
        var ticketOne = new Ticket();
        var ticketTwo = new Ticket();
        when(ticketRepository.findAllByUserId(userId)).thenReturn(List.of(ticketOne, ticketTwo));

        var result = ticketService.findAllByUserId(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(ticketRepository).findAllByUserId(userId);
    }

    @Test
    void findAllActualByUserIdSuccess() {
        Long userId = 1L;
        when(ticketRepository.findAllActualByUserId(userId)).thenReturn(List.of(new Ticket()));

        var result = ticketService.findAllActualByUserId(userId);

        assertEquals(1, result.size());
        verify(ticketRepository).findAllActualByUserId(userId);
    }
}
