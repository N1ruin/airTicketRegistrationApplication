package service;

import domain.Airport;
import exception.ApplicationException;
import repository.impl.AirportRepository;
import util.TransactionHelper;

import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_CONFLICT;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public class AirportService {
    private final AirportRepository airportRepository;
    private final TransactionHelper transactionHelper;

    public AirportService(AirportRepository airportRepository,
                          TransactionHelper transactionHelper) {
        this.airportRepository = airportRepository;
        this.transactionHelper = transactionHelper;
    }

    public Airport create(Airport airport) {
        return transactionHelper.executeInTransaction(() -> {
            var existing = airportRepository.findById(airport.getId());

            if (existing.isPresent()) {
                throw new ApplicationException("Airport already exist. Code: %s"
                        .formatted(airport.getId()), SC_CONFLICT);
            }

            checkAvailabilityAtAddress(airport);

            return airportRepository.create(airport);
        });
    }

    public List<Airport> findAll() {
        return airportRepository.findAll();
    }

    public Airport findById(String code) {
        return airportRepository.findById(code)
                .orElseThrow(() -> new ApplicationException("Airport not found. Code: %s".formatted(code), SC_NOT_FOUND));
    }

    public Airport update(Airport airport) {
        return transactionHelper.executeInTransaction(() -> {
            var existingAirport = getAirportByCode(airport.getId());

            existingAirport.setName(airport.getName());
            existingAirport.setCountry(airport.getCountry());
            existingAirport.setStreet(airport.getStreet());
            existingAirport.setHouseNumber(airport.getHouseNumber());
            existingAirport.setActive(airport.isActive());

            return airportRepository.update(existingAirport);
        });
    }

    private Airport getAirportByCode(String code) {
        return airportRepository.findById(code)
                .orElseThrow(() -> new ApplicationException("Airport not found. Code: %s".formatted(code), SC_NOT_FOUND));
    }

    private void checkAvailabilityAtAddress(Airport airport) {
        if (airportRepository.findByCountryAndCityAndStreetAndHouseNumber(airport).isPresent()) {
            throw new ApplicationException(("Airport already exist at the address. Country: %s, City: %s," +
                    " Street: %s, House number: %s")
                    .formatted(
                            airport.getCountry(),
                            airport.getCity(),
                            airport.getStreet(),
                            airport.getHouseNumber()),
                    SC_CONFLICT);
        }
    }
}
