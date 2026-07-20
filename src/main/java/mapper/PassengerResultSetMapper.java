package mapper;

import domain.Passenger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class PassengerResultSetMapper implements ResultSetMapper<Passenger> {
    @Override
    public Optional<Passenger> mapRow(ResultSet resultSet) throws SQLException {
        var passenger = new Passenger();

        passenger.setId(resultSet.getLong("id"));
        passenger.setPassportSeries(resultSet.getString("passport_series"));
        passenger.setPassportNumber(resultSet.getString("passport_number"));
        passenger.setCitizenship(resultSet.getString("citizenship"));
        passenger.setPassportIssueDate((resultSet.getObject("passport_issue_date", LocalDate.class)));
        passenger.setPassportExpiredDate(resultSet.getObject("passport_expired_date", LocalDate.class));
        passenger.setFirstName(resultSet.getString("first_name"));
        passenger.setLastName(resultSet.getString("last_name"));
        passenger.setFatherName(resultSet.getString("father_name"));
        passenger.setBirthDate(resultSet.getObject("birth_date", LocalDate.class));
        passenger.setMale(resultSet.getBoolean("male"));
        passenger.setUserId(resultSet.getLong("user_id"));

        return Optional.of(passenger);
    }

}
