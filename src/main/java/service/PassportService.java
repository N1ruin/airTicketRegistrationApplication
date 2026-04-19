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

    public Passport save(Passport passport) {
        return saveTransactional(passport);
    }

    public Passport update(Passport passport) {
        return updateTransactional(passport);
    }

    public void deleteById(Long id) {
        deleteTransactional(id);
    }

    public void checkPassportBySeriesAndNumberAndCitizenshipExist(String series, String number, String citizenship) {
        findBySeriesAndNumberAndCitizenshipTransactional(series, number, citizenship);
    }

    private Passport saveTransactional(Passport passport) {
        return transactionHelper.executeInTransaction(() -> {
            var existedPassport = passportRepository.findBySeriesAndNumberAndCitizenship(passport.getSeries(),
                    passport.getNumber(), passport.getCitizenship());
            if (existedPassport.isPresent()) {
                throw new EntityAlreadyExistException("There is already an passport with series %s, number %s, citizenship %s"
                        .formatted(passport.getSeries(), passport.getNumber(), passport.getCitizenship()));
            }

            return passportRepository.save(passport);
        });
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

    private void findBySeriesAndNumberAndCitizenshipTransactional(String series, String number, String citizenship) {
        transactionHelper.executeInTransaction(() ->
                passportRepository.findBySeriesAndNumberAndCitizenship(series, number, citizenship)
                        .orElseThrow(() -> new EntityNotFoundException("Passport wits series %s, number %s, citizenship %s not found"
                                .formatted(series, number, citizenship))));
    }
}
