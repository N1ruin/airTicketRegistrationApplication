package service;

import domain.Flight;
import exception.EntityNotFoundException;
import repository.AirportRepository;
import repository.FlightRepository;
import repository.TransactionHelper;

import java.util.List;

public class FlightService {
    private final FlightRepository flightRepository;
    private final TransactionHelper transactionHelper;
    private final AirportRepository airportRepository;

    public FlightService(FlightRepository flightRepository, TransactionHelper transactionHelper,
                         AirportRepository airportRepository) {
        this.flightRepository = flightRepository;
        this.transactionHelper = transactionHelper;
        this.airportRepository = airportRepository;
    }

    public Flight save(Flight flight) {
        return saveTransactional(flight);
    }

    public Flight findById(Long id) {
        return findByIdTransactional(id);
    }

    public List<Flight> findAll() {
        return findAllTransactional();
    }

    public Flight update(Flight flight) {
        return updateTransactional(flight);
    }

    public void delete(Long id) {
        deleteTransactional(id);
    }

    private Flight saveTransactional(Flight flight) {
        return transactionHelper.executeInTransaction(() -> {
            var departureAirportCode = flight.getDepartureAirport().getCode();
            findAirportByCode(departureAirportCode);

            var arrivalAirportCode = flight.getArrivalAirport().getCode();
            findAirportByCode(arrivalAirportCode);

            return flightRepository.save(flight);
        });
    }

    private Flight findByIdTransactional(Long id) {
        return transactionHelper.executeInTransaction(() -> flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight with id %d not found".formatted(id))));
    }

    private List<Flight> findAllTransactional() {
        return transactionHelper.executeInTransaction(flightRepository::findAll);
    }

    private Flight updateTransactional(Flight flight) {
        return transactionHelper.executeInTransaction(() -> {
            findByIdTransactional(flight.getId());

            return flightRepository.update(flight);
        });
    }

    private void deleteTransactional(Long id) {
        transactionHelper.executeInTransaction(() -> {
            flightRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Flight with %d not found".formatted(id)));

            flightRepository.deleteById(id);
        });
    }

    private void findAirportByCode(String code) {
        airportRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Airport with code %s not found"
                        .formatted(code)));
    }
}
