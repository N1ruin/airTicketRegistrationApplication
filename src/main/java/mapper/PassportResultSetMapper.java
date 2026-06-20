package mapper;

import domain.Passport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class PassportResultSetMapper implements ResultSetMapper<Passport> {
    @Override
    public Optional<Passport> mapRow(ResultSet resultSet) throws SQLException {
        var passport = new Passport();

        passport.setId(resultSet.getLong("passport_id"));
        passport.setSeries(resultSet.getString("passport_series"));
        passport.setNumber(resultSet.getString("passport_number"));
        passport.setCitizenship(resultSet.getString("passport_citizenship"));
        passport.setIssueDate((resultSet.getObject("passport_issue_date", LocalDate.class)));
        passport.setExpiredDate(resultSet.getObject("passport_expired_date", LocalDate.class));
        passport.setFirstName(resultSet.getString("first_name"));
        passport.setLastName(resultSet.getString("last_name"));
        passport.setFatherName(resultSet.getString("father_name"));
        passport.setBirthDate(resultSet.getObject("birth_date", LocalDate.class));
        passport.setMale(resultSet.getBoolean("male"));

        return Optional.of(passport);
    }
}
