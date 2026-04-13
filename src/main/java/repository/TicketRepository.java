package repository;

import domain.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends Repository<Ticket> {
    Optional<Ticket> findByFlightIdAndPassengerId(Long flightId, Long passengerId);

    Optional<Ticket> findByFlightIdAndSeatNumber(Long id, Integer seatNumber);

    List<Ticket> findAllByUserId(Long userId);

    List<Ticket> findAllActualByUserId(Long currentUserId);
}
