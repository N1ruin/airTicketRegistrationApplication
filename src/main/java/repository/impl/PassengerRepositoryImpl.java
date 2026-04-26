package repository.impl;

import domain.Passenger;
import exception.RepositoryException;
import mapper.PassengerResultSetMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.SessionHelper;
import repository.PassengerRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PassengerRepositoryImpl implements PassengerRepository {
    private static final Logger log = LogManager.getLogger(PassengerRepositoryImpl.class);
    private static final String SELECT_QUERY = """
            SELECT passenger.id AS passenger_id,
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
            FROM tickets_application.passenger AS passenger
            JOIN tickets_application.passport AS passport ON passenger.passport_id = passport.id
            """;
    private final SessionHelper connectionHelper;

    public PassengerRepositoryImpl(SessionHelper connectionHelper) {
        this.connectionHelper = connectionHelper;
    }

    @Override
    public Passenger save(Passenger passenger) {
        var sql = """
                INSERT INTO tickets_application.passenger(first_name, last_name, father_name, male, birth_date, passport_id, user_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id;
                """;

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            fillPreparedStatement(passenger, preparedStatement);
            preparedStatement.setLong(6, passenger.getPassport().getId());
            preparedStatement.setLong(7, passenger.getUserId());

            var resultSet = preparedStatement.executeQuery();

            resultSet.next();
            passenger.setId(resultSet.getLong(1));

            return passenger;
        } catch (SQLException e) {
            log.error("Passenger save in database error.", e);
            throw new RepositoryException("Passenger save error");
        }
    }

    @Override
    public Optional<Passenger> findById(Long id) {
        var sql = SELECT_QUERY + " WHERE passenger.id = ?";

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            log.error("Passenger find by id {} error.", id, e);
            throw new RepositoryException("Passenger find by id error");
        }
    }

    @Override
    public List<Passenger> findAllByUserId(Long userId) {
        var sql = SELECT_QUERY + " WHERE user_id = ?";

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            log.error("Find all passengers by user id {} error.", userId, e);
            throw new RepositoryException("Passenger find all error");
        }
    }

    @Override
    public List<Passenger> findAll() {
        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            log.error("Passenger find all error.", e);
            throw new RepositoryException("Passenger find all error");
        }
    }

    @Override
    public Passenger update(Passenger passenger) {
        var sql = """
                UPDATE tickets_application.passenger
                SET first_name = ?, last_name = ?, father_name = ?, male = ?, birth_date = ?, passport_id = ?
                WHERE id = ?
                """;

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            fillPreparedStatement(passenger, preparedStatement);
            preparedStatement.setLong(6, passenger.getPassport().getId());
            preparedStatement.setLong(7, passenger.getId());

            preparedStatement.executeUpdate();

            return passenger;
        } catch (SQLException e) {
            log.error("Passenger with id {} update error.", passenger.getId(), e);
            throw new RepositoryException("Passenger update error");
        }
    }

    @Override
    public void deleteById(Long id) {
        var sql = """
                DELETE FROM tickets_application.passenger WHERE id = ?
                """;

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Passenger with id {} delete error.", id, e);
            throw new RepositoryException("Passenger delete error");
        }
    }

    @Override
    public void updateFavoriteAirports(Long passengerId, Long airportId) {
        var updateSql = """
                UPDATE tickets_application.passenger_favorite_airports
                SET flights_count = flights_count + 1
                WHERE passenger_id = ? AND airport_id = ?
                """;
        var insertSql = """
                INSERT INTO tickets_application.passenger_favorite_airports (passenger_id, airport_id, flights_count)
                VALUES (?, ?, 1)
                """;

        var connection = connectionHelper.getSession();
        try {
            try (var preparedStatement = connection.prepareStatement(updateSql)) {
                preparedStatement.setLong(1, passengerId);
                preparedStatement.setLong(2, airportId);

                int rowsUpdated = preparedStatement.executeUpdate();

                if (rowsUpdated == 0) {
                    try (var psInsert = connection.prepareStatement(insertSql)) {
                        psInsert.setLong(1, passengerId);
                        psInsert.setLong(2, airportId);

                        psInsert.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error incrementing favorite airport", e);
            throw new RepositoryException("Passenger update favorite airports error");
        }
    }

    @Override
    public void refundFavoriteAirport(Long passengerId, Long airportId) {
        var sql = """
                UPDATE tickets_application.passenger_favorite_airports
                SET flights_count = flights_count - 1
                WHERE passenger_id = ? AND airport_id = ? AND flights_count > 0
                """;

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, passengerId);
            preparedStatement.setLong(2, airportId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Error decrementing favorite airport", e);
            throw new RepositoryException("Refund favorite airport error");
        }
    }

    @Override
    public Optional<Passenger> findByPassportId(Long passportId) {
        var sql = SELECT_QUERY + " WHERE passport.id = ?";

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, passportId);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            log.error("Error finding passenger by passport_id {}", passportId, e);
            throw new RepositoryException("Passenger find by passport error");
        }
    }

    private void fillPreparedStatement(Passenger passenger, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, passenger.getFirstName());
        preparedStatement.setString(2, passenger.getLastName());
        preparedStatement.setString(3, passenger.getFatherName());
        preparedStatement.setBoolean(4, passenger.isMale());
        preparedStatement.setObject(5, passenger.getBirthDate());
    }
}
