package repository.impl;

import domain.Passport;
import mapper.PassportResultSetMapper;
import repository.ConnectionHelper;
import repository.PassportRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PassportRepositoryImpl implements PassportRepository {
    private final ConnectionHelper connectionHelper;
    private final PassportResultSetMapper passportResultSetMapper;

    public PassportRepositoryImpl(ConnectionHelper connectionHelper, PassportResultSetMapper passportResultSetMapper) {
        this.connectionHelper = connectionHelper;
        this.passportResultSetMapper = passportResultSetMapper;
    }

    @Override
    public Passport save(Passport passport) {
        var sql = """
                INSERT INTO tickets_application.passport(passport_series, passport_number, citizenship,
                                                         passport_issue_date, passport_expired_date)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id;
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, passport.getSeries());
            preparedStatement.setString(2, passport.getNumber());
            preparedStatement.setString(3, passport.getCitizenship());
            preparedStatement.setObject(4, passport.getIssueDate());
            preparedStatement.setObject(5, passport.getExpiredDate());

            var resultSet = preparedStatement.executeQuery();

            resultSet.next();
            passport.setId(resultSet.getLong(1));

            return passport;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Passport> findById(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Passport> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Passport update(Passport passport) {
        var sql = """
                UPDATE tickets_application.passport
                SET passport_number = ?, passport_series = ?, citizenship = ?, passport_issue_date = ?,
                passport_expired_date = ?
                WHERE id = ?
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, passport.getNumber());
            preparedStatement.setString(2, passport.getSeries());
            preparedStatement.setString(3, passport.getCitizenship());
            preparedStatement.setObject(4, passport.getIssueDate());
            preparedStatement.setObject(5, passport.getExpiredDate());
            preparedStatement.setLong(6, passport.getId());

            preparedStatement.executeUpdate();

            return passport;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Passport> findBySeriesAndNumberAndCitizenship(String series, String number, String citizenship) {
        var sql = """
                SELECT * FROM tickets_application.passport
                WHERE passport_series = ? AND passport_number = ? AND citizenship = ?
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, series);
            preparedStatement.setString(2, number);
            preparedStatement.setObject(3, citizenship);

            var resultSet = preparedStatement.executeQuery();

            return passportResultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
