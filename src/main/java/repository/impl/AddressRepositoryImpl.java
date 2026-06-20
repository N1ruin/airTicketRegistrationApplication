package repository.impl;

import domain.Address;
import exception.RepositoryException;
import mapper.AddressResultSetMapper;
import repository.AddressRepository;
import util.ConnectionHelper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AddressRepositoryImpl implements AddressRepository {
    private static final String SELECT_QUERY = "SELECT id, country, city, street, house_number FROM tickets_application.address ";
    private final ConnectionHelper connectionHelper;
    private final AddressResultSetMapper resultSetMapper;

    public AddressRepositoryImpl(ConnectionHelper connectionHelper, AddressResultSetMapper resultSetMapper) {
        this.connectionHelper = connectionHelper;
        this.resultSetMapper = resultSetMapper;
    }

    @Override
    public Address create(Address address) {
        var sql = """
                INSERT INTO tickets_application.address(
                country,
                city,
                street,
                house_number)
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
            throw new RepositoryException("Address creation error", e);
        }
    }

    @Override
    public Optional<Address> findById(Long id) {
        var sql = SELECT_QUERY + "WHERE id = ? ";

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Address find by id error", e);
        }
    }

    @Override
    public List<Address> findAll() {
        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.mapList(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Address find all error", e);
        }
    }

    @Override
    public Address update(Address address) {
        var sql = """
                UPDATE tickets_application.address SET
                country = ?,
                city = ?,
                street = ?,
                house_number = ?
                WHERE id = ?
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(preparedStatement, address);
            preparedStatement.setLong(5, address.getId());

            preparedStatement.executeUpdate();

            return address;
        } catch (SQLException e) {
            throw new RepositoryException("Address update error", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        var sql = "DELETE FROM tickets_application.address WHERE id = ?";
        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Address find by id error", e);
        }
    }

    @Override
    public Optional<Address> findByCountryAndCityAndStreetAndHouseNumber(Address address) {
        var sql = SELECT_QUERY + """
                    WHERE address.country = ?
                    AND address.city = ?
                    AND address.street = ?
                    AND address.house_number = ?
                """;

        var connection = connectionHelper.getConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            setStatementFields(preparedStatement, address);

            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException("Address delete error", e);
        }
    }

    private void setStatementFields(PreparedStatement preparedStatement, Address address) throws SQLException {
        preparedStatement.setString(1, address.getCountry());
        preparedStatement.setString(2, address.getCity());
        preparedStatement.setString(3, address.getStreet());
        preparedStatement.setInt(4, address.getHouseNumber());
    }
}
