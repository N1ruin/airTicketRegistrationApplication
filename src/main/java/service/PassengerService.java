package service;

import domain.Passenger;
import exception.EntityAlreadyExistException;
import exception.EntityNotFoundException;
import exception.ValidationException;
import repository.PassengerRepository;
import repository.TransactionHelper;

import java.util.List;
import java.util.Optional;

public class PassengerService {
    private final PassengerRepository passengerRepository;
    private final AirportService airportService;
    private final PassportService passportService;
    private final TransactionHelper transactionHelper;

    public PassengerService(PassengerRepository passengerRepository, AirportService airportService,
                            PassportService passportService, TransactionHelper transactionHelper) {
        this.passengerRepository = passengerRepository;
        this.airportService = airportService;
        this.passportService = passportService;
        this.transactionHelper = transactionHelper;
    }

    public Passenger save(Passenger passenger) {
        return saveTransactional(passenger);
    }

    public List<Passenger> findAllByUserId(Long userId) {
        return findAllByUserIdTransactional(userId);
    }

    public Passenger findById(Long id) {
        return findByIdTransactional(id);
    }

    public List<Passenger> findAll() {
        return findAllTransactional();
    }

    public Passenger update(Passenger passenger, Long currentUserId) {
        return updateTransactional(passenger, currentUserId);
    }

    public void delete(Long id, Long userId) {
        deleteTransactional(id, userId);
    }

    public Optional<Passenger> findByPassportId(Long passportId) {
        return findByPassportIdTransactional(passportId);
    }

    private Optional<Passenger> findByPassportIdTransactional(Long passportId) {
        return transactionHelper.executeInTransaction(() -> passengerRepository.findByPassportId(passportId));
    }

    public void updateFavoriteAirports(Long passengerId, String code) {
        updateFavoriteAirportsTransactional(passengerId, code);
    }

    public void refundFavoriteAirport(Long passengerId, Long airportId) {
        refundFavoriteAirportTransactional(passengerId, airportId);
    }

    private Passenger saveTransactional(Passenger passenger) {
        return transactionHelper.executeInTransaction(() -> {
            var passport = passenger.getPassport();

            var existedPassport = passportService.findBySeriesAndNumberAndCitizenshipExist(
                            passport.getSeries(), passport.getNumber(), passport.getCitizenship())
                    .orElseGet(() -> passportService.save(passport));

            findByPassportId(existedPassport.getId())
                    .ifPresent(p -> {
                        throw new EntityAlreadyExistException(
                                "Passenger with passport id %d already exists ".formatted(p.getId())
                        );
                    });

            passenger.setPassport(existedPassport);

            return passengerRepository.save(passenger);
        });
    }

    private List<Passenger> findAllByUserIdTransactional(Long userId) {
        return transactionHelper.executeInTransaction(() -> passengerRepository.findAllByUserId(userId));
    }

    private Passenger findByIdTransactional(Long id) {
        return transactionHelper.executeInTransaction(() -> passengerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Passenger with id %d not found".formatted(id))));
    }

    private List<Passenger> findAllTransactional() {
        return transactionHelper.executeInTransaction(passengerRepository::findAll);
    }

    private Passenger updateTransactional(Passenger passenger, Long currentUserId) {
        return transactionHelper.executeInTransaction(() -> {
            var existing = passengerRepository.findById(passenger.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Passenger with id %d not found"
                            .formatted(passenger.getId())));

            if (!existing.getUser().getId().equals(currentUserId)) {
                throw new ValidationException("You cannot update someone else's passenger");
            }
            var passportRequest = passenger.getPassport();
            var passportToLink = passportService.findBySeriesAndNumberAndCitizenshipExist(
                    passportRequest.getSeries(),
                    passportRequest.getNumber(),
                    passportRequest.getCitizenship()
            ).orElseGet(() -> passportService.save(passportRequest));

            existing.setFirstName(passenger.getFirstName());
            existing.setLastName(passenger.getLastName());
            existing.setFatherName(passenger.getFatherName());
            existing.setMale(passenger.isMale());
            existing.setBirthDate(passenger.getBirthDate());
            existing.setPassport(passportToLink);

            return passengerRepository.update(existing);

        });
    }

    private void deleteTransactional(Long id, Long userId) {
        transactionHelper.executeInTransaction(() -> {
            var passenger = passengerRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Passenger with id %d not found".formatted(id)));

            if (!passenger.getUser().getId().equals(userId)) {
                throw new ValidationException("The passenger is not linked to the current user");
            }

            passengerRepository.deleteById(id);
        });
    }

    private void updateFavoriteAirportsTransactional(Long passengerId, String code) {
        transactionHelper.executeInTransaction(() -> {
            var airport = airportService.findByCode(code);

            passengerRepository.updateFavoriteAirports(passengerId, airport.getId());
        });
    }

    private void refundFavoriteAirportTransactional(Long passengerId, Long airportId) {
        transactionHelper.executeInTransaction(() -> passengerRepository.refundFavoriteAirport(passengerId, airportId));
    }
}
