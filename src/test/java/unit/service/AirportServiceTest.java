package unit.service;

import domain.Address;
import domain.Airport;
import exception.EntityAlreadyExistException;
import exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.AirportRepository;
import util.TransactionHelper;
import service.AddressService;
import service.AirportService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirportServiceTest {
    @Mock
    private AddressService addressService;
    @Mock
    private AirportRepository airportRepository;
    @Mock
    private TransactionHelper transactionHelper;

    @InjectMocks
    private AirportService airportService;

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
        var airport = getAirport();
        when(airportRepository.findById(airport.getCode())).thenReturn(Optional.empty());
        when(addressService.create(airport.getAddress())).thenReturn(airport.getAddress());
        when(airportRepository.create(any(Airport.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = airportService.create(airport);

        assertNotNull(result);
        assertEquals(airport.getCode(), result.getCode());
        assertEquals(airport.getName(), result.getName());
        verify(addressService).create(airport.getAddress());
        verify(airportRepository).create(airport);
    }

    @Test
    void createThrowsExceptionWhenAirportAlreadyExists() {
        var airport = getAirport();
        var existingAirport = new Airport();
        existingAirport.setCode(airport.getCode());
        existingAirport.setWorked(true);

        when(airportRepository.findById(airport.getCode())).thenReturn(Optional.of(existingAirport));

        assertThrows(EntityAlreadyExistException.class, () -> airportService.create(airport));

        verify(airportRepository, never()).create(any());
        verify(addressService, never()).create(any());
    }

    @Test
    void findByIdSuccess() {
        var airport = getAirport();
        when(airportRepository.findById(airport.getCode())).thenReturn(Optional.of(airport));

        var result = airportService.findById(airport.getCode());

        assertEquals(airport, result);
        verify(airportRepository).findById(airport.getCode());
    }

    @Test
    void findByIdNotFoundThrowsEntityNotFoundException() {
        var code = "UNKNOWN";
        when(airportRepository.findById(code)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> airportService.findById(code));
        verify(airportRepository).findById(code);
    }

    @Test
    void findAllReturnAirportList() {
        var airport1 = getAirport();
        var airport2 = getAirport();
        airport2.setCode("SVO");
        var airports = List.of(airport1, airport2);

        when(airportRepository.findAll()).thenReturn(airports);

        var result = airportService.findAll();

        assertEquals(2, result.size());
        verify(airportRepository).findAll();
    }

    @Test
    void findAllReturnEmptyList() {
        when(airportRepository.findAll()).thenReturn(new ArrayList<>());

        var result = airportService.findAll();

        assertTrue(result.isEmpty());
        verify(airportRepository).findAll();
    }

    @Test
    void updateSuccess() {
        var airport = getAirport();
        var address = airport.getAddress();

        when(airportRepository.findById(airport.getCode())).thenReturn(Optional.of(airport));
        when(addressService.update(any(Address.class))).thenReturn(address);
        when(airportRepository.update(any(Airport.class))).thenAnswer(i -> i.getArgument(0));

        airport.setName("Updated Name");
        airport.setWorked(false);

        var result = airportService.update(airport);

        assertEquals("Updated Name", result.getName());
        assertEquals("MSQ", result.getCode());
        assertFalse(result.isWorked());
        verify(airportRepository).findById(airport.getCode());
        verify(addressService).update(address);
        verify(airportRepository).update(airport);
    }

    @Test
    void updateThrowsEntityNotFoundExceptionWhenAirportNotFound() {
        var airport = getAirport();
        when(airportRepository.findById(airport.getCode())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> airportService.update(airport));

        verify(airportRepository).findById(airport.getCode());
        verify(addressService, never()).update(any());
        verify(airportRepository, never()).update(any());
    }

    private Airport getAirport() {
        var airport = new Airport();
        airport.setCode("MSQ");
        airport.setName("Minsk airport");
        airport.setAddress(getAddress());
        airport.setWorked(true);

        return airport;
    }

    private Address getAddress() {
        var address = new Address();
        address.setCity("Minsk");
        address.setCountry("Belarus");
        address.setStreet("Test");
        address.setHouseNumber(1);
        address.setId(1L);

        return address;
    }
}
