package repository.impl;

import domain.Airport;
import exception.RepositoryException;
import mapper.AirportResultSetMapper;
import repository.FavoriteAirportsRepository;
import util.ConnectionHelper;

import java.sql.SQLException;
import java.util.List;

public class FavoriteAirportsRepositoryImpl implements FavoriteAirportsRepository {
    private static final String SELECT_QUERY = """
            SELECT airport.code AS airport_code,
                   airport.name AS airport_name,
                   airport.worked AS airport_worked,
                   address.id AS address_id,
                   address.country AS address_country,
                   address.city AS address_city,
                   address.street AS address_street,
                   address.house_number AS address_house_number
            FROM tickets_application.passenger_favorite_airports AS pfa
            JOIN tickets_application.airport AS airport ON pfa.airport_id = airport.id
            JOIN tickets_application.address AS address ON airport.address_id = address.id
            WHERE pfa.passenger_id = ?
            """;

    private final ConnectionHelper connectionHelper;
    private final AirportResultSetMapper airportResultSetMapper;

    public FavoriteAirportsRepositoryImpl(ConnectionHelper connectionHelper,
                                         AirportResultSetMapper airportResultSetMapper) {
        this.connectionHelper = connectionHelper;
        this.airportResultSetMapper = airportResultSetMapper;
    }

    @Override
    public void addFavorite(Long passengerId, String airportCode) {
        var sql = """
                INSERT INTO tickets_application.passenger_favorite_airports (passenger_id, airport_id)
                VALUES (?, (SELECT id FROM tickets_application.airport WHERE code = ?))
                ON CONFLICT (passenger_id, airport_id) DO NOTHING
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, passengerId);
            preparedStatement.setString(2, airportCode);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Failed to add favorite airport", e);
        }
    }

    @Override
    public void removeFavorite(Long passengerId, String airportCode) {
        var sql = """
                DELETE FROM tickets_application.passenger_favorite_airports
                WHERE passenger_id = ?
                AND airport_id = (SELECT id FROM tickets_application.airport WHERE code = ?)
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, passengerId);
            preparedStatement.setString(2, airportCode);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Failed to remove favorite airport", e);
        }
    }
}