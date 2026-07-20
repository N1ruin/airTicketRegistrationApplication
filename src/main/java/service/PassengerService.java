package service;

import domain.Passenger;
import exception.ApplicationException;
import repository.impl.FavoriteAirportsRepository;
import repository.impl.PassengerRepository;
import util.CurrentUserHolder;
import util.TransactionHelper;

import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public class PassengerService {
    private final PassengerRepository passengerRepository;
    private final FavoriteAirportsRepository favoriteAirportsRepository;
    private final AirportService airportService;
    private final TransactionHelper transactionHelper;

    public PassengerService(PassengerRepository passengerRepository,
                            FavoriteAirportsRepository favoriteAirportsRepository, AirportService airportService,
                            TransactionHelper transactionHelper) {
        this.passengerRepository = passengerRepository;
        this.favoriteAirportsRepository = favoriteAirportsRepository;
        this.airportService = airportService;
        this.transactionHelper = transactionHelper;
    }

    public Passenger create(Passenger passenger) {
        return transactionHelper.executeInTransaction(() -> passengerRepository.create(passenger));
    }

    public List<Passenger> findAllByUserId(Long userId) {
        return passengerRepository.findAllByUserId(userId);
    }

    public Passenger findById(Long id) {
        return passengerRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Passenger not found. ID: %d".formatted(id), SC_NOT_FOUND));
    }

    public List<Passenger> findAll() {
        return passengerRepository.findAll();
    }

    public Passenger update(Passenger passenger) {
        return transactionHelper.executeInTransaction(() -> {
            var existing = findByIdAndUserId(passenger.getId(), CurrentUserHolder.getCurrentUserId());

            updatePassengerData(existing, passenger);

            return existing;
        });
    }

    public void delete(Long id) {
        transactionHelper.executeInTransaction(() -> {
            var existing = passengerRepository.findById(id);

            if (existing.isEmpty()) {
                throw new ApplicationException("Passenger not found. ID: %d".formatted(id), SC_NOT_FOUND);
            }

            passengerRepository.deleteById(id);
        });
    }

    public void addFavoriteAirport(Long passengerId, String code) {
        transactionHelper.executeInTransaction(() -> {
            var airport = airportService.findById(code);

            findByIdAndUserId(passengerId, CurrentUserHolder.getCurrentUserId());

            favoriteAirportsRepository.addFavorite(passengerId, airport.getId());
        });
    }

    public void removeFavoriteAirport(Long passengerId, String airportCode) {
        findByIdAndUserId(passengerId, CurrentUserHolder.getCurrentUserId());

        favoriteAirportsRepository.removeFavorite(passengerId, airportCode);
    }

    public Passenger findByIdAndUserId(Long id, Long userId) {
        return passengerRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ApplicationException("Passenger not found or unavailable. ID: %d"
                        .formatted(id), SC_NOT_FOUND));
    }

    public void updatePassengerData(Passenger existing, Passenger newPassengerData) {
        existing.setPassportSeries(newPassengerData.getPassportSeries());
        existing.setPassportNumber(newPassengerData.getPassportNumber());
        existing.setCitizenship(newPassengerData.getCitizenship());
        existing.setFirstName(newPassengerData.getFirstName());
        existing.setLastName(newPassengerData.getLastName());
        existing.setFatherName(newPassengerData.getFatherName());
        existing.setBirthDate(newPassengerData.getBirthDate());
        existing.setMale(newPassengerData.isMale());
        existing.setPassportIssueDate(newPassengerData.getPassportIssueDate());
        existing.setPassportExpiredDate(newPassengerData.getPassportExpiredDate());
    }
}
