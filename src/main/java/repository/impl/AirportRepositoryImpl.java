package repository.impl;

import domain.Airport;
import mapper.AirportResultSetMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.AirportRepository;
import repository.ConnectionHelper;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AirportRepositoryImpl implements AirportRepository {
    private static final Logger log = LogManager.getLogger(AirportRepositoryImpl.class);
    public static final String SELECT_QUERY = """
            SELECT airport.id AS airport_id,
                   airport.code AS airport_code,
                   airport.name AS airport_name,
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
    public Airport save(Airport airport) {
        var sql = """
                INSERT INTO tickets_application.airport(code, name, address_id)
                VALUES (?, ?, ?)
                RETURNING id;
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, airport.getCode());
            preparedStatement.setString(2, airport.getName());
            preparedStatement.setLong(3, airport.getAddress().getId());

            var resultSet = preparedStatement.executeQuery();

            resultSet.next();
            airport.setId(resultSet.getLong(1));

            return airport;
        } catch (SQLException e) {
            log.error("Airport save in database error.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Airport> findById(Long id) {
        var sql = SELECT_QUERY + " WHERE airport.id = ?";

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            log.error("Airport find by id {} error.", id, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Airport> findAll() {
        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            log.error("Airport find all error.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Airport update(Airport airport) {
        var sql = """
                UPDATE tickets_application.airport SET code = ?, name = ?, address_id = ? WHERE id = ?
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, airport.getCode());
            preparedStatement.setString(2, airport.getName());
            preparedStatement.setLong(3, airport.getAddress().getId());
            preparedStatement.setLong(4, airport.getId());

            preparedStatement.executeUpdate();

            return airport;
        } catch (SQLException e) {
            log.error("Airport with id {} update error.", airport.getId(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(long id) {
        var sql = """
                DELETE FROM tickets_application.airport WHERE id = ?
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Airport with id {} delete error.", id, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Airport> findByCode(String code) {
        var sql = SELECT_QUERY + " WHERE airport.code = ?";
        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, code);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            log.error("Get airport by code {} error.", code, e);
            throw new RuntimeException(e);
        }
    }
}
