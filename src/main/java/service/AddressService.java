package service;

import domain.Address;
import exception.EntityAlreadyExistException;
import exception.EntityNotFoundException;
import repository.AddressRepository;
import repository.TransactionHelper;

public class AddressService {
    private final TransactionHelper transactionHelper;
    private final AddressRepository addressRepository;

    public AddressService(TransactionHelper transactionHelper, AddressRepository addressRepository) {
        this.transactionHelper = transactionHelper;
        this.addressRepository = addressRepository;
    }

    public Address save(Address address) {
        return saveTransactional(address);
    }

    private Address saveTransactional(Address address) {
        return transactionHelper.executeInTransaction(() -> {
            var existingAddress = addressRepository.findByCountryAndCityAndStreetAndHouseNumber(address);
            if (existingAddress.isPresent()) {
                throw new EntityAlreadyExistException("There is already an airport at address");
            }

            return addressRepository.save(address);
        });
    }

    public Address update(Address address, Long airportId) {
        return updateTransactional(address, airportId);
    }

    private Address updateTransactional(Address address, Long airportId) {
        return transactionHelper.executeInTransaction(() -> {
            var existing = addressRepository.findByAirportId(airportId)
                    .orElseThrow(() -> new EntityNotFoundException("Address with airport id %d not found"
                            .formatted(airportId)));

            existing.setCountry(address.getCountry());
            existing.setCity(address.getCity());
            existing.setStreet(address.getStreet());
            existing.setHouseNumber(address.getHouseNumber());

            return addressRepository.update(existing);
        });
    }
}
