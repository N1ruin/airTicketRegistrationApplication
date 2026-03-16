package mapper.user;

import domain.Role;
import domain.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserResultSetMapper implements ResultSetMapper<Optional<User>> {

    @Override
    public Optional<User> map(ResultSet rs) throws SQLException {
        return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
    }

    private User mapRow(ResultSet rs) throws SQLException {
        var user = new User();

        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setFatherName(rs.getString("father_name"));
        user.setRole(Role.valueOf(rs.getString("user_role")));
        user.setLastLogin(rs.getObject("last_login", LocalDateTime.class));
        user.setBlocked(rs.getBoolean("is_blocked"));

        return user;
    }
}
