package unit.service;

import domain.Airport;
import domain.Passenger;
import domain.Passport;
import exception.EntityAlreadyExistException;
import exception.EntityNotFoundException;
import exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.PassengerRepository;
import repository.TransactionHelper;
import service.AirportService;
import service.PassengerService;
import service.PassportService;

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
    @InjectMocks
    private PassengerService passengerService;

    @BeforeEach
    void setUp() {
        lenient().when(transactionHelper.executeInTransaction(any(Supplier.class)))
                .thenAnswer(invocationOnMock -> ((Supplier<?>) invocationOnMock.getArgument(0)).get());

        lenient().doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(transactionHelper).executeInTransaction(any(Runnable.class));
    }

    @Test
    void saveSuccessWithNewPassport() {
        var passport = new Passport();
        passport.setSeries("4510");
        var passenger = new Passenger();
        passenger.setPassport(passport);
        when(passportService.findBySeriesAndNumberAndCitizenshipExist(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(passportService.save(passport)).thenReturn(passport);
        when(passengerRepository.save(passenger)).thenReturn(passenger);

        var result = passengerService.save(passenger);

        assertNotNull(result);
        verify(passportService).findBySeriesAndNumberAndCitizenshipExist(any(), any(), any());
        verify(passportService).save(passport);
        verify(passengerRepository).save(passenger);
    }

    @Test
    void saveSuccessWithExistingPassport() {
        var passportRequest = new Passport();
        passportRequest.setSeries("4510");
        var existingPassport = new Passport();
        existingPassport.setId(99L);
        var passenger = new Passenger();
        passenger.setPassport(passportRequest);
        when(passportService.findBySeriesAndNumberAndCitizenshipExist(any(), any(), any()))
                .thenReturn(Optional.of(existingPassport));
        when(passengerRepository.save(passenger)).thenReturn(passenger);

        var result = passengerService.save(passenger);

        assertNotNull(result);
        assertEquals(99L, passenger.getPassport().getId());
        verify(passportService, never()).save(any());
        verify(passengerRepository).save(passenger);
    }

    @Test
    void saveThrowsEntityAlreadyExistExceptionWhenPassengerWithPassportExists() {
        var passport = new Passport();
        passport.setId(99L);
        var passenger = new Passenger();
        passenger.setPassport(passport);

        when(passportService.findBySeriesAndNumberAndCitizenshipExist(any(), any(), any()))
                .thenReturn(Optional.of(passport));
        when(passengerRepository.findByPassportId(99L)).thenReturn(Optional.of(new Passenger()));

        assertThrows(EntityAlreadyExistException.class, () -> passengerService.save(passenger));

        verify(passengerRepository, never()).save(any());
    }

    @Test
    void findAllReturnListWithSizeTwo() {
        var passengerOne = new Passenger();
        var passengerTwo = new Passenger();
        when(passengerRepository.findAll()).thenReturn(List.of(passengerOne, passengerTwo));

        var result = passengerService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(passengerOne, result.get(0));
        assertEquals(passengerTwo, result.get(1));
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
        var currentUserId = 10L;
        var passportRequest = new Passport();
        passportRequest.setSeries("4510");

        var existingPassenger = new Passenger();
        existingPassenger.setId(1L);
        existingPassenger.setUser(currentUserId);

        var existingPassport = new Passport();
        existingPassport.setId(99L);
        var passengerUpdateData = new Passenger();
        passengerUpdateData.setId(1L);
        passengerUpdateData.setPassport(passportRequest);
        when(passengerRepository.findById(1L)).thenReturn(Optional.of(existingPassenger));
        when(passportService.findBySeriesAndNumberAndCitizenshipExist(any(), any(), any()))
                .thenReturn(Optional.of(existingPassport));
        when(passengerRepository.update(any(Passenger.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = passengerService.update(passengerUpdateData, currentUserId);

        assertNotNull(result);
        assertEquals(99L, result.getPassport().getId());
        verify(passengerRepository).update(any());
    }

    @Test
    void updateThrowsPassengerNotFoundException() {
        var passenger = new Passenger();
        var passengerId = 1L;
        passenger.setId(passengerId);
        var userId = 2L;
        when(passengerRepository.findById(passengerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> passengerService.update(passenger, userId));
        verify(passengerRepository, never()).update(passenger);
        verify(passengerRepository).findById(passengerId);
    }

    @Test
    void updateThrowsValidationExceptionWhenUserMismatch() {
        Long currentUserId = 10L;
        Long hackerId = 666L;
        var passenger = new Passenger();
        passenger.setId(1L);
        passenger.setUser(currentUserId);

        when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));

        assertThrows(ValidationException.class, () -> passengerService.update(passenger, hackerId));

        verify(passengerRepository, never()).update(any());
    }

    @Test
    void updateFavoriteAirportsSuccess() {
        Long passengerId = 1L;
        String code = "MSQ";
        var airport = new Airport();
        airport.setId(5L);
        airport.setCode(code);

        when(airportService.findByCode(code)).thenReturn(airport);

        passengerService.updateFavoriteAirports(passengerId, code);

        verify(airportService).findByCode(code);
        verify(passengerRepository).updateFavoriteAirports(passengerId, 5L);
    }

    @Test
    void deleteSuccess() {
        Long userId = 10L;
        Long passengerId = 1L;

        var passport = new Passport();
        passport.setId(100L);

        var passenger = new Passenger();
        passenger.setId(passengerId);
        passenger.setUser(userId);
        passenger.setPassport(passport);

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger));

        passengerService.delete(passengerId, userId);

        verify(passportService).deleteById(100L);
        verify(passengerRepository).deleteById(passengerId);
    }

    @Test
    void deleteThrowsEntityNotFoundException() {
        var id = 1L;
        var userId = 2L;
        when(passengerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> passengerService.delete(id, userId));
        verify(passengerRepository).findById(id);
        verify(passengerRepository, never()).deleteById(id);
    }

    @Test
    void deleteThrowsValidationExceptionWhenUserMismatch() {
        Long currentUserId = 10L;
        Long hackerId = 666L;
        var passenger = new Passenger();
        passenger.setId(1L);
        passenger.setUser(currentUserId);
        when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));

        assertThrows(ValidationException.class, () -> passengerService.delete(passenger.getId(), hackerId));

        verify(passengerRepository, never()).update(any());
    }

    @Test
    void refundFavoriteAirportSuccess() {
        Long passengerId = 1L;
        Long airportId = 5L;

        passengerService.refundFavoriteAirport(passengerId, airportId);

        verify(passengerRepository).refundFavoriteAirport(passengerId, airportId);
    }
}
