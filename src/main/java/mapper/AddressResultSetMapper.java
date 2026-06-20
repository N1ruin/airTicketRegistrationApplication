package mapper;

import domain.Address;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AddressResultSetMapper implements ResultSetMapper<Address> {
    @Override
    public Optional<Address> mapRow(ResultSet resultSet) throws SQLException {
        var address = new Address();

        address.setId(resultSet.getLong("address_id"));
        address.setCountry(resultSet.getString("address_country"));
        address.setCity(resultSet.getString("address_city"));
        address.setStreet(resultSet.getString("address_street"));
        address.setHouseNumber(resultSet.getInt("address_house_number"));

        return Optional.of(address);
    }
}
