package repository.impl;

import domain.User;
import exception.RepositoryException;
import mapper.UserResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.SessionHelper;
import repository.UserRepository;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);
    private static final String SELECT_QUERY = "SELECT * FROM tickets_application.users";
    private final SessionHelper connectionHelper;
    private final UserResultSetMapper resultSetMapper;

    public UserRepositoryImpl(SessionHelper connectionHelper, UserResultSetMapper resultSetMapper) {
        this.connectionHelper = connectionHelper;
        this.resultSetMapper = resultSetMapper;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        var sql = SELECT_QUERY + " WHERE email = ?";

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            log.error("Select by email {} error", email, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public User save(User user) {
        var sql = """
                INSERT INTO tickets_application.users (email, password_hash, first_name, last_name, father_name,
                user_role, is_blocked)
                VALUES(?, ?, ?, ?, ?, ?, ?)
                RETURNING id;
                """;

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(user, preparedStatement);

            var resultSet = preparedStatement.executeQuery();

            resultSet.next();
            user.setId(resultSet.getLong(1));

            return user;
        } catch (SQLException e) {
            log.error("Save user with email {} error", user.getEmail(), e);
            throw new RepositoryException(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        var sql = SELECT_QUERY + " WHERE id = ?";
        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            log.error("Select by id {} error", id, e);
            throw new RepositoryException("User find by id error");
        }
    }

    @Override
    public List<User> findAll() {
        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            log.error("Find all users error", e);
            throw new RepositoryException("User find all error");
        }
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

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(user, preparedStatement);
            if (user.getLastLogin() != null) {
                preparedStatement.setTimestamp(8, Timestamp.valueOf(user.getLastLogin()));
            } else {
                preparedStatement.setNull(8, Types.TIMESTAMP);
            }
            preparedStatement.setLong(9, user.getId());

            preparedStatement.executeUpdate();

            return user;
        } catch (SQLException e) {
            log.error("User with email {} update error", user.getEmail(), e);
            throw new RepositoryException("User update error");
        }
    }

    @Override
    public void deleteById(Long id) {
        var sql = "DELETE FROM tickets_application.users WHERE id = ?";

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Delete user with id {} error", id, e);
            throw new RepositoryException("User delete error");
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
