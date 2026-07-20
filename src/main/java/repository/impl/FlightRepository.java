package repository.impl;

import domain.Flight;
import exception.ApplicationException;
import mapper.FlightResultSetMapper;
import repository.Repository;
import util.ConnectionHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class FlightRepository implements Repository<Flight, Long> {
    private static final String SELECT_QUERY = """
            SELECT flight.id AS id,
            flight.seats_count AS seats_count,
            flight.free_seats AS free_seats,
            flight.departure_date AS departure_date,
            flight.arrival_date AS arrival_date,
            departure_airport.id AS departure_airport_id,
            arrival_airport.id AS arrival_airport_id,
            FROM tickets_application.flight AS flight 
            """;

    private final FlightResultSetMapper resultSetMapper;
    private final ConnectionHolder connectionHolder;

    public FlightRepository(FlightResultSetMapper resultSetMapper, ConnectionHolder connectionHolder) {
        this.resultSetMapper = resultSetMapper;
        this.connectionHolder = connectionHolder;
    }

    @Override
    public Flight create(Flight flight) {
        var sql = """
                INSERT INTO tickets_application.flight(
                seats_count,
                free_seats,
                departure_airport_id,
                arrival_airport_id,
                departure_date,
                arrival_date)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id;
                """;

        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(flight, preparedStatement);

            var resultSet = preparedStatement.executeQuery();

            resultSet.next();
            flight.setId(resultSet.getLong(1));

            return flight;
        } catch (SQLException e) {
            throw new ApplicationException("Flight save error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Optional<Flight> findById(Long id) {
        var sql = SELECT_QUERY + "WHERE flight.id = ?";

        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new ApplicationException("Flight find by id error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Flight> findAll() {
        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Flight find all error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Flight update(Flight flight) {
        var sql = """
                UPDATE tickets_application.flight SET
                seats_count = ?,
                free_seats = ?,
                departure_airport_id = ?,
                arrival_airport_id = ?,
                departure_date = ?,
                arrival_date = ?
                WHERE id = ?
                """;

        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(flight, preparedStatement);
            preparedStatement.setLong(7, flight.getId());

            preparedStatement.executeUpdate();

            return flight;
        } catch (SQLException e) {
            throw new RepositoryException("Flight update error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteById(Long id) {
        var sql = """
                DELETE FROM tickets_application.flight WHERE id = ?
                """;
        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Flight delete error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    private static void setStatementFields(Flight flight, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(1, flight.getSeatsCount());
        preparedStatement.setInt(2, flight.getFreeSeats());
        preparedStatement.setString(3, flight.getDepartureAirportId());
        preparedStatement.setString(4, flight.getArrivalAirportId());
        preparedStatement.setObject(5, flight.getDepartureDate());
        preparedStatement.setObject(6, flight.getArrivalDate());
    }
}
