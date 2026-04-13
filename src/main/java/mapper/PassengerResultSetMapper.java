package mapper;

import domain.Passenger;
import exception.MappingException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PassengerResultSetMapper implements ResultSetMapper<Optional<Passenger>>, ResultSetListMapper<Passenger> {
    private final PassportResultSetMapper passportResultSetMapper;

    public PassengerResultSetMapper(PassportResultSetMapper passportResultSetMapper) {
        this.passportResultSetMapper = passportResultSetMapper;
    }

    @Override
    public Optional<Passenger> map(ResultSet resultSet) throws SQLException {
        return resultSet.next() ? mapRow(resultSet) : Optional.empty();
    }

    @Override
    public List<Passenger> mapList(ResultSet resultSet) throws SQLException {
        var passengers = new ArrayList<Passenger>();

        while (resultSet.next()) {
            mapRow(resultSet).ifPresent(passengers::add);
        }

        return passengers;
    }

    private Optional<Passenger> mapRow(ResultSet resultSet) throws SQLException {
        var passenger = new Passenger();

        passenger.setId(resultSet.getLong("passenger_id"));
        passenger.setFirstName(resultSet.getString("first_name"));
        passenger.setLastName(resultSet.getString("last_name"));
        passenger.setFatherName(resultSet.getString("father_name"));
        passenger.setMale(resultSet.getBoolean("male"));
        passenger.setBirthDate(resultSet.getObject("birth_date", LocalDate.class));
        passenger.setUserId(resultSet.getLong("user_id"));

        var passport = passportResultSetMapper.map(resultSet)
                .orElseThrow(() -> new MappingException("Passport mapping error"));
        passenger.setPassport(passport);

        return Optional.of(passenger);
    }
}
