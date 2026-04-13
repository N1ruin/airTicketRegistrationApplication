package service;

import domain.Address;
import exception.EntityAlreadyExistException;
import repository.AddressRepository;
import repository.TransactionHelper;

public class AddressService {
    private final TransactionHelper transactionHelper;
    private final AddressRepository addressRepository;

    public AddressService(TransactionHelper transactionHelper, AddressRepository addressRepository) {
        this.transactionHelper = transactionHelper;
        this.addressRepository = addressRepository;
    }

    public void save(Address address) {
        saveTransactional(address);
    }

    private void saveTransactional(Address address) {
        transactionHelper.executeInTransaction(() -> {
            var existingAddress = addressRepository.findByCountryAndCityAndStreetAndHouseNumber(address);
            if (existingAddress.isPresent()) {
                throw new EntityAlreadyExistException("There is already an airport at address");
            }

            addressRepository.save(address);
        });
    }

    public void update(Address address) {
        transactionHelper.executeInTransaction(() -> {

            addressRepository.update(address);
        });
    }

    public void deleteById(Long id) {
        transactionHelper.executeInTransaction(() -> addressRepository.deleteById(id));
    }
}
