package service;

import domain.Flight;
import exception.EntityNotFoundException;
import repository.FlightRepository;
import util.TransactionHelper;

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

    public Flight create(Flight flight) {
        return transactionHelper.executeInTransaction(() -> {
            airportService.findById(flight.getDepartureAirportCode());
            airportService.findById(flight.getArrivalAirportCode());

            return flightRepository.create(flight);
        });
    }

    public Flight findById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight not found. ID: %d".formatted(id)));
    }

    public List<Flight> findAll() {
        return flightRepository.findAll();
    }

    public Flight update(Flight flight) {
        return transactionHelper.executeInTransaction(() -> {
            var existedFlight = findById(flight.getId());

            if (isAirportCodeChange(existedFlight.getDepartureAirportCode(), flight.getDepartureAirportCode())) {
                airportService.findById(flight.getDepartureAirportCode());
                existedFlight.setDepartureAirportCode(flight.getDepartureAirportCode());
            }

            if (isAirportCodeChange(existedFlight.getArrivalAirportCode(), flight.getArrivalAirportCode())) {
                airportService.findById(flight.getArrivalAirportCode());
                existedFlight.setArrivalAirportCode(flight.getArrivalAirportCode());
            }

            existedFlight.setFreeSeats(flight.getFreeSeats());
            existedFlight.setAllSeats(flight.getAllSeats());
            existedFlight.setDepartureDate(flight.getDepartureDate());
            existedFlight.setArrivalDate(flight.getArrivalDate());

            return flightRepository.update(existedFlight);
        });
    }

    public void delete(Long id) {
        transactionHelper.executeInTransaction(() -> {
            findById(id);

            flightRepository.deleteById(id);
        });
    }

    private boolean isAirportCodeChange(String oldCode, String newCode) {
        return !oldCode.equals(newCode);
    }
}
