package repository.impl;

import domain.User;
import mapper.user.UserResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.ConnectionHelper;
import repository.UserRepository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);
    private final ConnectionHelper connectionHelper;
    private final UserResultSetMapper resultSetMapper;

    public UserRepositoryImpl(ConnectionHelper connectionHelper, UserResultSetMapper resultSetMapper) {
        this.connectionHelper = connectionHelper;
        this.resultSetMapper = resultSetMapper;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        var sql = " SELECT * FROM tickets_application.users WHERE email = ? ";

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            var rs = preparedStatement.executeQuery();

            return resultSetMapper.map(rs);
        } catch (SQLException e) {
            log.error("SQL error with select by email {}", email, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public User save(User user) {
        var sql = """
                INSERT INTO tickets_application.users (email, password_hash, first_name, last_name, father_name,
                user_role, is_blocked)
                VALUES(?, ?, ?, ?, ?, ?, ?)
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setStatementFields(user, preparedStatement);

            preparedStatement.executeUpdate();
            var generatedId = preparedStatement.getGeneratedKeys();

            extractId(user, generatedId);

        } catch (SQLException e) {
            log.error("SQL error with save user with email {}", user.getEmail(), e);
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public User update(User user) {
        var sql = """
                UPDATE tickets_application.users
                SET email = ?,
                    password_hash = ?,
                    first_name = ?,
                    last_name = ?,
                    father_name = ?,
                    user_role = ?,
                    is_blocked = ?,
                    last_login = ?
                WHERE id = ?
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(user, preparedStatement);
            preparedStatement.setTimestamp(8, Timestamp.valueOf(user.getLastLogin()));
            preparedStatement.setLong(9, user.getId());
            preparedStatement.executeUpdate();

            return user;
        } catch (SQLException e) {
            log.error("SQL error with update user with email {}", user.getEmail(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(long id) {
        var sql = "DELETE FROM tickets_application.users WHERE id = ?";

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("SQL error with delete user with id {}", id, e);
            throw new RuntimeException(e);
        }
    }

    private void extractId(User user, ResultSet rs) throws SQLException {
        if (rs.next()) {
            user.setId(rs.getLong(1));
        }
    }

    private void setStatementFields(User user, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, user.getEmail());
        preparedStatement.setString(2, user.getPasswordHash());
        preparedStatement.setString(3, user.getFirstName());
        preparedStatement.setString(4, user.getLastName());
        preparedStatement.setString(5, user.getFatherName());
        preparedStatement.setString(6, user.getRole().name());
        preparedStatement.setBoolean(7, user.isBlocked());
    }
}
