package repository;

import domain.Passenger;

import java.util.List;
import java.util.Optional;

public interface PassengerRepository extends Repository<Passenger, Long> {
    List<Passenger> findAllByUserId(Long id);

    Optional<Passenger> findByIdAndUserId(Long id, Long userId);
}
