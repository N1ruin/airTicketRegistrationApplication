package service;

import domain.Airport;
import domain.AirportStatus;
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
        return transactionHelper.executeInTransaction(() -> airportRepository.findByCode(airport.getCode())
                .map(existed -> {
                    if (existed.getAirportStatus() == AirportStatus.WORKS) {
                        throw new EntityAlreadyExistException("Airport with code %s already exist"
                                .formatted(airport.getCode()));
                    }
                    existed.setAirportStatus(AirportStatus.WORKS);
                    airportRepository.update(existed);

                    return existed;
                }).orElseGet(() -> airportRepository.save(airport)));
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
            var existed = airportRepository.findById(airport.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Airport with id %d not found"
                            .formatted(airport.getId())));

            if (airport.getCode() != null) {
                existed.setCode(airport.getCode());
            }
            if (airport.getName() != null) {
                existed.setName(airport.getName());
            }
            if (airport.getAddress() != null) {
                var updatedAddress = addressService.update(airport.getAddress(), airport.getId());
                existed.setAddress(updatedAddress);
            }

            return airportRepository.update(existed);
        });
    }

    private void deleteTransactional(Long id) {
        transactionHelper.executeInTransaction(() -> {
            var airport = airportRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Airport with id %d not found".formatted(id)));

            if (airport.getAirportStatus() == AirportStatus.CLOSED) {
                return;
            }

            airportRepository.deleteById(id);
        });
    }

    private Airport findByCodeTransactional(String code) {
        return transactionHelper.executeInTransaction(() -> airportRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Airport with code %s not found".formatted(code))));
    }
}
