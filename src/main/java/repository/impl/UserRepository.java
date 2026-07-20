package repository.impl;

import domain.User;
import exception.ApplicationException;
import mapper.UserResultSetMapper;
import repository.Repository;
import util.ConnectionHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class UserRepository implements Repository<User, Long> {
    private static final String SELECT_QUERY = "SELECT * FROM tickets_application.users";

    private final ConnectionHolder connectionHolder;
    private final UserResultSetMapper resultSetMapper;

    public UserRepository(ConnectionHolder connectionHolder, UserResultSetMapper resultSetMapper) {
        this.connectionHolder = connectionHolder;
        this.resultSetMapper = resultSetMapper;
    }

    public Optional<User> findByEmail(String email) {
        var sql = SELECT_QUERY + " WHERE email = ?";

        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new ApplicationException("User find by email error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public User create(User user) {
        var sql = """
                INSERT INTO tickets_application.users (
                email,
                password_hash,
                role,
                is_blocked)
                VALUES(?, ?, ?, ?)
                RETURNING id;
                """;

        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(user, preparedStatement);

            var resultSet = preparedStatement.executeQuery();

            resultSet.next();
            user.setId(resultSet.getLong(1));

            return user;
        } catch (SQLException e) {
            throw new ApplicationException("User creating error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        var sql = SELECT_QUERY + " WHERE id = ?";
        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new ApplicationException("User find by id error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<User> findAll() {
        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            throw new ApplicationException("User find all error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public User update(User user) {
        var sql = """
                UPDATE tickets_application.users
                SET email = ?,
                    password_hash = ?,
                    role = ?,
                    is_blocked = ?,
                    last_login = ?
                WHERE id = ?
                """;

        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(user, preparedStatement);

            if (user.getLastLogin() != null) {
                preparedStatement.setTimestamp(5, Timestamp.valueOf(user.getLastLogin()));
            } else {
                preparedStatement.setNull(5, Types.TIMESTAMP);
            }

            preparedStatement.setLong(6, user.getId());

            preparedStatement.executeUpdate();

            return user;
        } catch (SQLException e) {
            throw new ApplicationException("User update error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteById(Long id) {
        var sql = "DELETE FROM tickets_application.users WHERE id = ?";

        var connection = connectionHolder.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ApplicationException("User delete error", e, SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void setStatementFields(User user, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, user.getEmail());
        preparedStatement.setString(2, user.getPasswordHash());
        preparedStatement.setString(3, user.getRole().name());
        preparedStatement.setBoolean(4, user.isBlocked());
    }
}
