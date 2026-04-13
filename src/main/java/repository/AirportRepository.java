package repository;

import domain.Airport;

import java.util.Optional;

public interface AirportRepository extends Repository<Airport> {
    Optional<Airport> findByCode(String code);
}
