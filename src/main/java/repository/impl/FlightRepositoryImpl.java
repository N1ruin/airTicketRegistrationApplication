package repository.impl;

import domain.Flight;
import exception.RepositoryException;
import mapper.FlightResultSetMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.ConnectionHelper;
import repository.FlightRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FlightRepositoryImpl implements FlightRepository {
    private static final Logger log = LogManager.getLogger(FlightRepositoryImpl.class);
    private static final String SELECT_QUERY = """
            SELECT flight.id AS flight_id,
            flight.all_seats AS all_seats,
            flight.free_seats AS free_seats,
            flight.departure_date AS departure_date,
            flight.arrival_date AS arrival_date,
            departure_airport.id AS departure_airport_id,
            departure_airport.code AS departure_airport_code,
            departure_airport.name AS departure_airport_name,
            departure_airport.status AS departure_airport_status,
            departure_address.id AS departure_address_id,
            departure_address.country AS departure_address_country,
            departure_address.city AS departure_address_city,
            departure_address.street AS departure_address_street,
            departure_address.house_number AS departure_address_house_number,
            arrival_airport.id AS arrival_airport_id,
            arrival_airport.code AS arrival_airport_code,
            arrival_airport.name AS arrival_airport_name,
            arrival_airport.status AS arrival_airport_status,
            arrival_address.id AS arrival_address_id,
            arrival_address.country AS arrival_address_country,
            arrival_address.city AS arrival_address_city,
            arrival_address.street AS arrival_address_street,
            arrival_address.house_number AS arrival_address_house_number
            FROM tickets_application.flight AS flight
            JOIN tickets_application.airport AS departure_airport  ON departure_airport.id = flight.departure_airport_id
            JOIN tickets_application.airport AS arrival_airport ON arrival_airport.id = flight.arrival_airport_id
            JOIN tickets_application.address AS departure_address ON departure_airport.address_id = departure_address.id
            JOIN tickets_application.address AS arrival_address ON arrival_airport.address_id = arrival_address.id
            """;
    private final FlightResultSetMapper resultSetMapper;
    private final ConnectionHelper connectionHelper;

    public FlightRepositoryImpl(FlightResultSetMapper resultSetMapper, ConnectionHelper connectionHelper) {
        this.resultSetMapper = resultSetMapper;
        this.connectionHelper = connectionHelper;
    }

    @Override
    public Flight save(Flight flight) {
        var sql = """
                INSERT INTO tickets_application.flight(all_seats, free_seats, departure_airport_id, arrival_airport_id,
                                                       departure_date, arrival_date)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id;
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(flight, preparedStatement);

            var resultSet = preparedStatement.executeQuery();

            resultSet.next();
            flight.setId(resultSet.getLong(1));

            return flight;
        } catch (SQLException e) {
            log.error("Flight save in database error.", e);
            throw new RepositoryException("Flight save error");
        }
    }

    @Override
    public Optional<Flight> findById(Long id) {
        var sql = SELECT_QUERY + " WHERE flight.id = ?";

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            log.error("Flight find by id {} error.", id, e);
            throw new RepositoryException("Flight find by id error");
        }
    }

    @Override
    public List<Flight> findAll() {
        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            log.error("Flight find all error.", e);
            throw new RepositoryException("Flight find all error");
        }
    }

    @Override
    public Flight update(Flight flight) {
        var sql = """
                UPDATE tickets_application.flight SET all_seats = ?, free_seats = ?, departure_airport_id = ?,
                                                      arrival_airport_id = ?, departure_date = ?, arrival_date = ?
                WHERE id = ?
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(flight, preparedStatement);
            preparedStatement.setLong(7, flight.getId());

            preparedStatement.executeUpdate();

            return flight;
        } catch (SQLException e) {
            log.error("Flight with id {} update error.", flight.getId(), e);
            throw new RepositoryException("Flight update error");
        }
    }

    @Override
    public void deleteById(Long id) {
        var sql = """
                DELETE FROM tickets_application.flight WHERE id = ?
                """;
        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Flight with id {} delete error.", id, e);
            throw new RepositoryException("Flight delete error");
        }
    }

    private static void setStatementFields(Flight flight, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(1, flight.getAllSeats());
        preparedStatement.setInt(2, flight.getFreeSeats());
        preparedStatement.setLong(3, flight.getDepartureAirport().getId());
        preparedStatement.setLong(4, flight.getArrivalAirport().getId());
        preparedStatement.setObject(5, flight.getDepartureDate());
        preparedStatement.setObject(6, flight.getArrivalDate());
    }
}
