package repository.impl;

import domain.Address;
import exception.RepositoryException;
import mapper.AddressResultSetMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.AddressRepository;
import repository.SessionHelper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AddressRepositoryImpl implements AddressRepository {
    public static final Logger log = LogManager.getLogger(AddressRepositoryImpl.class);
    private final SessionHelper connectionHelper;
    private final AddressResultSetMapper addressResultSetMapper;

    public AddressRepositoryImpl(SessionHelper connectionHelper, AddressResultSetMapper addressResultSetMapper) {
        this.connectionHelper = connectionHelper;
        this.addressResultSetMapper = addressResultSetMapper;
    }

    @Override
    public Address save(Address address) {
        var sql = """
                INSERT INTO tickets_application.address(country, city, street, house_number)
                VALUES (?, ?, ?, ?)
                RETURNING id;
                """;

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(preparedStatement, address);

            var resultSet = preparedStatement.executeQuery();

            resultSet.next();
            address.setId(resultSet.getLong(1));

            return address;
        } catch (SQLException e) {
            log.error(e);
            throw new RepositoryException("Address saving error");
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

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(preparedStatement, address);
            preparedStatement.setLong(5, address.getId());

            preparedStatement.executeUpdate();

            return address;
        } catch (SQLException e) {
            log.error(e);
            throw new RepositoryException("Address update error");
        }
    }

    @Override
    public void deleteById(Long id) {
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

        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(preparedStatement, address);

            var resultSet = preparedStatement.executeQuery();

            return resultSet.next() ? Optional.of(address) : Optional.empty();
        } catch (SQLException e) {
            log.error(e);
            throw new RepositoryException("Address delete error");
        }
    }

    @Override
    public Optional<Address> findByAirportId(Long airportId) {
        var sql = """
                SELECT address.id AS address_id,
                address.country AS address_country,
                address.city AS address_city,
                address.street AS address_street,
                address.house_number AS address_house_number
                FROM tickets_application.address AS address
                JOIN tickets_application.airport AS airport ON address.id = airport.address_id
                WHERE airport.id = ?;
                """;
        var connection = connectionHelper.getSession();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, airportId);

            var resultSet = preparedStatement.executeQuery();

            return resultSet.next() ? addressResultSetMapper.map(resultSet) : Optional.empty();
        } catch (SQLException e) {
            log.error(e);
            throw new RepositoryException("Address delete error");
        }
    }

    private void setStatementFields(PreparedStatement preparedStatement, Address address) throws SQLException {
        preparedStatement.setString(1, address.getCountry());
        preparedStatement.setString(2, address.getCity());
        preparedStatement.setString(3, address.getStreet());
        preparedStatement.setObject(4, address.getHouseNumber());
    }
}
