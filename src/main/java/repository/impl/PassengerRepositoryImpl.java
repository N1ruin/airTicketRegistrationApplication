package repository.impl;

import domain.Airport;
import domain.Passenger;
import domain.PassengerFavoriteAirport;
import domain.PassengerFavoriteAirportId;
import exception.RepositoryException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.PassengerRepository;
import repository.SessionHelper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PassengerRepositoryImpl implements PassengerRepository {
    private static final Logger log = LogManager.getLogger(PassengerRepositoryImpl.class);
    private final SessionHelper sessionHelper;

    public PassengerRepositoryImpl(SessionHelper sessionHelper) {
        this.sessionHelper = sessionHelper;
    }

    @Override
    public Passenger save(Passenger passenger) {
        var session = sessionHelper.getSession();

        session.persist(passenger);

        return passenger;
    }

    @Override
    public Optional<Passenger> findById(Long id) {
        var session = sessionHelper.getSession();

        var hql = """
                FROM Passenger passenger
                JOIN FETCH passenger.passport
                JOIN FETCH passenger.user
                JOIN FETCH passenger.favoriteAirports
                WHERE passenger.id = ?1
                """;

        return session.createQuery(hql, Passenger.class)
                .setParameter(1, id)
                .uniqueResultOptional();
    }

    @Override
    public List<Passenger> findAllByUserId(Long userId) {
        var session = sessionHelper.getSession();

        var hql = """
                 FROM Passenger passenger
                JOIN FETCH passenger.passport
                JOIN FETCH passenger.user
                JOIN FETCH passenger.favoriteAirports
                WHERE passenger.user.id = ?1
                """;

        return session.createQuery(hql, Passenger.class)
                .setParameter(1, userId)
                .getResultList();
    }

    @Override
    public List<Passenger> findAll() {
        var session = sessionHelper.getSession();

        var hql = """
                  FROM Passenger passenger
                JOIN FETCH passenger.passport
                JOIN FETCH passenger.user
                JOIN FETCH passenger.favoriteAirports
                """;
        return session.createQuery(hql, Passenger.class)
                .getResultList();
    }

    @Override
    public Passenger update(Passenger passenger) {
        var session = sessionHelper.getSession();

        return session.merge(passenger);
    }

    @Override
    public void deleteById(Long id) {
        var session = sessionHelper.getSession();

        var passenger = session.getReference(Passenger.class, id);

        session.remove(passenger);
    }

    @Override
    public void updateFavoriteAirports(Long passengerId, Long airportId) {
        var session = sessionHelper.getSession();

        var hql = """
                FROM PassengerFavoriteAirport pfa
                WHERE pfa.passenger.id = ?1
                AND pfa.airport.id = ?2
                """;

        var passenger = session.getReference(Passenger.class, passengerId);
        var airport = session.getReference(Airport.class, airportId);

        var favoriteAirport = session.createQuery(hql, PassengerFavoriteAirport.class)
                .setParameter(1, passengerId)
                .setParameter(2, airportId)
                .uniqueResultOptional()
                .orElseGet(() -> {
                    var newFavorite = new PassengerFavoriteAirport();
                    newFavorite.setPassenger(passenger);
                    newFavorite.setAirport(airport);
                    newFavorite.setFlightCounts(0);
                    session.persist(newFavorite);
                    return newFavorite;
                });

        favoriteAirport.setFlightCounts(favoriteAirport.getFlightCounts() + 1);
    }

    @Override
    public void refundFavoriteAirport(Long passengerId, Long airportId) {
        var session = sessionHelper.getSession();

        var hql = """
                FROM PassengerFavoriteAirport pfa
                            WHERE pfa.passenger.id = ?1
                            AND pfa.airport.id = ?2
                """;

        var favoriteAirport = session.createQuery(hql, PassengerFavoriteAirport.class)
                .setParameter(1, passengerId)
                .setParameter(2, airportId)
                .uniqueResultOptional()
                .orElseThrow(() -> new RepositoryException("Favorite airport not found for passenger %d and airport %d"
                        .formatted(passengerId, airportId)
                ));

        if (favoriteAirport.getFlightCounts() > 0) {
            favoriteAirport.setFlightCounts(favoriteAirport.getFlightCounts() - 1);
        }
    }

    @Override
    public Optional<Passenger> findByPassportId(Long passportId) {
        var session = sessionHelper.getSession();

        var hql = """
                FROM Passenger passenger
                JOIN FETCH passenger.passport
                WHERE passenger.passport.id = ?1
                """;

        return session.createQuery(hql, Passenger.class)
                .setParameter(1, passportId)
                .uniqueResultOptional();
    }
}
