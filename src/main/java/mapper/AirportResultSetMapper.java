package mapper;

import domain.Airport;
import exception.MappingException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AirportResultSetMapper implements ResultSetMapper<Optional<Airport>>, ResultSetListMapper<Airport> {
    private final AddressResultSetMapper addressResultSetMapper;

    public AirportResultSetMapper(AddressResultSetMapper addressResultSetMapper) {
        this.addressResultSetMapper = addressResultSetMapper;
    }

    @Override
    public Optional<Airport> map(ResultSet resultSet) throws SQLException {
        return resultSet.next() ? mapRow(resultSet) : Optional.empty();
    }

    private Optional<Airport> mapRow(ResultSet resultSet) throws SQLException {
        var airport = new Airport();

        airport.setId(resultSet.getLong("airport_id"));
        airport.setCode(resultSet.getString("airport_code"));
        airport.setName(resultSet.getString("airport_name"));

        var address = addressResultSetMapper.map(resultSet)
                .orElseThrow(() -> new MappingException("Address mapping error"));

        airport.setAddress(address);

        return Optional.of(airport);
    }

    @Override
    public List<Airport> mapList(ResultSet resultSet) throws SQLException {
        var airports = new ArrayList<Airport>();

        while (resultSet.next()) {
            mapRow(resultSet).ifPresent(airports::add);
        }

        return airports;
    }
}
