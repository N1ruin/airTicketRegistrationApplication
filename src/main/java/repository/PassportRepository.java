package repository;

import domain.Passport;

import java.util.Optional;

public interface PassportRepository extends Repository<Passport, Long> {
    Optional<Passport> findBySeriesAndNumberAndCitizenship(String series, String number, String citizenship);

    Optional<Passport> findByPassengerId(Long passengerId);
}
