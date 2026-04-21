package repository;

import domain.Flight;

import java.util.List;
import java.util.Optional;

public interface FlightRepository extends Repository<Flight> {
    @Override
    Flight save(Flight entity);

    @Override
    Optional<Flight> findById(Long id);

    @Override
    List<Flight> findAll();

    @Override
    Flight update(Flight entity);
}
