package service;

import domain.Passenger;
import exception.EntityNotFoundException;
import repository.FavoriteAirportsRepository;
import repository.PassengerRepository;
import util.CurrentUserHolder;
import util.TransactionHelper;

import java.util.List;

public class PassengerService {
    private final PassengerRepository passengerRepository;
    private final FavoriteAirportsRepository favoriteAirportsRepository;
    private final AirportService airportService;
    private final PassportService passportService;
    private final TransactionHelper transactionHelper;

    public PassengerService(PassengerRepository passengerRepository,
                            FavoriteAirportsRepository favoriteAirportsRepository, AirportService airportService,
                            PassportService passportService, TransactionHelper transactionHelper) {
        this.passengerRepository = passengerRepository;
        this.favoriteAirportsRepository = favoriteAirportsRepository;
        this.airportService = airportService;
        this.passportService = passportService;
        this.transactionHelper = transactionHelper;
    }

    public Passenger create(Passenger passenger) {
        return transactionHelper.executeInTransaction(() -> {
            var createdPassport = passportService.create(passenger.getPassport());
            passenger.setPassport(createdPassport);

            return passengerRepository.create(passenger);
        });
    }

    public List<Passenger> findAllByUserId(Long userId) {
        return passengerRepository.findAllByUserId(userId);
    }

    public Passenger findById(Long id) {
        return passengerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Passenger not found. ID: %d".formatted(id)));
    }

    public List<Passenger> findAll() {
        return passengerRepository.findAll();
    }

    public Passenger update(Passenger passenger) {
        return transactionHelper.executeInTransaction(() -> {
            var existing = findByIdAndUserId(passenger.getId(), CurrentUserHolder.getCurrentUserId());

            var updatedPassport = passportService.update(passenger.getPassport(), passenger.getId());

            existing.setPassport(updatedPassport);

            return existing;
        });
    }

    public void delete(Long id) {
        transactionHelper.executeInTransaction(() -> {
            var passenger = findByIdAndUserId(id, CurrentUserHolder.getCurrentUserId());

            passportService.deleteById(passenger.getPassport().getId());
            passengerRepository.deleteById(id);
        });
    }

    public void addFavoriteAirport(Long passengerId, String code) {
        transactionHelper.executeInTransaction(() -> {
            var airport = airportService.findById(code);

            findByIdAndUserId(passengerId, CurrentUserHolder.getCurrentUserId());

            favoriteAirportsRepository.addFavorite(passengerId, airport.getCode());
        });
    }

    public void removeFavoriteAirport(Long passengerId, String airportCode) {
        findByIdAndUserId(passengerId, CurrentUserHolder.getCurrentUserId());

        favoriteAirportsRepository.removeFavorite(passengerId, airportCode);
    }

    public Passenger findByIdAndUserId(Long id, Long userId) {
        return passengerRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Passenger not found or unavailable. ID: %d"
                        .formatted(id)));
    }
}
