package repository.impl;

import domain.Airport;
import domain.AirportStatus;
import exception.RepositoryException;
import mapper.AirportResultSetMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.AirportRepository;
import repository.SessionHelper;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AirportRepositoryImpl implements AirportRepository {
    private static final Logger log = LogManager.getLogger(AirportRepositoryImpl.class);
    public static final String SELECT_QUERY = """
            SELECT airport.id AS airport_id,
                   airport.code AS airport_code,
                   airport.name AS airport_name,
                   airport.status AS airport_status,
                   address.id AS address_id,
                   address.country AS address_country,
                   address.city AS address_city,
                   address.street AS address_street,
                   address.house_number AS address_house_number
            FROM tickets_application.airport AS airport
            JOIN tickets_application.address AS address ON airport.address_id = address.id
            """;
    private final SessionHelper connectionHelper;
    private final AirportResultSetMapper resultSetMapper;

    public AirportRepositoryImpl(SessionHelper connectionHelper, AirportResultSetMapper resultSetMapper) {
        this.connectionHelper = connectionHelper;
        this.resultSetMapper = resultSetMapper;
    }

    @Override
    public Airport save(Airport airport) {
        var sql = """
                INSERT INTO tickets_application.airport(code, name, address_id, status)
                VALUES (?, ?, ?, ?)
                RETURNING id;
                """;

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, airport.getCode());
            preparedStatement.setString(2, airport.getName());
            preparedStatement.setLong(3, airport.getAddress().getId());
            preparedStatement.setString(4, AirportStatus.WORKS.name());

            var resultSet = preparedStatement.executeQuery();

            resultSet.next();
            airport.setId(resultSet.getLong(1));
            airport.setAirportStatus(AirportStatus.WORKS);

            return airport;
        } catch (SQLException e) {
            log.error("Airport save in database error.", e);
            throw new RepositoryException("Airport saving error");
        }
    }

    @Override
    public Optional<Airport> findById(Long id) {
        var sql = SELECT_QUERY + " WHERE airport.id = ?";

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            log.error("Airport find by id {} error.", id, e);
            throw new RepositoryException("Airport find by id error");
        }
    }

    @Override
    public List<Airport> findAll() {
        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            log.error("Airport find all error.", e);
            throw new RepositoryException("Airport find all error");
        }
    }

    @Override
    public Airport update(Airport airport) {
        var sql = """
                UPDATE tickets_application.airport SET code = ?, name = ?, address_id = ?, status = ? WHERE id = ?
                """;

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, airport.getCode());
            preparedStatement.setString(2, airport.getName());
            preparedStatement.setLong(3, airport.getAddress().getId());
            preparedStatement.setString(4, airport.getAirportStatus().name());
            preparedStatement.setLong(5, airport.getId());

            preparedStatement.executeUpdate();

            return airport;
        } catch (SQLException e) {
            log.error("Airport with id {} update error.", airport.getId(), e);
            throw new RepositoryException("Airport update error");
        }
    }

    @Override
    public void deleteById(Long id) {
        var sql = """
                UPDATE tickets_application.airport SET status = ? WHERE id = ?
                """;

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, AirportStatus.CLOSED.name());
            preparedStatement.setLong(2, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Airport with id {} delete error.", id, e);
            throw new RepositoryException("Airport delete error");
        }
    }

    @Override
    public Optional<Airport> findByCode(String code) {
        var sql = SELECT_QUERY + " WHERE airport.code = ?";
        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, code);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            log.error("Get airport by code {} error.", code, e);
            throw new RepositoryException("Airport find by code error");
        }
    }
}
