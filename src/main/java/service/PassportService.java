package service;

import domain.Passport;
import exception.EntityAlreadyExistException;
import exception.EntityNotFoundException;
import repository.PassportRepository;
import repository.TransactionHelper;

public class PassportService {
    private final TransactionHelper transactionHelper;
    private final PassportRepository passportRepository;

    public PassportService(TransactionHelper transactionHelper, PassportRepository passportRepository) {
        this.transactionHelper = transactionHelper;
        this.passportRepository = passportRepository;
    }

    public void save(Passport passport) {
        saveTransactional(passport);
    }

    private void saveTransactional(Passport passport) {
        transactionHelper.executeInTransaction(() -> {
            if (passportRepository.findBySeriesAndNumberAndCitizenship(passport.getSeries(), passport.getNumber(),
                    passport.getCitizenship()).isPresent()) {
                throw new EntityAlreadyExistException("There is already an passport with series %s, number %s, citizenship %s"
                        .formatted(passport.getSeries(), passport.getNumber(), passport.getCitizenship()));
            }

            passportRepository.save(passport);
        });
    }

    public void update(Passport passport) {
        transactionHelper.executeInTransaction(() -> passportRepository.update(passport));
    }

    public void deleteById(Long id) {
        transactionHelper.executeInTransaction(() -> {
            passportRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Passport with id %d not found".formatted(id)));

            passportRepository.deleteById(id);
        });
    }
}
