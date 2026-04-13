package repository;

import domain.Passenger;

import java.util.List;

public interface PassengerRepository extends Repository<Passenger> {
    void updateFavoriteAirports(Long passengerId, Long airportID);

    void refundFavoriteAirport(Long passengerId, Long airportId);

    List<Passenger> findAllByUserId(Long id);
}
