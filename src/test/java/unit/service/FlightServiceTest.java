package unit.service;

import domain.Airport;
import domain.Flight;
import exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.FlightRepository;
import repository.TransactionHelper;
import service.AirportService;
import service.FlightService;

import java.time.LocalDateTime;
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
    void saveSuccess() {
        var flight = getFlight();
        var departureDate = LocalDateTime.now().plusDays(1);
        var arrivalDate = LocalDateTime.now().plusDays(2);
        flight.setDepartureDate(departureDate);
        flight.setArrivalDate(arrivalDate);
        when(airportService.findByCode(flight.getDepartureAirport().getCode()))
                .thenReturn(flight.getDepartureAirport());
        when(airportService.findByCode(flight.getArrivalAirport().getCode()))
                .thenReturn(flight.getArrivalAirport());
        when(flightRepository.save(flight)).thenAnswer(invocation -> {
            Flight savedFlight = invocation.getArgument(0);
            savedFlight.setId(1L);
            return savedFlight;
        });

        var result = flightService.save(flight);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100, result.getAllSeats());
        assertEquals(90, result.getFreeSeats());
        assertEquals(departureDate, result.getDepartureDate());
        assertEquals(arrivalDate, result.getArrivalDate());
        assertEquals("DEP", result.getDepartureAirport().getCode());
        assertEquals("ARR", result.getArrivalAirport().getCode());
        verify(airportService).findByCode(result.getDepartureAirport().getCode());
        verify(airportService).findByCode(result.getArrivalAirport().getCode());
        verify(flightRepository).save(flight);
    }

    @Test
    void saveThrowsAirportNotFoundException() {
        var flight = getFlight();
        var departureDate = LocalDateTime.now().plusDays(1);
        var arrivalDate = LocalDateTime.now().plusDays(2);
        flight.setDepartureDate(departureDate);
        flight.setArrivalDate(arrivalDate);
        when(airportService.findByCode(flight.getDepartureAirport().getCode()))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> flightService.save(flight));

        verify(airportService).findByCode(flight.getDepartureAirport().getCode());
    }

    @Test
    void findByIdSuccess() {
        var id = 1L;
        var flight = getFlight();
        var departureDate = LocalDateTime.now().plusDays(1);
        var arrivalDate = LocalDateTime.now().plusDays(2);
        flight.setDepartureDate(departureDate);
        flight.setArrivalDate(arrivalDate);
        when(flightRepository.findById(id)).thenAnswer(invocation -> {
            flight.setId(1L);
            return Optional.of(flight);
        });

        var result = flightService.findById(id);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100, result.getAllSeats());
        assertEquals(90, result.getFreeSeats());
        assertEquals(departureDate, result.getDepartureDate());
        assertEquals(arrivalDate, result.getArrivalDate());
        assertEquals("DEP", result.getDepartureAirport().getCode());
        assertEquals("ARR", result.getArrivalAirport().getCode());
    }

    @Test
    void findByIdThrowsEntityNotFound() {
        var id = 1L;
        var flight = getFlight();
        var departureDate = LocalDateTime.now().plusDays(1);
        var arrivalDate = LocalDateTime.now().plusDays(2);
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
        var departureDateOne = LocalDateTime.now().plusDays(1);
        var arrivalDateOne = LocalDateTime.now().plusDays(2);
        flightOne.setDepartureDate(departureDateOne);
        flightOne.setArrivalDate(arrivalDateOne);
        var flightTwo = getFlight();
        flightTwo.setId(2L);
        var departureDateTwo = LocalDateTime.now().plusDays(1);
        var arrivalDateTwo = LocalDateTime.now().plusDays(2);
        flightTwo.setDepartureDate(departureDateTwo);
        flightTwo.setArrivalDate(arrivalDateTwo);
        when(flightRepository.findAll()).thenReturn(List.of(flightOne, flightTwo));

        var result = flightService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.getFirst().getId());
        assertEquals(2L, result.getLast().getId());
        assertNotNull(result);
        assertEquals(1L, result.getFirst().getId());
        assertEquals(100, result.getFirst().getAllSeats());
        assertEquals(90, result.getFirst().getFreeSeats());
        assertEquals(departureDateOne, result.getFirst().getDepartureDate());
        assertEquals(arrivalDateOne, result.getFirst().getArrivalDate());
        assertEquals("DEP", result.getFirst().getDepartureAirport().getCode());
        assertEquals("ARR", result.getFirst().getArrivalAirport().getCode());
        assertNotNull(result);
        assertEquals(2L, result.getLast().getId());
        assertEquals(100, result.getLast().getAllSeats());
        assertEquals(90, result.getLast().getFreeSeats());
        assertEquals(departureDateTwo, result.getLast().getDepartureDate());
        assertEquals(arrivalDateTwo, result.getLast().getArrivalDate());
        assertEquals("DEP", result.getLast().getDepartureAirport().getCode());
        assertEquals("ARR", result.getLast().getArrivalAirport().getCode());
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
        var flight = getFlight();
        flight.setId(1L);
        var depAirport = new Airport();
        depAirport.setCode("AAA");
        var arrAirport = new Airport();
        arrAirport.setCode("TTT");
        flight.setDepartureAirport(depAirport);
        flight.setArrivalAirport(arrAirport);
        flight.setFreeSeats(50);
        flight.setAllSeats(200);
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(airportService.findByCode("AAA")).thenReturn(depAirport);
        when(airportService.findByCode("TTT")).thenReturn(arrAirport);
        when(flightRepository.update(any(Flight.class))).thenReturn(flight);

        var result = flightService.update(flight);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(200, result.getAllSeats());
        assertEquals(50, result.getFreeSeats());
        verify(airportService, times(2)).findByCode(anyString());
        verify(flightRepository).update(flight);
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
        flight.setAllSeats(100);
        flight.setFreeSeats(90);
        var departureAirport = new Airport();
        departureAirport.setCode("DEP");
        flight.setDepartureAirport(departureAirport);
        var arrivalAirport = new Airport();
        arrivalAirport.setCode("ARR");
        flight.setArrivalAirport(arrivalAirport);
        return flight;
    }
}
