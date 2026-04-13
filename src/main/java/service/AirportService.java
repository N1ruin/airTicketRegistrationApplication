package service;

import domain.Airport;
import exception.EntityAlreadyExistException;
import exception.EntityNotFoundException;
import repository.AirportRepository;
import repository.TransactionHelper;

import java.util.List;

public class AirportService {
    private final AddressService addressService;
    private final AirportRepository airportRepository;
    private final TransactionHelper transactionHelper;

    public AirportService(AddressService addressService, AirportRepository airportRepository,
                          TransactionHelper transactionHelper) {
        this.addressService = addressService;
        this.airportRepository = airportRepository;
        this.transactionHelper = transactionHelper;
    }

    public Airport save(Airport airport) {
        return saveTransactional(airport);
    }

    public Airport findById(Long id) {
        return findByIdTransactional(id);
    }

    public List<Airport> findAll() {
        return findAllTransactional();
    }

    public Airport findByCode(String code) {
        return findByCodeTransactional(code);
    }

    public Airport update(Airport airport) {
        return updateTransactional(airport);
    }

    public void delete(Long id) {
        deleteTransactional(id);
    }

    private Airport saveTransactional(Airport airport) {
        return transactionHelper.executeInTransaction(() -> {
            airportRepository.findByCode(airport.getCode())
                    .ifPresent(a -> {
                        throw new EntityAlreadyExistException("Airport with code %s already exist"
                                .formatted(airport.getCode()));
                    });

            addressService.save(airport.getAddress());
            return airportRepository.save(airport);
        });
    }

    private List<Airport> findAllTransactional() {
        return transactionHelper.executeInTransaction(airportRepository::findAll);
    }

    private Airport findByIdTransactional(Long id) {
        return transactionHelper.executeInTransaction(() -> airportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Airport with id %d not found".formatted(id))));
    }

    private Airport updateTransactional(Airport airport) {
        return transactionHelper.executeInTransaction(() -> {
            airportRepository.findById(airport.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Airport with id %d not found"
                            .formatted(airport.getId())));

            addressService.update(airport.getAddress());
            return airportRepository.update(airport);
        });
    }

    private void deleteTransactional(Long id) {
        transactionHelper.executeInTransaction(() -> {
            var airport = airportRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Airport with id %d not found".formatted(id)));

            addressService.deleteById(airport.getAddress().getId());
            airportRepository.deleteById(id);
        });
    }

    private Airport findByCodeTransactional(String code) {
        return transactionHelper.executeInTransaction(() -> airportRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Airport with code %s not found".formatted(code))));
    }
}
