package repository;

import domain.Address;

import java.util.Optional;

public interface AddressRepository extends Repository<Address, Long> {
    Optional<Address> findByCountryAndCityAndStreetAndHouseNumber(Address address);
}
