package mapper;

import domain.Airport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AirportResultSetMapper implements ResultSetMapper<Airport> {
    @Override
    public Optional<Airport> mapRow(ResultSet resultSet) throws SQLException {
        var airport = new Airport();

        airport.setId(resultSet.getString("id"));
        airport.setName(resultSet.getString("name"));
        airport.setActive(resultSet.getBoolean("is_active"));
        airport.setCountry(resultSet.getString("country"));
        airport.setCity(resultSet.getString("city"));
        airport.setStreet(resultSet.getString("street"));
        airport.setHouseNumber(resultSet.getString("house_number"));

        return Optional.of(airport);
    }
}
