package unit.service;

import domain.Airport;
import domain.Passenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import util.CurrentUserHolder;
import util.TransactionHelper;
import service.AirportService;
import service.PassengerService;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerServiceTest {
    @Mock
    private PassengerRepository passengerRepository;
    @Mock
    private AirportService airportService;
    @Mock
    private TransactionHelper transactionHelper;
    @Mock
    private PassportService passportService;
    @Mock
    private FavoriteAirportsRepository favoriteAirportsRepository;
    @InjectMocks
    private PassengerService passengerService;

    @BeforeEach
    void setUp() {
        CurrentUserHolder.setCurrentUserId(10L);
        CurrentUserHolder.setCurrentUserRole(domain.Role.USER);

        lenient().when(transactionHelper.executeInTransaction(any(Supplier.class)))
                .thenAnswer(invocationOnMock -> ((Supplier<?>) invocationOnMock.getArgument(0)).get());

        lenient().doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(transactionHelper).executeInTransaction(any(Runnable.class));
    }

    @Test
    void createSuccessWithNewPassport() {
        var passport = new Passport();
        passport.setId(99L);
        passport.setSeries("4510");
        var passenger = new Passenger();
        passenger.setPassport(passport);
        when(passportService.create(passport)).thenReturn(passport);
        when(passengerRepository.create(passenger)).thenReturn(passenger);

        var result = passengerService.create(passenger);

        assertNotNull(result);
        assertEquals(99L, result.getPassport().getId());
        verify(passportService).create(passport);
        verify(passengerRepository).create(passenger);
    }

    @Test
    void createSuccessWithExistingPassport() {
        var existingPassport = new Passport();
        existingPassport.setId(99L);
        existingPassport.setSeries("4510");
        var passenger = new Passenger();
        passenger.setPassport(existingPassport);
        when(passportService.create(existingPassport)).thenReturn(existingPassport);
        when(passengerRepository.create(passenger)).thenReturn(passenger);

        var result = passengerService.create(passenger);

        assertNotNull(result);
        assertEquals(99L, result.getPassport().getId());
        verify(passportService).create(existingPassport);
        verify(passengerRepository).create(passenger);
    }

    @Test
    void createThrowsEntityAlreadyExistExceptionWhenPassengerWithPassportExists() {
        var passport = new Passport();
        passport.setId(99L);
        passport.setSeries("4510");

        var passenger = new Passenger();
        passenger.setPassport(passport);

        when(passportService.create(passport))
                .thenThrow(EntityAlreadyExistException.class);

        assertThrows(EntityAlreadyExistException.class, () -> passengerService.create(passenger));

        verify(passengerRepository, never()).create(any());
    }

    @Test
    void findAllReturnListWithSizeTwo() {
        var passengerOne = new Passenger();
        var passengerTwo = new Passenger();
        when(passengerRepository.findAll()).thenReturn(List.of(passengerOne, passengerTwo));

        var result = passengerService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(passengerRepository).findAll();
    }

    @Test
    void findAllReturnEmptyList() {
        when(passengerRepository.findAll()).thenReturn(List.of());

        var result = passengerService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(passengerRepository).findAll();
    }

    @Test
    void updateSuccess() {
        var existingPassport = new Passport();
        existingPassport.setId(99L);
        existingPassport.setSeries("4510");
        var existingPassenger = new Passenger();
        existingPassenger.setId(1L);
        existingPassenger.setUserId(10L);
        existingPassenger.setPassport(existingPassport);
        var updatedPassport = new Passport();
        updatedPassport.setId(100L);
        updatedPassport.setSeries("5555");
        var passengerUpdateData = new Passenger();
        passengerUpdateData.setId(1L);
        passengerUpdateData.setPassport(updatedPassport);
        when(passengerRepository.findByIdAndUserId(eq(1L), eq(10L))).thenReturn(Optional.of(existingPassenger));
        when(passportService.update(updatedPassport, 1L)).thenReturn(updatedPassport);

        var result = passengerService.update(passengerUpdateData);

        assertNotNull(result);
        assertEquals(100L, result.getPassport().getId());
        verify(passengerRepository).findByIdAndUserId(eq(1L), eq(10L));
        verify(passportService).update(updatedPassport, 1L);
        verify(passengerRepository, never()).update(any());
    }

    @Test
    void updateThrowsPassengerNotFoundException() {
        var passenger = new Passenger();
        passenger.setId(1L);

        when(passengerRepository.findByIdAndUserId(eq(1L), eq(10L)))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> passengerService.update(passenger));
        verify(passengerRepository, never()).update(any());
    }

    @Test
    void addFavoriteAirportSuccess() {
        Long passengerId = 1L;
        String code = "MSQ";
        var airport = new Airport();
        airport.setId(code);
        var passenger = new Passenger();
        passenger.setId(passengerId);
        when(airportService.findById(code)).thenReturn(airport);
        when(passengerRepository.findByIdAndUserId(eq(passengerId), eq(10L))).thenReturn(Optional.of(passenger));

        passengerService.addFavoriteAirport(passengerId, code);

        verify(airportService).findById(code);
        verify(passengerRepository).findByIdAndUserId(eq(passengerId), eq(10L));
        verify(favoriteAirportsRepository).addFavorite(eq(passengerId), eq(code));
    }

    @Test
    void deleteSuccess() {
        var passport = new Passport();
        passport.setId(100L);

        var passenger = new Passenger();
        passenger.setId(1L);
        passenger.setUserId(10L);
        passenger.setPassport(passport);

        when(passengerRepository.findByIdAndUserId(eq(1L), eq(10L))).thenReturn(Optional.of(passenger));

        passengerService.delete(1L);

        verify(passportService).deleteById(100L);
        verify(passengerRepository).deleteById(1L);
    }

    @Test
    void findByIdSuccess() {
        var passenger = new Passenger();
        passenger.setId(1L);

        when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));

        var result = passengerService.findById(1L);

        assertNotNull(result);
        verify(passengerRepository).findById(1L);
    }

    @Test
    void findByIdThrowsEntityNotFoundException() {
        when(passengerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> passengerService.findById(1L));
        verify(passengerRepository).findById(1L);
    }

    @Test
    void findAllByUserIdSuccess() {
        var passenger1 = new Passenger();
        var passenger2 = new Passenger();
        when(passengerRepository.findAllByUserId(10L)).thenReturn(List.of(passenger1, passenger2));

        var result = passengerService.findAllByUserId(10L);

        assertEquals(2, result.size());
        verify(passengerRepository).findAllByUserId(10L);
    }

    @Test
    void deleteThrowsEntityNotFoundException() {
        when(passengerRepository.findByIdAndUserId(eq(1L), eq(10L)))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> passengerService.delete(1L));
        verify(passengerRepository, never()).deleteById(any());
    }

    @Test
    void removeFavoriteAirportSuccess() {
        var passengerId = 1L;
        var airportCode = "MSQ";
        var passenger = new Passenger();
        passenger.setId(passengerId);
        when(passengerRepository.findByIdAndUserId(eq(passengerId), eq(10L))).thenReturn(Optional.of(passenger));

        passengerService.removeFavoriteAirport(passengerId, airportCode);

        verify(favoriteAirportsRepository).removeFavorite(eq(passengerId), eq(airportCode));
    }

    @Test
    void findByIdAndUserIdSuccess() {
        var passenger = new Passenger();
        passenger.setId(1L);
        passenger.setUserId(10L);

        when(passengerRepository.findByIdAndUserId(1L, 10L)).thenReturn(Optional.of(passenger));

        var result = passengerService.findByIdAndUserId(1L, 10L);

        assertNotNull(result);
        verify(passengerRepository).findByIdAndUserId(1L, 10L);
    }

    @Test
    void findByIdAndUserIdThrowsEntityNotFoundException() {
        when(passengerRepository.findByIdAndUserId(1L, 10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> passengerService.findByIdAndUserId(1L, 10L));
        verify(passengerRepository).findByIdAndUserId(1L, 10L);
    }
}
