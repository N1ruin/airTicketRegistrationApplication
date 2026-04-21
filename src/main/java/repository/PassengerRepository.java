package repository;

import domain.Passenger;

import java.util.List;
import java.util.Optional;

public interface PassengerRepository extends Repository<Passenger> {
    void updateFavoriteAirports(Long passengerId, Long airportID);

    void refundFavoriteAirport(Long passengerId, Long airportId);

    List<Passenger> findAllByUserId(Long id);

    Optional<Passenger> findByPassportId(Long passportId);
}
