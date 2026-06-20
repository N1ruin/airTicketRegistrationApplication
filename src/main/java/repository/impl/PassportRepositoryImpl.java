package repository.impl;

import domain.Passport;
import exception.RepositoryException;
import mapper.PassportResultSetMapper;
import repository.PassportRepository;
import util.ConnectionHelper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PassportRepositoryImpl implements PassportRepository {
    private static final String SELECT_QUERY = """
            SELECT id AS passport_id,
                   passport_series,
                   passport_number,
                   citizenship,
                   passport_issue_date,
                   passport_expired_date,
                   first_name,
                   last_name,
                   father_name,
                   birth_date,
                   male
            FROM tickets_application.passport 
            """;
    private final ConnectionHelper connectionHelper;
    private final PassportResultSetMapper resultSetMapper;

    public PassportRepositoryImpl(ConnectionHelper connectionHelper, PassportResultSetMapper resultSetMapper) {
        this.connectionHelper = connectionHelper;
        this.resultSetMapper = resultSetMapper;
    }

    @Override
    public Passport create(Passport passport) {
        var sql = """
                INSERT INTO tickets_application.passport(
                passport_series,
                passport_number,
                citizenship,
                passport_issue_date,
                passport_expired_date,
                first_name,
                last_name,
                father_name,
                birth_date,
                male)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id;
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(preparedStatement, passport);

            var resultSet = preparedStatement.executeQuery();

            resultSet.next();
            passport.setId(resultSet.getLong(1));

            return passport;
        } catch (SQLException e) {
            throw new RepositoryException("Passport saving error", e);
        }
    }

    @Override
    public Optional<Passport> findById(Long id) {
        var sql = SELECT_QUERY + "WHERE id = ?";

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Passport find by id error", e);
        }
    }

    @Override
    public List<Passport> findAll() {
        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(SELECT_QUERY)) {

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Passport find all error", e);
        }
    }

    @Override
    public Passport update(Passport passport) {
        var sql = """
                UPDATE tickets_application.passport SET
                passport_series = ?,
                passport_number = ?,
                citizenship = ?,
                passport_issue_date = ?,
                passport_expired_date = ?,
                first_name = ?,
                last_name = ?,
                father_name = ?,
                birth_date = ?,
                male = ?
                WHERE id = ?
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(preparedStatement, passport);
            preparedStatement.setLong(11, passport.getId());

            preparedStatement.executeUpdate();

            return passport;
        } catch (SQLException e) {
            throw new RepositoryException("Passport update error", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        var sql = "DELETE FROM tickets_application.passport WHERE id = ?";

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Passport delete error", e);
        }
    }

    @Override
    public Optional<Passport> findBySeriesAndNumberAndCitizenship(String series, String number, String citizenship) {
        var sql = SELECT_QUERY + """
                WHERE passport_series = ?
                AND passport_number = ?
                AND citizenship = ?
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, series);
            preparedStatement.setString(2, number);
            preparedStatement.setString(3, citizenship);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Passport find by series and number and citizenship error", e);
        }
    }

    @Override
    public Optional<Passport> findByPassengerId(Long passengerId) {
        var sql = SELECT_QUERY + """
                JOIN tickets_application.passenger ON passenger.passport_id = passport.id
                WHERE passenger.id = ?
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, passengerId);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Passport find by passenger id error", e);
        }
    }

    private void setStatementFields(PreparedStatement preparedStatement, Passport passport) throws SQLException {
        preparedStatement.setString(1, passport.getSeries());
        preparedStatement.setString(2, passport.getNumber());
        preparedStatement.setString(3, passport.getCitizenship());

        if (passport.getIssueDate() != null) {
            preparedStatement.setDate(4, Date.valueOf(passport.getIssueDate()));
        } else {
            preparedStatement.setDate(4, null);
        }

        if (passport.getExpiredDate() != null) {
            preparedStatement.setDate(5, Date.valueOf(passport.getExpiredDate()));
        } else {
            preparedStatement.setDate(5, null);
        }

        preparedStatement.setString(6, passport.getFirstName());
        preparedStatement.setString(7, passport.getLastName());
        preparedStatement.setString(8, passport.getFatherName());

        if (passport.getBirthDate() != null) {
            preparedStatement.setDate(9, Date.valueOf(passport.getBirthDate()));
        } else {
            preparedStatement.setDate(9, null);
        }

        preparedStatement.setBoolean(10, passport.isMale());
    }
}
