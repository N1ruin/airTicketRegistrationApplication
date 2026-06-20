package repository.impl;

import domain.Passenger;
import exception.RepositoryException;
import mapper.PassengerResultSetMapper;
import util.ConnectionHelper;
import repository.PassengerRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PassengerRepositoryImpl implements PassengerRepository {
    private static final String SELECT_QUERY = """
            SELECT passenger.id AS passenger_id,
                   passenger.user_id AS user_id,
                   passport.id AS passport_id,
                   passport.passport_series AS passport_series,
                   passport.passport_number AS passport_number,
                   passport.citizenship AS passport_citizenship,
                   passport.passport_issue_date AS passport_issue_date,
                   passport.passport_expired_date AS passport_expired_date,
                   passport.first_name AS first_name,
                   passport.last_name AS last_name,
                   passport.father_name AS father_name,
                   passport.birth_date AS birth_date,
                   passport.male AS male
            FROM tickets_application.passenger AS passenger
            JOIN tickets_applicatiosn.passport AS passport ON passenger.passport_id = passport.id 
            """;
    private final ConnectionHelper connectionHelper;
    private final PassengerResultSetMapper resultSetMapper;

    public PassengerRepositoryImpl(ConnectionHelper connectionHelper, PassengerResultSetMapper resultSetMapper) {
        this.connectionHelper = connectionHelper;
        this.resultSetMapper = resultSetMapper;
    }

    @Override
    public Passenger create(Passenger passenger) {
        var sql = """
                INSERT INTO tickets_application.passenger(
                passport_id,
                user_id)
                VALUES (?, ?)
                RETURNING id;
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            fillPreparedStatement(passenger, preparedStatement);
            preparedStatement.setLong(1, passenger.getPassport().getId());
            preparedStatement.setLong(2, passenger.getUserId());

            var resultSet = preparedStatement.executeQuery();

            resultSet.next();
            passenger.setId(resultSet.getLong(1));

            return passenger;
        } catch (SQLException e) {
            throw new RepositoryException("Passenger save error", e);
        }
    }

    @Override
    public Optional<Passenger> findById(Long id) {
        var sql = SELECT_QUERY + "WHERE passenger.id = ?";

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Passenger find by id error", e);
        }
    }

    @Override
    public List<Passenger> findAllByUserId(Long userId) {
        var sql = SELECT_QUERY + " WHERE user_id = ?";

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Passenger find all error", e);
        }
    }

    @Override
    public List<Passenger> findAll() {
        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Passenger find all error", e);
        }
    }

    @Override
    public Passenger update(Passenger passenger) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteById(Long id) {
        var sql = """
                DELETE FROM tickets_application.passenger WHERE id = ?
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Passenger delete error", e);
        }
    }

    @Override
    public Optional<Passenger> findByIdAndUserId(Long id, Long userId) {
        var sql = SELECT_QUERY + " WHERE passenger.id = ? AND passenger.user_id = ?";

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setLong(2, userId);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Passenger find by id and user id error", e);
        }
    }

    private void fillPreparedStatement(Passenger passenger, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setBoolean(1, passenger.getPassport().isMale());
    }
}
