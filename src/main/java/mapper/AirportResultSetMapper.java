package mapper;

import domain.Airport;
import exception.MappingException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AirportResultSetMapper implements ResultSetMapper<Airport> {
    private final AddressResultSetMapper addressResultSetMapper;

    public AirportResultSetMapper(AddressResultSetMapper addressResultSetMapper) {
        this.addressResultSetMapper = addressResultSetMapper;
    }

    @Override
    public Optional<Airport> mapRow(ResultSet resultSet) throws SQLException {
        var airport = new Airport();

        airport.setCode(resultSet.getString("airport_code"));
        airport.setName(resultSet.getString("airport_name"));
        airport.setWorked(resultSet.getBoolean("airport_worked"));

        var address = addressResultSetMapper.map(resultSet)
                .orElseThrow(() -> new MappingException("Address mapping error for airport: " + airport.getCode()));

        airport.setAddress(address);

        return Optional.of(airport);
    }
}
