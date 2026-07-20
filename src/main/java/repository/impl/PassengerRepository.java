package repository.impl;

import domain.Passenger;
import exception.ApplicationException;
import mapper.PassengerResultSetMapper;
import repository.Repository;
import util.ConnectionHolder;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class PassengerRepository implements Repository<Passenger, Long> {
    private static final String SELECT_QUERY = """
            SELECT passenger.id AS id,
                   passenger.user_id AS user_id,
                   passenger.passport_series AS passport_series,
                   passenger.passport_number AS passport_number,
                   passenger.citizenship AS citizenship,
                   passenger.passport_issue_date AS passport_issue_date,
                   passenger.passport_expired_date AS passport_expired_date,
                   passenger.first_name AS first_name,
                   passenger.last_name AS last_name,
                   passenger.father_name AS father_name,
                   passenger.birth_date AS birth_date,
                   passenger.male AS male
            FROM tickets_application.passenger AS passenger 
            """;

    private final ConnectionHolder connectionHolder;
    private final PassengerResultSetMapper resultSetMapper;

    public PassengerRepository(ConnectionHolder connectionHolder, PassengerResultSetMapper resultSetMapper) {
        this.connectionHolder = connectionHolder;
        this.resultSetMapper = resultSetMapper;
    }

    @Override
    public Passenger create(Passenger passenger) {
        var sql = """
                INSERT INTO tickets_application.passenger(
                passport_series,
                passport_number,
                citizenship,
                passport_issue_date,
                passport_expired_date,
                first_name,
                last_name,
                father_name,
                birth_date,
                male,
                user_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id;
                """;

        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            fillPreparedStatement(passenger, preparedStatement);

            var resultSet = preparedStatement.executeQuery();

            resultSet.next();
            passenger.setId(resultSet.getLong(1));

            return passenger;
        } catch (SQLException e) {
            throw new ApplicationException("Passenger save error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Optional<Passenger> findById(Long id) {
        var sql = SELECT_QUERY + "WHERE passenger.id = ?";

        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new ApplicationException("Passenger find by id error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    public List<Passenger> findAllByUserId(Long userId) {
        var sql = SELECT_QUERY + " WHERE user_id = ?";

        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            throw new ApplicationException("Passenger find all error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Passenger> findAll() {
        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            throw new ApplicationException("Passenger find all error", e, SC_INTERNAL_SERVER_ERROR);
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

        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ApplicationException("Passenger delete error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    public Optional<Passenger> findByIdAndUserId(Long id, Long userId) {
        var sql = SELECT_QUERY + " WHERE passenger.id = ? AND passenger.user_id = ?";

        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setLong(2, userId);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new ApplicationException("Passenger find by id and user id error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }


    public Optional<Passenger> findBySeriesAndNumberAndCitizenship(String series, String number, String citizenship) {
        var sql = SELECT_QUERY + """
                WHERE passport_series = ?
                AND passport_number = ?
                AND citizenship = ?
                """;

        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, series);
            preparedStatement.setString(2, number);
            preparedStatement.setString(3, citizenship);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new ApplicationException("Passenger find by series and number and citizenship error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void fillPreparedStatement(Passenger passenger, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, passenger.getPassportSeries());
        preparedStatement.setString(2, passenger.getPassportNumber());
        preparedStatement.setString(3, passenger.getCitizenship());

        if (passenger.getPassportIssueDate() != null) {
            preparedStatement.setDate(4, Date.valueOf(passenger.getPassportIssueDate()));
        } else {
            preparedStatement.setNull(4, Types.DATE);
        }

        if (passenger.getPassportExpiredDate() != null) {
            preparedStatement.setDate(5, Date.valueOf(passenger.getPassportExpiredDate()));
        } else {
            preparedStatement.setNull(5, Types.DATE);
        }

        preparedStatement.setString(6, passenger.getFirstName());
        preparedStatement.setString(7, passenger.getLastName());
        preparedStatement.setString(8, passenger.getFatherName());
        preparedStatement.setDate(9, Date.valueOf(passenger.getBirthDate()));
        preparedStatement.setBoolean(10, passenger.isMale());
        preparedStatement.setLong(11, passenger.getUserId());
    }
}
