package unit.service;

import domain.Address;
import domain.Airport;
import domain.AirportStatus;
import exception.EntityAlreadyExistException;
import exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.AirportRepository;
import repository.TransactionHelper;
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
    void saveSuccess() {
        var airport = getAirport();
        when(airportRepository.findByCode(airport.getCode())).thenReturn(Optional.empty());
        when(airportRepository.save(any(Airport.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        var result = airportService.save(airport);

        assertNotNull(result);
        assertEquals(result, airport);
        assertEquals(result.getId(), airport.getId());
        verify(addressService).save(airport.getAddress());
        verify(airportRepository).save(airport);
    }

    @Test
    void saveSuccessUpdateClosedAirport() {
        var newAirport = getAirport();
        var existedAirport = new Airport();
        existedAirport.setId(1L);
        existedAirport.setCode("MSQ");
        existedAirport.setAirportStatus(AirportStatus.CLOSED);
        when(airportRepository.findByCode(newAirport.getCode())).thenReturn(Optional.of(existedAirport));
        when(airportRepository.update(any(Airport.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        var result = airportService.save(newAirport);

        assertEquals(AirportStatus.WORKS, result.getAirportStatus());
        verify(airportRepository).update(existedAirport);
        verify(airportRepository, never()).save(existedAirport);
        verify(addressService, never()).save(any());
    }

    @Test
    void saveFailureAirportAlreadyWorks() {
        var airport = getAirport();
        var existedAirport = new Airport();
        existedAirport.setAirportStatus(AirportStatus.WORKS);
        when(airportRepository.findByCode(airport.getCode())).thenReturn(Optional.of(existedAirport));

        assertThrows(EntityAlreadyExistException.class, () -> airportService.save(airport));

        verify(airportRepository, never()).save(any());
    }

    @Test
    void saveFailureAddressExist() {
        var airport = getAirport();
        airport.setAirportStatus(AirportStatus.WORKS);
        when(airportRepository.findByCode(airport.getCode())).thenReturn(Optional.of(airport));

        assertThrows(EntityAlreadyExistException.class, () -> airportService.save(airport));

        verify(airportRepository).findByCode(airport.getCode());
    }

    @Test
    void saveFailureAirportExist() {
        var airport = getAirport();
        var existing = new Airport();
        existing.setAirportStatus(AirportStatus.WORKS);
        when(airportRepository.findByCode(airport.getCode())).thenReturn(Optional.of(existing));

        assertThrows(EntityAlreadyExistException.class, () -> airportService.save(airport));

        verify(airportRepository).findByCode(airport.getCode());
    }

    @Test
    void findByIdSuccess() {
        var airport = getAirport();
        airport.setId(1L);
        when(airportRepository.findById(airport.getId())).thenReturn(Optional.of(airport));

        var result = airportService.findById(airport.getId());

        assertEquals(airport, result);
        verify(airportRepository).findById(airport.getId());
    }

    @Test
    void findByIdNotFoundThrowsEntityNotFoundException() {
        var airport = getAirport();
        airport.setId(100L);
        when(airportRepository.findById(airport.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> airportService.findById(airport.getId()));
        verify(airportRepository).findById(airport.getId());
    }

    @Test
    void findAllReturnOneAirport() {
        var airport = getAirport();
        var airports = List.of(airport);
        when(airportRepository.findAll()).thenReturn(airports);

        var result = airportService.findAll();

        assertEquals(1, result.size());
        assertEquals(airports, result);
        verify(airportRepository).findAll();
    }

    @Test
    void findAllReturnEmptyList() {
        var airports = new ArrayList<Airport>();
        when(airportRepository.findAll()).thenReturn(airports);

        var result = airportService.findAll();

        assertEquals(0, result.size());
        assertEquals(airports, result);
        verify(airportRepository).findAll();
    }

    @Test
    void updateSuccess() {
        var airport = getAirport();
        var address = airport.getAddress();
        when(airportRepository.findById(airport.getId())).thenReturn(Optional.of(airport));
        when(addressService.update(any(Address.class), eq(airport.getId()))).thenReturn(address);
        when(airportRepository.update(any(Airport.class))).thenAnswer(i -> i.getArgument(0));
        airport.setName("Test");
        airport.setCode("TST");
        address.setCountry("Russia");
        address.setCity("Moscow");
        airport.setAirportStatus(AirportStatus.CLOSED);

        var result = airportService.update(airport);

        assertEquals("Test", result.getName());
        assertEquals("TST", result.getCode());
        assertEquals("Russia", result.getAddress().getCountry());
        assertEquals("Moscow", result.getAddress().getCity());
        assertEquals(AirportStatus.CLOSED, result.getAirportStatus());
        verify(airportRepository).findById(airport.getId());
        verify(addressService).update(address, airport.getId());
        verify(airportRepository).update(airport);
    }

    @Test
    void updateFailureAirportNotFoundThrowEntityNotFoundException() {
        var airport = getAirport();
        when(airportRepository.findById(airport.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> airportService.update(airport));
        verify(airportRepository).findById(airport.getId());
    }

    @Test
    void deleteSuccessful() {
        var airport = getAirport();
        when(airportRepository.findById(airport.getId())).thenReturn(Optional.of(airport));

        airportService.delete(airport.getId());

        verify(airportRepository).deleteById(airport.getId());
    }

    @Test
    void deleteAirportNotFoundThrowsEntityNotFoundException() {
        var airport = getAirport();
        when(airportRepository.findById(airport.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> airportService.delete(airport.getId()));
    }

    private Airport getAirport() {
        var airport = new Airport();
        airport.setCode("MSQ");
        airport.setName("Minsk airport");
        airport.setAddress(getAddress());
        airport.setId(1L);

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
