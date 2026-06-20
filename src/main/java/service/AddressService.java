package service;

import domain.Address;
import exception.EntityAlreadyExistException;
import exception.EntityNotFoundException;
import repository.AddressRepository;
import util.TransactionHelper;

public class AddressService {
    private final TransactionHelper transactionHelper;
    private final AddressRepository addressRepository;

    public AddressService(TransactionHelper transactionHelper, AddressRepository addressRepository) {
        this.transactionHelper = transactionHelper;
        this.addressRepository = addressRepository;
    }

    public Address create(Address address) {
        return transactionHelper.executeInTransaction(() -> {
            var existingAddress = addressRepository.findByCountryAndCityAndStreetAndHouseNumber(address);
            if (existingAddress.isPresent()) {
                var existing = existingAddress.get();
                throw new EntityAlreadyExistException("There is already an airport at address %s %s %s %s"
                        .formatted(existing.getCountry(), existing.getCity(), existing.getStreet(), existing.getHouseNumber()));
            }

            return addressRepository.create(address);
        });
    }

    public Address update(Address address) {
        return transactionHelper.executeInTransaction(() -> {
            var existing = addressRepository.findById(address.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Address not found. Id: %s"
                            .formatted(address.getId())));

            existing.setCountry(address.getCountry());
            existing.setCity(address.getCity());
            existing.setStreet(address.getStreet());
            existing.setHouseNumber(address.getHouseNumber());

            return addressRepository.update(existing);
        });
    }
}
