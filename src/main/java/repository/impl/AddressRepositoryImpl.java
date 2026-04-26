package repository.impl;

import domain.Address;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.AddressRepository;
import repository.SessionHelper;

import java.util.List;
import java.util.Optional;

public class AddressRepositoryImpl implements AddressRepository {
    public static final Logger log = LogManager.getLogger(AddressRepositoryImpl.class);
    private final SessionHelper sessionHelper;

    public AddressRepositoryImpl(SessionHelper connectionHelper) {
        this.sessionHelper = connectionHelper;
    }

    @Override
    public Address save(Address address) {
        var session = sessionHelper.getSession();
        session.persist(address);

        return address;
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
        var session = sessionHelper.getSession();

        return session.merge(address);
    }

    @Override
    public void deleteById(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Address> findByCountryAndCityAndStreetAndHouseNumber(Address address) {
        var hql = """
                FROM Address a
                WHERE a.country = ?1
                AND a.city = ?2
                AND a.street = ?3
                AND a.houseNumber = ?4
                """;

        var session = sessionHelper.getSession();

        return session.createQuery(hql, Address.class)
                .setParameter(1, address.getCountry())
                .setParameter(2, address.getCity())
                .setParameter(3, address.getStreet())
                .setParameter(4, address.getHouseNumber())
                .uniqueResultOptional();
    }

    @Override
    public Optional<Address> findByAirportId(Long airportId) {
        var hql = """
                SELECT airport.address
                FROM Airport airport
                WHERE airport.id = ?1
                """;

        var session = sessionHelper.getSession();
        return session.createQuery(hql, Address.class)
                .setParameter(1, airportId)
                .uniqueResultOptional();
    }
}
