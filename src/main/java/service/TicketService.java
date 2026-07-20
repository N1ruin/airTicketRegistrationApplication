package service;

import domain.Flight;
import domain.Ticket;
import domain.TicketStatus;
import exception.ApplicationException;
import repository.impl.TicketRepository;
import util.CurrentUserHolder;
import util.TransactionHelper;

import java.time.ZonedDateTime;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;

public class TicketService {
    private final TransactionHelper transactionHelper;
    private final TicketRepository ticketRepository;
    private final PassengerService passengerService;
    private final AirportService airportService;
    private final FlightService flightService;

    public TicketService(TransactionHelper transactionHelper, TicketRepository ticketRepository,
                         PassengerService passengerService, AirportService airportService,
                         FlightService flightService) {
        this.transactionHelper = transactionHelper;
        this.ticketRepository = ticketRepository;
        this.passengerService = passengerService;
        this.airportService = airportService;
        this.flightService = flightService;
    }

    public Ticket create(Ticket ticket) {
        return transactionHelper.executeInTransaction(() -> {
            var flightId = ticket.getFlightId();
            var passengerId = ticket.getPassengerId();
            var currentUserId = CurrentUserHolder.getCurrentUserId();

            passengerService.findByIdAndUserId(passengerId, currentUserId);

            checkActiveTicket(flightId, passengerId);

            var flight = flightService.findById(flightId);

            checkHasFreeSeats(flight);
            checkSeatAvailable(flightId, ticket.getSeatNumber());

            flight.setFreeSeats(flight.getFreeSeats() - 1);
            flightService.update(flight);
            ticket.setTicketStatus(TicketStatus.PENDING);

            passengerService.addFavoriteAirport(passengerId, flight.getDepartureAirportId());
            ticket.setPurchaseDate(ZonedDateTime.now());

            return ticketRepository.create(ticket);
        });
    }

    public List<Ticket> findAll() {
        var isAdmin = CurrentUserHolder.isAdmin();

        return isAdmin
                ? ticketRepository.findAll()
                : ticketRepository.findAllByUserId(CurrentUserHolder.getCurrentUserId());
    }

    public void refundTicket(Long id) {
        transactionHelper.executeInTransaction(() -> {
            var currentUserId = CurrentUserHolder.getCurrentUserId();
            var isAdmin = CurrentUserHolder.isAdmin();

            Ticket ticket;
            if (isAdmin) {
                ticket = ticketRepository.findById(id)
                        .orElseThrow(() -> new ApplicationException("Ticket not found. ID: %d"
                                .formatted(id), SC_NOT_FOUND));
            } else {
                ticket = ticketRepository.findByIdAndCurrentUserId(id, currentUserId)
                        .orElseThrow(() -> new ApplicationException("Ticket not found or access denied. ID: %d"
                                .formatted(id), SC_FORBIDDEN));

                passengerService.findByIdAndUserId(ticket.getPassengerId(), currentUserId);
            }

            processRefund(ticket);
        });
    }

    public List<Ticket> findAllActualByUserId() {
        return ticketRepository.findAllActualByUserId(CurrentUserHolder.getCurrentUserId());
    }

    private void checkActiveTicket(Long flightId, Long passengerId) {
        ticketRepository.findByFlightIdAndPassengerId(flightId, passengerId)
                .filter(existing -> existing.getTicketStatus() != TicketStatus.REFUNDED)
                .ifPresent(existing -> {
                    throw new ApplicationException("Passenger already has an active ticket for this flight", SC_CONFLICT);
                });
    }

    private void checkSeatAvailable(Long flightId, Integer seatNumber) {
        ticketRepository.findByFlightIdAndSeatNumber(flightId, seatNumber)
                .ifPresent(existing -> {
                    throw new ApplicationException("Seat is already taken. Number: %d"
                            .formatted(seatNumber), SC_CONFLICT);
                });
    }

    private void checkHasFreeSeats(Flight flight) {
        var freeSeats = flight.getFreeSeats();

        if (freeSeats <= 0) {
            throw new ApplicationException("No free seats available for this flight", SC_CONFLICT);
        }
    }

    private void processRefund(Ticket ticket) {
        ticket.setTicketStatus(TicketStatus.REFUNDED);
        ticketRepository.update(ticket);

        var passengerId = ticket.getPassengerId();

        var flight = flightService.findById(ticket.getFlightId());

        var airport = airportService.findById(flight.getDepartureAirportId());
        passengerService.removeFavoriteAirport(passengerId, airport.getId());

        flight.setFreeSeats(flight.getFreeSeats() + 1);
        flightService.update(flight);
    }

}
