package mapper;

import domain.Role;
import domain.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserResultSetMapper implements ResultSetMapper<User> {
    @Override
    public Optional<User> mapRow(ResultSet resultSet) throws SQLException {
        var user = new User();

        user.setId(resultSet.getLong("id"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setRole(Role.valueOf(resultSet.getString("role")));
        user.setLastLogin(resultSet.getObject("last_login", LocalDateTime.class));
        user.setBlocked(resultSet.getBoolean("is_blocked"));

        return Optional.of(user);
    }

}
