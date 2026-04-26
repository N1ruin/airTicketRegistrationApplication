package repository.impl;

import domain.Ticket;
import domain.TicketStatus;
import exception.RepositoryException;
import mapper.TicketResultSetMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.SessionHelper;
import repository.TicketRepository;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TicketRepositoryImpl implements TicketRepository {
    public static final Logger log = LogManager.getLogger(TicketRepositoryImpl.class);
    private static final String SELECT_QUERY = """
            SELECT
            ticket.id AS ticket_id,
            ticket.ticket_number AS ticket_number,
            ticket.ticket_status AS ticket_status,
            ticket.service_class AS service_class,
            ticket.seat_number AS seat_number,
            ticket.purchase_date AS purchase_date,
            ticket.updated_date AS updated_date,
            ticket.baggage_weight AS baggage_weight,
            ticket.carry_on_weight AS carry_on_weight,
            flight.id AS flight_id,
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
            arrival_address.house_number AS arrival_address_house_number,
            passenger.id AS passenger_id,
            passenger.first_name AS first_name,
            passenger.last_name AS last_name,
            passenger.father_name AS father_name,
            passenger.male AS male,
            passenger.birth_date AS birth_date,
            passenger.user_id AS user_id,
            passport.id AS passport_id,
            passport.passport_series AS passport_series,
            passport.passport_number AS passport_number,
            passport.citizenship AS passport_citizenship,
            passport.passport_issue_date AS passport_issue_date,
            passport.passport_expired_date AS passport_expired_date
            FROM tickets_application.ticket AS ticket
            JOIN tickets_application.flight AS flight ON ticket.flight_id = flight.id
            JOIN tickets_application.passenger AS passenger ON ticket.passenger_id = passenger.id
            JOIN tickets_application.airport departure_airport ON flight.departure_airport_id = departure_airport.id
            JOIN tickets_application.airport arrival_airport ON flight.arrival_airport_id = arrival_airport.id
            JOIN tickets_application.address departure_address ON departure_airport.address_id = departure_address.id
            JOIN tickets_application.address arrival_address ON arrival_airport.address_id = arrival_address.id
            JOIN tickets_application.passport AS passport ON passenger.passport_id = passport.id
            """;
    private final SessionHelper connectionHelper;

    public TicketRepositoryImpl(SessionHelper connectionHelper) {
        this.connectionHelper = connectionHelper;
    }

    @Override
    public Ticket save(Ticket ticket) {
        var sql = """
                INSERT INTO tickets_application.ticket (ticket_number, ticket_status, service_class, seat_number,
                    flight_id, passenger_id, purchase_date, baggage_weight, carry_on_weight)
                VALUES (nextval('ticket_number_sequence'), ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id, ticket_number;
                """;
        ticket.setPurchaseDate(LocalDateTime.now());

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, ticket.getTicketStatus().name());
            preparedStatement.setString(2, ticket.getServiceClass().name());
            preparedStatement.setInt(3, ticket.getSeatNumber());
            preparedStatement.setLong(4, ticket.getFlight().getId());
            preparedStatement.setLong(5, ticket.getPassenger().getId());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(ticket.getPurchaseDate()));
            preparedStatement.setDouble(7, ticket.getBaggageWeight());
            preparedStatement.setDouble(8, ticket.getCarryOnBaggageWeight());

            var resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                ticket.setId(resultSet.getLong("id"));
                ticket.setTicketNumber(resultSet.getLong("ticket_number"));
            }

            return ticket;
        } catch (SQLException e) {
            log.error("Ticket save error", e);
            throw new RepositoryException("Ticket save error");
        }
    }

    @Override
    public Optional<Ticket> findById(Long id) {
        var sql = SELECT_QUERY + " WHERE ticket.id = ?";

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            log.error("Ticket find by id {} error.", id, e);
            throw new RepositoryException("Ticket find by id error");
        }
    }

    @Override
    public List<Ticket> findAll() {
        var connection = connectionHelper.getSession();
        try (var ps = connection.prepareStatement(SELECT_QUERY)) {
            var resultSet = ps.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            log.error("Find all tickets error", e);
            throw new RepositoryException("Tickets find all error");
        }
    }

    @Override
    public Ticket update(Ticket ticket) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteById(Long id) {
        var sql = """
                UPDATE tickets_application.ticket SET ticket_status = ?, updated_date = ? WHERE id = ?
                """;

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, TicketStatus.REFUNDED.name());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setLong(3, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Ticket with id {} delete error", id, e);
            throw new RepositoryException("Ticket delete error");
        }
    }

    @Override
    public Optional<Ticket> findByFlightIdAndPassengerId(Long flightId, Long passengerId) {
        var sql = SELECT_QUERY + " WHERE flight.id = ? AND passenger.id = ?";

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, flightId);
            preparedStatement.setLong(2, passengerId);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            log.error("Ticket find by flightId {} and passenger id {} error.", flightId, passengerId, e);
            throw new RepositoryException("Ticket find by flight and passenger id error");
        }
    }

    @Override
    public Optional<Ticket> findByFlightIdAndSeatNumber(Long id, Integer seatNumber) {
        var sql = SELECT_QUERY + " WHERE flight.id = ? AND ticket.seat_number = ?";

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setInt(2, seatNumber);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            log.error("Ticket find by flight id {} and seat number {} error.", id, seatNumber, e);
            throw new RepositoryException("Ticket find by flight id and seat number error");
        }
    }

    @Override
    public List<Ticket> findAllByUserId(Long userId) {
        var sql = SELECT_QUERY + " WHERE passenger.user_id = ?";

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            log.error("Find all by user id {} error.", userId, e);
            throw new RepositoryException("Ticket find all by user id error");
        }
    }

    @Override
    public List<Ticket> findAllActualByUserId(Long currentUserId) {
        var sql = SELECT_QUERY + " WHERE passenger.user_id = ? AND flight.departure_date > now() AND ticket.ticket_status != 'REFUNDED'";

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, currentUserId);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            log.error("Find all actual tickets by user id {} error.", currentUserId, e);
            throw new RepositoryException("Ticket find all actual error");
        }
    }
}
