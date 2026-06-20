package mapper;

import domain.Passenger;
import exception.MappingException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class PassengerResultSetMapper implements ResultSetMapper<Passenger> {
    private final PassportResultSetMapper passportResultSetMapper;

    public PassengerResultSetMapper(PassportResultSetMapper passportResultSetMapper) {
        this.passportResultSetMapper = passportResultSetMapper;
    }

    @Override
    public Optional<Passenger> mapRow(ResultSet resultSet) throws SQLException {
        var passenger = new Passenger();

        passenger.setId(resultSet.getLong("passenger_id"));
        passenger.setUserId(resultSet.getLong("user_id"));

        var passport = passportResultSetMapper.mapRow(resultSet)
                .orElseThrow(() -> new MappingException("Passport mapping error for passenger: " + passenger.getId()));

        passenger.setPassport(passport);

        return Optional.of(passenger);
    }

}
