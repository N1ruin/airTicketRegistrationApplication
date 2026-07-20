package unit.service;

import domain.Airport;
import domain.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import util.TransactionHelper;
import service.AirportService;
import service.FlightService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {
    @Mock
    private FlightRepository flightRepository;
    @Mock
    private TransactionHelper transactionHelper;
    @Mock
    private AirportService airportService;
    @InjectMocks
    private FlightService flightService;

    @BeforeEach
    void setUp() {
        lenient().when(transactionHelper.executeInTransaction(any(Supplier.class)))
                .thenAnswer(invocationOnMock -> ((Supplier<?>) invocationOnMock.getArgument(0)).get());

        lenient().doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transactionHelper).executeInTransaction(any(Runnable.class));
    }

    @Test
    void createSuccess() {
        var flight = getFlight();
        var departureDate = ZonedDateTime.now().plusDays(1);
        var arrivalDate = ZonedDateTime.now().plusDays(2);
        flight.setDepartureDate(departureDate);
        flight.setArrivalDate(arrivalDate);
        var airport = new Airport();
        airport.setId(flight.getDepartureAirportId());
        when(airportService.findById(flight.getDepartureAirportId())).thenReturn(airport);
        when(airportService.findById(flight.getArrivalAirportId())).thenReturn(airport);
        when(flightRepository.create(flight)).thenAnswer(invocation -> {
            Flight savedFlight = invocation.getArgument(0);
            savedFlight.setId(1L);
            return savedFlight;
        });

        var result = flightService.create(flight);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100, result.getSeatsCount());
        assertEquals(90, result.getFreeSeats());
        assertEquals(departureDate, result.getDepartureDate());
        assertEquals(arrivalDate, result.getArrivalDate());
        assertEquals("MSQ", result.getDepartureAirportId());
        assertEquals("DME", result.getArrivalAirportId());
        verify(airportService, times(2)).findById(anyString());
        verify(flightRepository).create(flight);
    }

    @Test
    void createThrowsAirportNotFoundException() {
        var flight = getFlight();
        var departureDate = ZonedDateTime.now().plusDays(1);
        var arrivalDate = ZonedDateTime.now().plusDays(2);
        flight.setDepartureDate(departureDate);
        flight.setArrivalDate(arrivalDate);
        when(airportService.findById(flight.getDepartureAirportId()))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> flightService.create(flight));

        verify(airportService).findById(flight.getDepartureAirportId());
    }

    @Test
    void findByIdSuccess() {
        var id = 1L;
        var flight = getFlight();
        var departureDate = ZonedDateTime.now().plusDays(1);
        var arrivalDate = ZonedDateTime.now().plusDays(2);
        flight.setDepartureDate(departureDate);
        flight.setArrivalDate(arrivalDate);
        when(flightRepository.findById(id)).thenAnswer(invocation -> {
            flight.setId(1L);
            return Optional.of(flight);
        });

        var result = flightService.findById(id);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100, result.getSeatsCount());
        assertEquals(90, result.getFreeSeats());
        assertEquals(departureDate, result.getDepartureDate());
        assertEquals(arrivalDate, result.getArrivalDate());
        assertEquals("MSQ", result.getDepartureAirportId());
        assertEquals("DME", result.getArrivalAirportId());
    }

    @Test
    void findByIdThrowsEntityNotFound() {
        var id = 1L;
        var flight = getFlight();
        var departureDate = ZonedDateTime.now().plusDays(1);
        var arrivalDate = ZonedDateTime.now().plusDays(2);
        flight.setDepartureDate(departureDate);
        flight.setArrivalDate(arrivalDate);
        when(flightRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> flightService.findById(id));

        verify(flightRepository).findById(id);
    }

    @Test
    void findAllSuccessReturnListWithSizeTwo() {
        var flightOne = getFlight();
        flightOne.setId(1L);
        var departureDateOne = ZonedDateTime.now().plusDays(1);
        var arrivalDateOne = ZonedDateTime.now().plusDays(2);
        flightOne.setDepartureDate(departureDateOne);
        flightOne.setArrivalDate(arrivalDateOne);
        var flightTwo = getFlight();
        flightTwo.setId(2L);
        flightTwo.setDepartureAirportId("SVO");
        flightTwo.setArrivalAirportId("LED");
        var departureDateTwo = ZonedDateTime.now().plusDays(3);
        var arrivalDateTwo = ZonedDateTime.now().plusDays(4);
        flightTwo.setDepartureDate(departureDateTwo);
        flightTwo.setArrivalDate(arrivalDateTwo);
        when(flightRepository.findAll()).thenReturn(List.of(flightOne, flightTwo));

        var result = flightService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.getFirst().getId());
        assertEquals(2L, result.getLast().getId());
        assertEquals("MSQ", result.getFirst().getDepartureAirportId());
        assertEquals("DME", result.getFirst().getArrivalAirportId());
        assertEquals("SVO", result.getLast().getDepartureAirportId());
        assertEquals("LED", result.getLast().getArrivalAirportId());
        verify(flightRepository).findAll();
    }

    @Test
    void findAllReturnEmptyList() {
        when(flightRepository.findAll()).thenReturn(List.of());

        var result = flightService.findAll();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void updateSuccess() {
        var existingFlight = getFlight();
        existingFlight.setId(1L);

        var updatedFlight = getFlight();
        updatedFlight.setId(1L);
        updatedFlight.setFreeSeats(50);
        updatedFlight.setSeatsCount(200);
        updatedFlight.setDepartureAirportId("SVO");
        updatedFlight.setArrivalAirportId("LED");

        var airport1 = new Airport();
        airport1.setId("SVO");
        var airport2 = new Airport();
        airport2.setId("LED");

        when(flightRepository.findById(1L)).thenReturn(Optional.of(existingFlight));
        when(airportService.findById("SVO")).thenReturn(airport1);
        when(airportService.findById("LED")).thenReturn(airport2);
        when(flightRepository.update(any(Flight.class))).thenReturn(updatedFlight);

        var result = flightService.update(updatedFlight);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(200, result.getSeatsCount());
        assertEquals(50, result.getFreeSeats());
        assertEquals("SVO", result.getDepartureAirportId());
        assertEquals("LED", result.getArrivalAirportId());
        verify(airportService, times(2)).findById(anyString());
        verify(flightRepository).update(any(Flight.class));
    }

    @Test
    void updateFlightNotFoundThrowEntityNotFoundException() {
        var flight = getFlight();
        flight.setId(1L);
        when(flightRepository.findById(flight.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> flightService.update(flight));
        verify(flightRepository).findById(flight.getId());
    }

    @Test
    void deleteSuccessful() {
        var flight = getFlight();
        flight.setId(1L);
        when(flightRepository.findById(flight.getId())).thenReturn(Optional.of(flight));

        flightService.delete(flight.getId());

        verify(flightRepository).deleteById(flight.getId());
    }

    @Test
    void deleteAirportNotFoundThrowsEntityNotFoundException() {
        var flight = getFlight();
        flight.setId(1L);
        when(flightRepository.findById(flight.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> flightService.delete(flight.getId()));
        verify(flightRepository).findById(flight.getId());
    }

    private Flight getFlight() {
        var flight = new Flight();
        flight.setSeatsCount(100);
        flight.setFreeSeats(90);
        var departureAirportCode = "MSQ";
        flight.setDepartureAirportId(departureAirportCode);
        var arrivalAirportCode = "DME";
        flight.setArrivalAirportId(arrivalAirportCode);
        return flight;
    }
}
