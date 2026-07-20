package service;

import domain.Flight;
import exception.ApplicationException;
import repository.impl.FlightRepository;
import util.TransactionHelper;

import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

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
            airportService.findById(flight.getDepartureAirportId());
            airportService.findById(flight.getArrivalAirportId());

            return flightRepository.create(flight);
        });
    }

    public Flight findById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Flight not found. ID: %d".formatted(id), SC_NOT_FOUND));
    }

    public List<Flight> findAll() {
        return flightRepository.findAll();
    }

    public Flight update(Flight flight) {
        return transactionHelper.executeInTransaction(() -> {
            var existedFlight = findById(flight.getId());

            if (isAirportCodeChange(existedFlight.getDepartureAirportId(), flight.getDepartureAirportId())) {
                airportService.findById(flight.getDepartureAirportId());
                existedFlight.setDepartureAirportId(flight.getDepartureAirportId());
            }

            if (isAirportCodeChange(existedFlight.getArrivalAirportId(), flight.getArrivalAirportId())) {
                airportService.findById(flight.getArrivalAirportId());
                existedFlight.setArrivalAirportId(flight.getArrivalAirportId());
            }

            existedFlight.setFreeSeats(flight.getFreeSeats());
            existedFlight.setSeatsCount(flight.getSeatsCount());
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
