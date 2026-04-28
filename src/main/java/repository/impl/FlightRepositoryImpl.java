package repository.impl;

import domain.Flight;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.FlightRepository;
import repository.SessionHelper;

import java.util.List;
import java.util.Optional;

public class FlightRepositoryImpl implements FlightRepository {
    private static final Logger log = LogManager.getLogger(FlightRepositoryImpl.class);
    private final SessionHelper sessionHelper;

    public FlightRepositoryImpl(SessionHelper sessionHelper) {
        this.sessionHelper = sessionHelper;
    }

    @Override
    public Flight save(Flight flight) {
        var session = sessionHelper.getSession();

        session.persist(flight);

        return flight;
    }

    @Override
    public Optional<Flight> findById(Long id) {
        var session = sessionHelper.getSession();

        var hql = """
                FROM Flight flight 
                JOIN FETCH flight.departureAirport departureAirport
                JOIN FETCH departureAiprort.address
                JOIN FETCH flight.arrivalAirport arrivalAirport
                JOIN FETCH arrivalAirport.address
                WHERE flight.id = ?1
                """;

        return session.createQuery(hql, Flight.class)
                .setParameter(1, id)
                .uniqueResultOptional();
    }

    @Override
    public List<Flight> findAll() {
        var session = sessionHelper.getSession();

        var hql = """
                FROM Flight flight 
                JOIN FETCH flight.departureAirport departureAirport
                JOIN FETCH departureAiprort.address
                JOIN FETCH flight.arrivalAirport arrivalAirport
                JOIN FETCH arrivalAirport.address
                """;
        return session.createQuery(hql, Flight.class)
                .getResultList();
    }

    @Override
    public Flight update(Flight flight) {
        var session = sessionHelper.getSession();

        return session.merge(flight);
    }

    @Override
    public void deleteById(Long id) {
        var session = sessionHelper.getSession();

        var flight = session.find(Flight.class, id);

        session.remove(flight);
    }
}
