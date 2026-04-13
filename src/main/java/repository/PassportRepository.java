package repository;

import domain.Passport;

import java.util.Optional;

public interface PassportRepository extends Repository<Passport> {
    Optional<Passport> findBySeriesAndNumberAndCitizenship(String series, String number, String citizenship);
}
