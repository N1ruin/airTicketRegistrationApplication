package service;

import domain.Flight;
import domain.Ticket;
import domain.TicketStatus;
import exception.EntityAlreadyExistException;
import exception.EntityNotFoundException;
import exception.ValidationException;
import repository.TicketRepository;
import util.CurrentUserHolder;
import util.TransactionHelper;

import java.time.ZonedDateTime;
import java.util.List;

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

            passengerService.addFavoriteAirport(passengerId, flight.getDepartureAirportCode());
            ticket.setPurchaseDate(ZonedDateTime.now());

            return ticketRepository.create(ticket);
        });
    }

    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public List<Ticket> findAllByCurrentUserId() {
        return ticketRepository.findAllByUserId(CurrentUserHolder.getCurrentUserId());
    }

    public void refundByCurrentUser(Long id) {
        transactionHelper.executeInTransaction(() -> {
            var currentUserId = CurrentUserHolder.getCurrentUserId();
            var ticket = ticketRepository.findByIdAndCurrentUserId(id, currentUserId)
                    .orElseThrow(() -> new EntityNotFoundException("Ticket not found. ID: %d".formatted(id)));

            passengerService.findByIdAndUserId(ticket.getPassengerId(), currentUserId);

            processRefund(ticket);
        });
    }

    public void refund(Long id) {
        transactionHelper.executeInTransaction(() -> {
            var ticket = ticketRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Ticket not found. ID: %d".formatted(id)));

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
                    throw new EntityAlreadyExistException("Passenger already has an active ticket for this flight");
                });
    }

    private void checkSeatAvailable(Long flightId, Integer seatNumber) {
        ticketRepository.findByFlightIdAndSeatNumber(flightId, seatNumber)
                .ifPresent(existing -> {
                    throw new EntityAlreadyExistException("Seat is already taken. Number: %d"
                            .formatted(seatNumber));
                });
    }

    private void checkHasFreeSeats(Flight flight) {
        var freeSeats = flight.getFreeSeats();

        if (freeSeats <= 0) {
            throw new ValidationException("No free seats available for this flight");
        }
    }

    private void processRefund(Ticket ticket) {
        ticket.setTicketStatus(TicketStatus.REFUNDED);
        ticketRepository.update(ticket);

        var passengerId = ticket.getPassengerId();

        var flight = flightService.findById(ticket.getFlightId());

        var airport = airportService.findById(flight.getDepartureAirportCode());
        passengerService.removeFavoriteAirport(passengerId, airport.getCode());

        flight.setFreeSeats(flight.getFreeSeats() + 1);
        flightService.update(flight);
    }

}
