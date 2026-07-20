package repository.impl;

import domain.Airport;
import exception.ApplicationException;
import mapper.AirportResultSetMapper;
import repository.Repository;
import util.ConnectionHolder;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class AirportRepository implements Repository<Airport, String> {
    public static final String SELECT_QUERY = """
            SELECT airport.id AS id,
                    airport.name AS name,
                    airport.is_active AS is_active,
                    address.country AS country,
                    address.city AS city,
                    address.street AS street,
                    address.house_number AS house_number
             FROM tickets_application.airport AS airport 
            """;

    private final ConnectionHolder connectionHolder;
    private final AirportResultSetMapper resultSetMapper;

    public AirportRepository(ConnectionHolder connectionHolder, AirportResultSetMapper resultSetMapper) {
        this.connectionHolder = connectionHolder;
        this.resultSetMapper = resultSetMapper;
    }

    @Override
    public Airport create(Airport airport) {
        var sql = """
                INSERT INTO tickets_application.airport(
                id, 
                name,
                country, 
                city, 
                street,
                house_number, 
                is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, airport.getId());
            preparedStatement.setString(2, airport.getName());
            preparedStatement.setString(3, airport.getCountry());
            preparedStatement.setString(4, airport.getCity());
            preparedStatement.setString(5, airport.getStreet());
            preparedStatement.setString(6, airport.getHouseNumber());
            preparedStatement.setBoolean(7, airport.isActive());

            preparedStatement.executeUpdate();

            return airport;
        } catch (SQLException e) {
            throw new ApplicationException("Airport saving error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Optional<Airport> findById(String id) {
        var sql = SELECT_QUERY + "WHERE airport.id = ?";

        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, id);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new ApplicationException("Airport find by id error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Airport> findAll() {
        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            throw new ApplicationException("Airport find all error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Airport update(Airport airport) {
        var sql = """
                UPDATE tickets_application.airport SET
                name = ?,
                country = ?,
                city = ?,
                street = ?,
                house_number = ?,
                is_active = ?
                WHERE id = ?
                """;

        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, airport.getName());
            preparedStatement.setString(2, airport.getCountry());
            preparedStatement.setString(3, airport.getCity());
            preparedStatement.setString(4, airport.getStreet());
            preparedStatement.setString(5, airport.getHouseNumber());
            preparedStatement.setBoolean(6, airport.isActive());
            preparedStatement.setString(7, airport.getId());

            preparedStatement.executeUpdate();

            return airport;
        } catch (SQLException e) {
            throw new ApplicationException("Airport update error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteById(String id) {
        var sql = "DELETE FROM tickets_application.airport WHERE id = ?";

        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ApplicationException("Airport delete by id %s error".formatted(id), e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    public Optional<Airport> findByCountryAndCityAndStreetAndHouseNumber(Airport airport) {
        var sql = SELECT_QUERY + """
                    WHERE airport.country = ?
                    AND airport.city = ?
                    AND airport.street = ?
                    AND airport.house_number = ?
                """;

        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, airport.getCountry());
            preparedStatement.setString(2, airport.getCity());
            preparedStatement.setString(3, airport.getStreet());
            preparedStatement.setString(4, airport.getHouseNumber());

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new ApplicationException("Address delete error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }
}
