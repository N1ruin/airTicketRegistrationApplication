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

    public Address update(Address address) {
        return updateTransactional(address);
    }

    private Address updateTransactional(Address address) {
        return transactionHelper.executeInTransaction(() -> {
            var existingAddress = addressRepository.findByCountryAndCityAndStreetAndHouseNumber(address);
            if (existingAddress.isPresent()) {
                throw new EntityAlreadyExistException("Address exist");
            }

            return addressRepository.update(address);
        });
    }

    public void deleteById(Long id) {
        deleteTransactional(id);
    }

    private void deleteTransactional(Long id) {
        transactionHelper.executeInTransaction(() -> {
            addressRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Address with id %d not found".formatted(id)));
            addressRepository.deleteById(id);
        });
    }
}
