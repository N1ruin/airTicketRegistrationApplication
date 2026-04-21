package service;

import domain.Passport;
import exception.EntityNotFoundException;
import repository.PassportRepository;
import repository.TransactionHelper;

import java.util.Optional;

public class PassportService {
    private final TransactionHelper transactionHelper;
    private final PassportRepository passportRepository;

    public PassportService(TransactionHelper transactionHelper, PassportRepository passportRepository) {
        this.transactionHelper = transactionHelper;
        this.passportRepository = passportRepository;
    }

    public Passport save(Passport passport) {
        return saveTransactional(passport);
    }

    public Passport update(Passport passport) {
        return updateTransactional(passport);
    }

    public void deleteById(Long id) {
        deleteTransactional(id);
    }

    public Optional<Passport> findBySeriesAndNumberAndCitizenshipExist(String series, String number, String citizenship) {
        return findBySeriesAndNumberAndCitizenshipTransactional(series, number, citizenship);
    }

    private Passport saveTransactional(Passport passport) {
        return transactionHelper.executeInTransaction(() -> passportRepository.findBySeriesAndNumberAndCitizenship(
                        passport.getSeries(), passport.getNumber(), passport.getCitizenship())
                .orElseGet(() -> passportRepository.save(passport)));
    }

    private Passport updateTransactional(Passport passport) {
        return transactionHelper.executeInTransaction(() -> {
            var existedPassport = passportRepository.findBySeriesAndNumberAndCitizenship(passport.getSeries(), passport.getNumber(),
                            passport.getCitizenship())
                    .orElseThrow(() -> new EntityNotFoundException("Passport  not found"));

            passport.setId(existedPassport.getId());

            return passportRepository.update(passport);
        });
    }

    private void deleteTransactional(Long id) {
        transactionHelper.executeInTransaction(() -> {
            passportRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Passport with id %d not found".formatted(id)));

            passportRepository.deleteById(id);
        });
    }

    private Optional<Passport> findBySeriesAndNumberAndCitizenshipTransactional(String series, String number, String citizenship) {
        return transactionHelper.executeInTransaction(() ->
                passportRepository.findBySeriesAndNumberAndCitizenship(series, number, citizenship));
    }
}
