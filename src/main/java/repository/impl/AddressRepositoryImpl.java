package repository.impl;

import domain.Address;
import repository.AddressRepository;
import repository.ConnectionHelper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AddressRepositoryImpl implements AddressRepository {
    private final ConnectionHelper connectionHelper;

    public AddressRepositoryImpl(ConnectionHelper connectionHelper) {
        this.connectionHelper = connectionHelper;
    }

    @Override
    public Address save(Address address) {
        var sql = """
                INSERT INTO tickets_application.address(country, city, street, house_number)
                VALUES (?, ?, ?, ?)
                RETURNING id;
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(preparedStatement, address);

            var resultSet = preparedStatement.executeQuery();

            resultSet.next();
            address.setId(resultSet.getLong(1));

            return address;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Address> findById(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Address> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Address update(Address address) {
        var sql = """
                UPDATE tickets_application.address SET country = ?, city = ?, street = ?, house_number = ? WHERE id = ?
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(preparedStatement, address);
            preparedStatement.setLong(5, address.getId());

            preparedStatement.executeUpdate();

            return address;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Address> findByCountryAndCityAndStreetAndHouseNumber(Address address) {
        var sql = """
                    SELECT id, country, city, street, house_number FROM tickets_application.address
                    WHERE address.country = ?
                    AND address.city = ?
                    AND address.street = ?
                    AND address.house_number = ?
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(preparedStatement, address);

            var resultSet = preparedStatement.executeQuery();

            return resultSet.next() ? Optional.of(address) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setStatementFields(PreparedStatement preparedStatement, Address address) throws SQLException {
        preparedStatement.setString(1, address.getCountry());
        preparedStatement.setString(2, address.getCity());
        preparedStatement.setString(3, address.getStreet());
        preparedStatement.setObject(4, address.getHouseNumber());
    }
}
