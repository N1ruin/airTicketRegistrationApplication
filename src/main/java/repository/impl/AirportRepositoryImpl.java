package repository.impl;

import domain.Airport;
import exception.RepositoryException;
import mapper.AirportResultSetMapper;
import repository.AirportRepository;
import util.ConnectionHelper;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AirportRepositoryImpl implements AirportRepository {
    public static final String SELECT_QUERY = """
           SELECT airport.code AS airport_code,
                   airport.name AS airport_name,
                   airport.worked AS airport_worked,
                   address.id AS address_id,
                   address.country AS address_country,
                   address.city AS address_city,
                   address.street AS address_street,
                   address.house_number AS address_house_number
            FROM tickets_application.airport AS airport
            JOIN tickets_application.address AS address ON airport.address_id = address.id 
           """;
    private final ConnectionHelper connectionHelper;
    private final AirportResultSetMapper resultSetMapper;

    public AirportRepositoryImpl(ConnectionHelper connectionHelper, AirportResultSetMapper resultSetMapper) {
        this.connectionHelper = connectionHelper;
        this.resultSetMapper = resultSetMapper;
    }

    @Override
    public Airport create(Airport airport) {
        var sql = """
                INSERT INTO tickets_application.airport(code, name, address_id, worked)
                VALUES (?, ?, ?, ?)
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, airport.getCode());
            preparedStatement.setString(2, airport.getName());
            preparedStatement.setLong(3, airport.getAddress().getId());
            preparedStatement.setBoolean(4, airport.isWorked());

            preparedStatement.executeUpdate();

            return airport;
        } catch (SQLException e) {
            throw new RepositoryException("Airport saving error", e);
        }
    }

    @Override
    public Optional<Airport> findById(String code) {
        var sql = SELECT_QUERY + "WHERE airport.code = ?";

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, code);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Airport find by code error", e);
        }
    }

    @Override
    public List<Airport> findAll() {
        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Airport find all error", e);
        }
    }

    @Override
    public Airport update(Airport airport) {
        var sql = """
                UPDATE tickets_application.airport SET
                name = ?,
                address_id = ?,
                worked = ?
                WHERE сode = ?
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, airport.getName());
            preparedStatement.setLong(2, airport.getAddress().getId());
            preparedStatement.setBoolean(3, airport.isWorked());
            preparedStatement.setString(4, airport.getCode());

            preparedStatement.executeUpdate();

            return airport;
        } catch (SQLException e) {
            throw new RepositoryException("Airport update error", e);
        }
    }

    @Override
    public void deleteById(String code) {
        var sql = "DELETE FROM tickets_application.airport WHERE code = ?";

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, code);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Airport delete error", e);
        }
    }
}
