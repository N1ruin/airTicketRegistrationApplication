package service;

import domain.Flight;
import exception.EntityNotFoundException;
import repository.FlightRepository;
import repository.TransactionHelper;

import java.util.List;

public class FlightService {
    private final FlightRepository flightRepository;
    private final TransactionHelper transactionHelper;
    private final AirportService airportService;

    public FlightService(FlightRepository flightRepository, TransactionHelper transactionHelper,
                         AirportService airportService) {
        this.flightRepository = flightRepository;
        this.transactionHelper = transactionHelper;
        this.airportService = airportService;
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
            var departureAirport = airportService.findByCode(departureAirportCode);

            var arrivalAirportCode = flight.getArrivalAirport().getCode();
            var arrivalAirport = airportService.findByCode(arrivalAirportCode);

            flight.setDepartureAirport(departureAirport);
            flight.setArrivalAirport(arrivalAirport);

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
            var existedFlight = findByIdTransactional(flight.getId());
            var existedDepartureAirport = airportService.findByCode(flight.getDepartureAirport().getCode());
            var existedArrivalAirport = airportService.findByCode(flight.getArrivalAirport().getCode());

            existedFlight.setDepartureAirport(existedDepartureAirport);
            existedFlight.setArrivalAirport(existedArrivalAirport);
            existedFlight.setFreeSeats(flight.getFreeSeats());
            existedFlight.setAllSeats(flight.getAllSeats());
            existedFlight.setDepartureDate(flight.getDepartureDate());
            existedFlight.setArrivalDate(flight.getArrivalDate());

            return flightRepository.update(existedFlight);
        });
    }

    private void deleteTransactional(Long id) {
        transactionHelper.executeInTransaction(() -> {
            flightRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Flight with %d not found".formatted(id)));

            flightRepository.deleteById(id);
        });
    }
}
