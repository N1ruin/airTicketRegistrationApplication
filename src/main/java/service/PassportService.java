package service;

import domain.Passport;
import exception.EntityAlreadyExistException;
import exception.EntityNotFoundException;
import repository.PassportRepository;
import util.TransactionHelper;

public class PassportService {
    private final TransactionHelper transactionHelper;
    private final PassportRepository passportRepository;

    public PassportService(TransactionHelper transactionHelper, PassportRepository passportRepository) {
        this.transactionHelper = transactionHelper;
        this.passportRepository = passportRepository;
    }

    public Passport create(Passport passport) {
        return transactionHelper.executeInTransaction(() -> {
            var existedOptional = passportRepository.findBySeriesAndNumberAndCitizenship(
                    passport.getSeries(), passport.getNumber(), passport.getCitizenship());

            if (existedOptional.isPresent()) {
                throw new EntityAlreadyExistException(("Passport already exists. " +
                                                       "Series: %s, number: %s, citizenship: %s")
                        .formatted(passport.getSeries(), passport.getNumber(), passport.getCitizenship()));
            }

            return passportRepository.create(passport);
        });
    }

    public Passport update(Passport passport, Long passengerId) {
        return transactionHelper.executeInTransaction(() -> {
            var existing = passportRepository.findByPassengerId(passengerId)
                    .orElseThrow(() -> new EntityNotFoundException("Passport not found. Passenger ID: %d"
                            .formatted(passengerId)));

            existing.setSeries(passport.getSeries());
            existing.setNumber(passport.getNumber());
            existing.setCitizenship(passport.getCitizenship());
            existing.setFirstName(passport.getFirstName());
            existing.setLastName(passport.getLastName());
            existing.setFatherName(passport.getFatherName());
            existing.setBirthDate(passport.getBirthDate());
            existing.setMale(passport.isMale());
            existing.setIssueDate(passport.getIssueDate());
            existing.setExpiredDate(passport.getExpiredDate());

            return passportRepository.update(existing);
        });
    }

    public void deleteById(Long id) {
        transactionHelper.executeInTransaction(() -> {
            passportRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Passport not found. ID: %d".formatted(id)));

            passportRepository.deleteById(id);
        });
    }
}
