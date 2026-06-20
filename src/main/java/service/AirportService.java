package service;

import domain.Airport;
import exception.EntityAlreadyExistException;
import exception.EntityNotFoundException;
import repository.AirportRepository;
import util.TransactionHelper;

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

    public Airport create(Airport airport) {
        return transactionHelper.executeInTransaction(() -> {
            var existing = airportRepository.findById(airport.getCode());
            if (existing.isPresent()) {
                throw new EntityAlreadyExistException("Airport already exist. Code: %s"
                        .formatted(airport.getCode()));
            }

            addressService.create(airport.getAddress());

            return airportRepository.create(airport);
        });
    }

    public List<Airport> findAll() {
        return airportRepository.findAll();
    }

    public Airport findById(String code) {
        return airportRepository.findById(code)
                .orElseThrow(() -> new EntityNotFoundException("Airport not found. Code: %s".formatted(code)));
    }

    public Airport update(Airport airport) {
        return transactionHelper.executeInTransaction(() -> {
            var existingAirport = getAirportByCode(airport.getCode());

            var updatedAddress = addressService.update(airport.getAddress());
            existingAirport.setName(airport.getName());
            existingAirport.setWorked(airport.isWorked());
            existingAirport.setAddress(updatedAddress);

            return airportRepository.update(existingAirport);
        });
    }

    private Airport getAirportByCode(String code) {
        return airportRepository.findById(code)
                .orElseThrow(() -> new EntityNotFoundException("Airport not found. Code: %s".formatted(code)));
    }
}
