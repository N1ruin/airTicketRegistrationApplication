package unit.service;

import domain.*;
import exception.EntityAlreadyExistException;
import exception.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.TicketRepository;
import service.AirportService;
import service.FlightService;
import service.PassengerService;
import service.TicketService;
import util.CurrentUserHolder;
import util.TransactionHelper;

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
        CurrentUserHolder.setCurrentUserId(1L);
        CurrentUserHolder.setCurrentUserRole(Role.USER);

        lenient().when(transactionHelper.executeInTransaction(any(Supplier.class)))
                .thenAnswer(invocation -> ((Supplier<?>) invocation.getArgument(0)).get());

        lenient().doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(transactionHelper).executeInTransaction(any(Runnable.class));
    }

    @AfterEach
    void clearUserContext() {
        CurrentUserHolder.clear();
    }

    @Test
    void createSuccess() {
        var passenger = new Passenger();
        passenger.setId(10L);
        passenger.setUserId(1L);
        var flight = new Flight();
        flight.setId(20L);
        flight.setFreeSeats(50);
        flight.setDepartureAirportCode("MSQ");
        var ticket = new Ticket();
        ticket.setPassengerId(10L);
        ticket.setFlightId(20L);
        ticket.setSeatNumber(5);
        when(passengerService.findByIdAndUserId(10L, 1L)).thenReturn(passenger);
        when(flightService.findById(20L)).thenReturn(flight);
        when(ticketRepository.findByFlightIdAndPassengerId(20L, 10L)).thenReturn(Optional.empty());
        when(ticketRepository.findByFlightIdAndSeatNumber(20L, 5)).thenReturn(Optional.empty());
        when(ticketRepository.create(any(Ticket.class))).thenAnswer(i -> {
            Ticket t = i.getArgument(0);
            t.setId(100L);
            return t;
        });

        var result = ticketService.create(ticket);

        assertNotNull(result.getId());
        assertEquals(TicketStatus.PENDING, result.getTicketStatus());
        assertEquals(49, flight.getFreeSeats());
        verify(flightService).update(flight);
        verify(passengerService).addFavoriteAirport(10L, "MSQ");
        verify(ticketRepository).create(ticket);
    }

    @Test
    void createThrowsExceptionIfSeatAlreadyTaken() {
        var passenger = new Passenger();
        passenger.setId(10L);
        passenger.setUserId(1L);
        var flight = new Flight();
        flight.setId(20L);
        flight.setFreeSeats(10);
        var ticket = new Ticket();
        ticket.setPassengerId(10L);
        ticket.setFlightId(20L);
        ticket.setSeatNumber(5);
        when(passengerService.findByIdAndUserId(10L, 1L)).thenReturn(passenger);
        when(flightService.findById(20L)).thenReturn(flight);
        when(ticketRepository.findByFlightIdAndPassengerId(20L, 10L)).thenReturn(Optional.empty());
        when(ticketRepository.findByFlightIdAndSeatNumber(20L, 5)).thenReturn(Optional.of(new Ticket()));


        assertThrows(EntityAlreadyExistException.class, () -> ticketService.create(ticket));

        verify(ticketRepository, never()).create(any());
        verify(flightService, never()).update(any());
    }

    @Test
    void createThrowsValidationExceptionWhenPassengerNotBelongsToUser() {
        var ticket = new Ticket();
        ticket.setPassengerId(999L);
        ticket.setFlightId(20L);
        when(passengerService.findByIdAndUserId(999L, 1L))
                .thenThrow(new EntityNotFoundException("Passenger not found"));

        assertThrows(EntityNotFoundException.class, () -> ticketService.create(ticket));
    }

    @Test
    void createSuccessWhenOnlyOneSeatLeft() {
        var passenger = new Passenger();
        passenger.setId(10L);
        passenger.setUserId(1L);
        var flight = new Flight();
        flight.setId(20L);
        flight.setFreeSeats(1);
        flight.setDepartureAirportCode("MSQ");
        var ticket = new Ticket();
        ticket.setPassengerId(10L);
        ticket.setFlightId(20L);
        ticket.setSeatNumber(99);
        when(passengerService.findByIdAndUserId(10L, 1L)).thenReturn(passenger);
        when(flightService.findById(20L)).thenReturn(flight);
        when(ticketRepository.findByFlightIdAndPassengerId(any(), any())).thenReturn(Optional.empty());
        when(ticketRepository.findByFlightIdAndSeatNumber(any(), any())).thenReturn(Optional.empty());
        when(ticketRepository.create(any())).thenReturn(ticket);

        ticketService.create(ticket);

        assertEquals(0, flight.getFreeSeats());
        verify(flightService).update(flight);
    }

    @Test
    void createSuccessIfPreviousTicketWasRefunded() {
        var passenger = new Passenger();
        passenger.setId(10L);
        passenger.setUserId(1L);
        var flight = new Flight();
        flight.setId(20L);
        flight.setFreeSeats(10);
        flight.setDepartureAirportCode("MSQ");
        var oldTicket = new Ticket();
        oldTicket.setTicketStatus(TicketStatus.REFUNDED);
        var newTicket = new Ticket();
        newTicket.setPassengerId(10L);
        newTicket.setFlightId(20L);
        newTicket.setSeatNumber(50);
        when(passengerService.findByIdAndUserId(10L, 1L)).thenReturn(passenger);
        when(flightService.findById(20L)).thenReturn(flight);
        when(ticketRepository.findByFlightIdAndPassengerId(20L, 10L))
                .thenReturn(Optional.of(oldTicket));
        when(ticketRepository.findByFlightIdAndSeatNumber(any(), any()))
                .thenReturn(Optional.empty());
        when(ticketRepository.create(any())).thenReturn(newTicket);

        var result = ticketService.create(newTicket);

        assertNotNull(result);
        assertEquals(TicketStatus.PENDING, result.getTicketStatus());
        verify(ticketRepository).create(newTicket);
    }

    @Test
    void refundByCurrentUserSuccess() {
        var ticketId = 100L;
        var passenger = new Passenger();
        passenger.setId(10L);
        passenger.setUserId(1L);
        var flight = new Flight();
        flight.setFreeSeats(10);
        flight.setDepartureAirportCode("JFK");
        var ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setPassengerId(10L);
        ticket.setFlightId(20L);
        ticket.setTicketStatus(TicketStatus.PENDING);
        when(ticketRepository.findByIdAndCurrentUserId(ticketId, 1L))
                .thenReturn(Optional.of(ticket));
        when(passengerService.findByIdAndUserId(10L, 1L)).thenReturn(passenger);
        when(flightService.findById(20L)).thenReturn(flight);
        when(airportService.findById("JFK")).thenReturn(new Airport());

        ticketService.refundByCurrentUser(ticketId);

        assertEquals(TicketStatus.REFUNDED, ticket.getTicketStatus());
        assertEquals(11, flight.getFreeSeats());
        verify(ticketRepository).update(ticket);
        verify(flightService).update(flight);
    }

    @Test
    void refundByCurrentUserThrowsValidationExceptionWhenUserMismatch() {
        Long ticketId = 100L;

        when(ticketRepository.findByIdAndCurrentUserId(ticketId, 1L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ticketService.refundByCurrentUser(ticketId));
    }

    @Test
    void refundByCurrentUserThrowsNotFoundException() {
        Long ticketId = 1L;
        when(ticketRepository.findByIdAndCurrentUserId(ticketId, 1L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ticketService.refundByCurrentUser(ticketId));
    }

    @Test
    void findAllByCurrentUserIdSuccess() {
        var ticketOne = new Ticket();
        var ticketTwo = new Ticket();
        when(ticketRepository.findAllByUserId(1L)).thenReturn(List.of(ticketOne, ticketTwo));

        var result = ticketService.findAllByCurrentUserId();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(ticketRepository).findAllByUserId(1L);
    }

    @Test
    void findAllActualByUserIdSuccess() {
        when(ticketRepository.findAllActualByUserId(1L)).thenReturn(List.of(new Ticket()));

        var result = ticketService.findAllActualByUserId();

        assertEquals(1, result.size());
        verify(ticketRepository).findAllActualByUserId(1L);
    }

    @Test
    void findAllSuccess() {
        var ticket1 = new Ticket();
        var ticket2 = new Ticket();
        when(ticketRepository.findAll()).thenReturn(List.of(ticket1, ticket2));

        var result = ticketService.findAll();

        assertEquals(2, result.size());
        verify(ticketRepository).findAll();
    }

    @Test
    void refundByAdminSuccess() {
        var ticketId = 100L;
        var ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setPassengerId(10L);
        ticket.setFlightId(20L);
        ticket.setTicketStatus(TicketStatus.PENDING);

        var flight = new Flight();
        flight.setFreeSeats(10);
        flight.setDepartureAirportCode("JFK");

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(flightService.findById(20L)).thenReturn(flight);
        when(airportService.findById("JFK")).thenReturn(new Airport());

        ticketService.refund(ticketId);

        assertEquals(TicketStatus.REFUNDED, ticket.getTicketStatus());
        assertEquals(11, flight.getFreeSeats());
        verify(ticketRepository).update(ticket);
        verify(flightService).update(flight);
    }

    @Test
    void refundByAdminThrowsNotFoundException() {
        var ticketId = 999L;
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ticketService.refund(ticketId));
    }
}
