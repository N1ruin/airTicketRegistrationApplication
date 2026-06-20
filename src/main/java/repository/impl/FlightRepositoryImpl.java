package repository.impl;

import domain.Flight;
import exception.RepositoryException;
import mapper.FlightResultSetMapper;
import repository.FlightRepository;
import util.ConnectionHelper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FlightRepositoryImpl implements FlightRepository {
    private static final String SELECT_QUERY = """
            SELECT flight.id AS flight_id,
            flight.all_seats AS all_seats,
            flight.free_seats AS free_seats,
            flight.departure_date AS departure_date,
            flight.arrival_date AS arrival_date,
            departure_airport.code AS departure_airport_code,
            arrival_airport.code AS arrival_airport_code,
            FROM tickets_application.flight AS flight 
            """;
    private final FlightResultSetMapper resultSetMapper;
    private final ConnectionHelper connectionHelper;

    public FlightRepositoryImpl(FlightResultSetMapper resultSetMapper, ConnectionHelper connectionHelper) {
        this.resultSetMapper = resultSetMapper;
        this.connectionHelper = connectionHelper;
    }

    @Override
    public Flight create(Flight flight) {
        var sql = """
                INSERT INTO tickets_application.flight(
                all_seats,
                free_seats,
                departure_airport_code,
                arrival_airport_code,
                departure_date,
                arrival_date)
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
            throw new RepositoryException("Flight save error", e);
        }
    }

    @Override
    public Optional<Flight> findById(Long id) {
        var sql = SELECT_QUERY + "WHERE flight.id = ?";

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Flight find by id error", e);
        }
    }

    @Override
    public List<Flight> findAll() {
        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Flight find all error", e);
        }
    }

    @Override
    public Flight update(Flight flight) {
        var sql = """
                UPDATE tickets_application.flight SET
                all_seats = ?,
                free_seats = ?,
                departure_airport_code = ?,
                arrival_airport_code = ?,
                departure_date = ?,
                arrival_date = ?
                WHERE id = ?
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(flight, preparedStatement);
            preparedStatement.setLong(7, flight.getId());

            preparedStatement.executeUpdate();

            return flight;
        } catch (SQLException e) {
            throw new RepositoryException("Flight update error", e);
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
            throw new RepositoryException("Flight delete error", e);
        }
    }

    private static void setStatementFields(Flight flight, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(1, flight.getAllSeats());
        preparedStatement.setInt(2, flight.getFreeSeats());
        preparedStatement.setString(3, flight.getDepartureAirportCode());
        preparedStatement.setString(4, flight.getArrivalAirportCode());
        preparedStatement.setObject(5, flight.getDepartureDate());
        preparedStatement.setObject(6, flight.getArrivalDate());
    }
}
