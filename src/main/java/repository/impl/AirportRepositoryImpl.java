package repository.impl;

import domain.Airport;
import domain.AirportStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.AirportRepository;
import repository.SessionHelper;

import java.util.List;
import java.util.Optional;

public class AirportRepositoryImpl implements AirportRepository {
    private static final Logger log = LogManager.getLogger(AirportRepositoryImpl.class);
    private final SessionHelper sessionHelper;

    public AirportRepositoryImpl(SessionHelper sessionHelper) {
        this.sessionHelper = sessionHelper;
    }

    @Override
    public Airport save(Airport airport) {
        airport.setAirportStatus(AirportStatus.WORKS);

        var session = sessionHelper.getSession();

        session.persist(airport);

        return airport;
    }

    @Override
    public Optional<Airport> findById(Long id) {
        var session = sessionHelper.getSession();

        var hql = "FROM Airport airport JOIN FETCH airport.address WHERE airport.id = ?1";

        return session.createQuery(hql, Airport.class)
                .setParameter(1, id)
                .uniqueResultOptional();
    }

    @Override
    public List<Airport> findAll() {
        var hql = "FROM Airport airport JOIN FETCH airport.address";

        var session = sessionHelper.getSession();

        return session.createQuery(hql, Airport.class).getResultList();
    }

    @Override
    public Airport update(Airport airport) {
        var session = sessionHelper.getSession();

        return session.merge(airport);
    }

    @Override
    public void deleteById(Long id) {
        var session = sessionHelper.getSession();
        var airport = session.find(Airport.class, id);

        airport.setAirportStatus(AirportStatus.CLOSED);

        session.merge(airport);
    }

    @Override
    public Optional<Airport> findByCode(String code) {
        var hql = "FROM Airport airport WHERE airport.code = ?1";
        var session = sessionHelper.getSession();

        return session.createQuery(hql, Airport.class).setParameter(1, code)
                .uniqueResultOptional();
    }
}
