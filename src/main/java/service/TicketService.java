package service;

import domain.Ticket;
import domain.TicketStatus;
import exception.EntityAlreadyExistException;
import exception.EntityNotFoundException;
import exception.ValidationException;
import repository.TicketRepository;
import repository.TransactionHelper;

import java.time.LocalDateTime;
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

    public Ticket save(Ticket ticket, Long currentUserId) {
        return saveTransactional(ticket, currentUserId);
    }

    public List<Ticket> findAll() {
        return findAllTransactional();
    }

    public List<Ticket> findAllByUserId(Long userId) {
        return findAllByUserIdTransactional(userId);
    }

    public void refund(Long id, Long userId) {
        refundTransactional(id, userId);
    }

    public void refund(Long id) {
        refundTransactional(id);
    }

    public List<Ticket> findAllActualByUserId(Long currentUserId) {
        return findAllActualByUserIdTransactional(currentUserId);
    }

    private Ticket saveTransactional(Ticket ticket, Long currentUserId) {
        return transactionHelper.executeInTransaction(() -> {
            var flightId = ticket.getFlight().getId();
            var passengerId = ticket.getPassenger().getId();

            var passenger = passengerService.findById(passengerId);
            if (!passenger.getUserId().equals(currentUserId)) {
                throw new ValidationException("Вы можете покупать билеты только для своих пассажиров");
            }
            ticketRepository.findByFlightIdAndPassengerId(flightId, passengerId)
                    .ifPresent(existingTicket -> {
                        if (existingTicket.getTicketStatus() == TicketStatus.ACTIVE) {
                            throw new EntityAlreadyExistException("У пассажира уже есть билет на этот рейс");
                        }
                    });

            var flight = ticket.getFlight();
            var freeSeats = flight.getFreeSeats();

            if (freeSeats <= 0) {
                throw new ValidationException("На рейсе нет свободных мест");
            }

            ticketRepository.findByFlightIdAndSeatNumber(ticket.getFlight().getId(), ticket.getSeatNumber())
                    .ifPresent(exsistedTicket -> {
                        throw new EntityAlreadyExistException("Место %d занято".formatted(ticket.getSeatNumber()));
                    });

            flight.setFreeSeats(flight.getFreeSeats() - 1);
            flightService.update(flight);
            ticket.setTicketStatus(TicketStatus.ACTIVE);

            passengerService.updateFavoriteAirports(passengerId, ticket.getFlight().getDepartureAirport().getCode());
            ticket.setPurchaseDate(LocalDateTime.now());
            return ticketRepository.save(ticket);
        });
    }

    private List<Ticket> findAllTransactional() {
        return transactionHelper.executeInTransaction(ticketRepository::findAll);
    }

    private List<Ticket> findAllByUserIdTransactional(Long userId) {
        return transactionHelper.executeInTransaction(() -> ticketRepository.findAllByUserId(userId));
    }

    private void refundTransactional(Long id, Long userId) {
        transactionHelper.executeInTransaction(() -> {


            var ticket = ticketRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Ticket with id %d not found".formatted(id)));

            if (!ticket.getPassenger().getUserId().equals(userId)) {
                throw new ValidationException("Ticket is not linked to the current user");
            }

            processRefund(ticket);
        });
    }

    private void refundTransactional(Long id) {
        transactionHelper.executeInTransaction(() -> {
            var ticket = ticketRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Ticket with id %d not found".formatted(id)));

            processRefund(ticket);
        });
    }

    private void processRefund(Ticket ticket) {
        ticket.setTicketStatus(TicketStatus.REFUNDED);
        ticketRepository.deleteById(ticket.getId());

        var passengerId = ticket.getPassenger().getId();
        var airport = airportService.findByCode(ticket.getFlight().getDepartureAirport().getCode());
        passengerService.refundFavoriteAirport(passengerId, airport.getId());

        var flight = ticket.getFlight();
        flight.setFreeSeats(flight.getFreeSeats() + 1);
        flightService.update(flight);
    }

    private List<Ticket> findAllActualByUserIdTransactional(Long currentUserId) {
        return transactionHelper.executeInTransaction(() -> ticketRepository.findAllActualByUserId(currentUserId));
    }
}
