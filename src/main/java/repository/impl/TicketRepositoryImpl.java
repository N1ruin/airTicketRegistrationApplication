package repository.impl;

import domain.Ticket;
import exception.RepositoryException;
import mapper.TicketResultSetMapper;
import repository.TicketRepository;
import util.ConnectionHelper;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class TicketRepositoryImpl implements TicketRepository {
    private static final String SELECT_QUERY = """
            SELECT
            ticket.id AS ticket_id,
            ticket.ticket_number AS ticket_number,
            ticket.ticket_status AS ticket_status,
            ticket.ticket_rank AS ticket_rank,
            ticket.seat_number AS seat_number,
            ticket.purchase_date AS purchase_date,
            ticket.updated_date AS updated_date,
            ticket.baggage_weight AS baggage_weight,
            ticket.carry_on_weight AS carry_on_weight,
            ticket.flight_id AS flight_id,
            ticket.passenger_id AS passenger_id
            FROM tickets_application.ticket AS ticket
            """;
    private final ConnectionHelper connectionHelper;
    private final TicketResultSetMapper resultSetMapper;

    public TicketRepositoryImpl(ConnectionHelper connectionHelper, TicketResultSetMapper ticketResultSetMapper) {
        this.connectionHelper = connectionHelper;
        this.resultSetMapper = ticketResultSetMapper;
    }

    @Override
    public Ticket create(Ticket ticket) {
        var sql = """
                INSERT INTO tickets_application.ticket (
                    ticket_number,
                    ticket_status,
                    ticket_rank,
                    seat_number,
                    flight_id,
                    passenger_id,
                    purchase_date,
                    baggage_weight,
                    carry_on_weight)
                VALUES (nextval('ticket_number_sequence'), ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id, ticket_number;
                """;

        ticket.setPurchaseDate(ZonedDateTime.now());

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, ticket.getTicketStatus().name());
            preparedStatement.setString(2, ticket.getTicketRank().name());
            preparedStatement.setInt(3, ticket.getSeatNumber());
            preparedStatement.setLong(4, ticket.getFlightId());
            preparedStatement.setLong(5, ticket.getPassengerId());
            preparedStatement.setTimestamp(6, Timestamp.from(ticket.getPurchaseDate().toInstant()));
            preparedStatement.setDouble(7, ticket.getBaggageWeight());
            preparedStatement.setDouble(8, ticket.getCarryOnBaggageWeight());

            var resultSet = preparedStatement.executeQuery();

            ticket.setId(resultSet.getLong("id"));
            ticket.setTicketNumber(resultSet.getLong("ticket_number"));

            return ticket;
        } catch (SQLException e) {
            throw new RepositoryException("Ticket save error", e);
        }
    }

    @Override
    public Optional<Ticket> findById(Long id) {
        var sql = SELECT_QUERY + " WHERE ticket.id = ?";

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Ticket find by id error", e);
        }
    }

    @Override
    public List<Ticket> findAll() {
        var connection = connectionHelper.getConnection();
        try (var ps = connection.prepareStatement(SELECT_QUERY)) {
            var resultSet = ps.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Tickets find all error", e);
        }
    }

    @Override
    public Ticket update(Ticket ticket) {
        var sql = """
                UPDATE tickets_application.ticket SET
                ticket_status = ?,
                updated_date = ?,
                seat_number = ?,
                baggage_weight = ?,
                carry_on_weight = ?
                WHERE id = ?
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, ticket.getTicketStatus().name());
            preparedStatement.setTimestamp(2, Timestamp.from(ticket.getUpdatedDate().toInstant()));
            preparedStatement.setInt(3, ticket.getSeatNumber());
            preparedStatement.setDouble(4, ticket.getBaggageWeight());
            preparedStatement.setDouble(5, ticket.getCarryOnBaggageWeight());
            preparedStatement.setLong(6, ticket.getId());

            preparedStatement.executeUpdate();

            return ticket;
        } catch (SQLException e) {
            throw new RepositoryException("Ticket update error", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        var sql = "DELETE FROM tickets_application.ticket WHERE id = ?";

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Ticket delete error", e);
        }
    }

    @Override
    public Optional<Ticket> findByFlightIdAndPassengerId(Long flightId, Long passengerId) {
        var sql = SELECT_QUERY + " WHERE ticket.flight_id = ? AND ticket.passenger_id = ?";

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, flightId);
            preparedStatement.setLong(2, passengerId);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Ticket find by flight and passenger id error", e);
        }
    }

    @Override
    public Optional<Ticket> findByFlightIdAndSeatNumber(Long id, Integer seatNumber) {
        var sql = SELECT_QUERY + " WHERE ticket.flight_id = ? AND ticket.seat_number = ?";

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setInt(2, seatNumber);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Ticket find by flight id and seat number error", e);
        }
    }

    @Override
    public List<Ticket> findAllByUserId(Long userId) {
        var sql = SELECT_QUERY + """
                JOIN tickets_application.passenger ON ticket.passenger_id = passenger.id
                WHERE passenger.user_id = ?
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Ticket find all by user id error: " + userId, e);
        }
    }

    @Override
    public List<Ticket> findAllActualByUserId(Long userId) {
        var sql = SELECT_QUERY + """
                JOIN tickets_application.passenger ON ticket.passenger_id = passenger.id
                JOIN tickets_application.flight ON ticket.flight_id = flight.id
                WHERE passenger.user_id = ?
                AND flight.departure_date > now()
                AND ticket.ticket_status != 'REFUNDED'
                AND ticket.ticket_status != 'CANCELLED'
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Ticket find all actual error", e);
        }
    }

    @Override
    public Optional<Ticket> findByIdAndCurrentUserId(Long id, Long userId) {
        var sql = SELECT_QUERY + """
                JOIN tickets_application.passenger ON ticket.passenger_id = passenger.id
                WHERE ticket.id = ? AND passenger.user_id = ?
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setLong(2, userId);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Ticket find by id and user id error", e);
        }
    }
}
