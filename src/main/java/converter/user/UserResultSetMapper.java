package converter.user;

import domain.Role;
import domain.User;
import mapper.ResultSetListMapper;
import mapper.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserResultSetMapper implements ResultSetMapper<Optional<User>>, ResultSetListMapper<User> {

    @Override
    public Optional<User> map(ResultSet resultSet) throws SQLException {
        return resultSet.next() ? mapRow(resultSet) : Optional.empty();
    }

    @Override
    public List<User> mapList(ResultSet resultSet) throws SQLException {
        var users = new ArrayList<User>();

        while (resultSet.next()) {
            mapRow(resultSet).ifPresent(users::add);
        }

        return users;
    }

    private Optional<User> mapRow(ResultSet resultSet) throws SQLException {
        var user = new User();

        user.setId(resultSet.getLong("id"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setFirstName(resultSet.getString("first_name"));
        user.setLastName(resultSet.getString("last_name"));
        user.setFatherName(resultSet.getString("father_name"));
        user.setRole(Role.valueOf(resultSet.getString("user_role")));
        user.setLastLogin(resultSet.getObject("last_login", LocalDateTime.class));
        user.setBlocked(resultSet.getBoolean("is_blocked"));

        return Optional.of(user);
    }

}
