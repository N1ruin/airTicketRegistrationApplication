package repository;

import domain.Address;

import java.util.Optional;

public interface AddressRepository extends Repository<Address> {
    Optional<Address> findByCountryAndCityAndStreetAndHouseNumber(Address address);

    Optional<Address> findByAirportId(Long airportId);
}
