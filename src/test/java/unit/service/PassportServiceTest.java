package unit.service;

import domain.Passport;
import exception.EntityAlreadyExistException;
import exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.PassportRepository;
import util.TransactionHelper;
import service.PassportService;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassportServiceTest {
    @Mock
    private PassportRepository passportRepository;
    @Mock
    private TransactionHelper transactionHelper;
    @InjectMocks
    private PassportService passportService;

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
        var passport = getPassport();
        when(passportRepository.findBySeriesAndNumberAndCitizenship(
                passport.getSeries(), passport.getNumber(), passport.getCitizenship()))
                .thenReturn(Optional.empty());
        when(passportRepository.create(passport)).thenAnswer(invocation -> {
            Passport p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        var result = passportService.create(passport);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Series", result.getSeries());
        verify(passportRepository).findBySeriesAndNumberAndCitizenship(
                passport.getSeries(), passport.getNumber(), passport.getCitizenship());
        verify(passportRepository).create(passport);
    }

    @Test
    void createThrowsEntityAlreadyExistExceptionWhenPassportExists() {
        var passport = getPassport();
        var existingPassport = new Passport();

        when(passportRepository.findBySeriesAndNumberAndCitizenship(
                passport.getSeries(), passport.getNumber(), passport.getCitizenship()))
                .thenReturn(Optional.of(existingPassport));

        assertThrows(EntityAlreadyExistException.class, () -> passportService.create(passport));

        verify(passportRepository).findBySeriesAndNumberAndCitizenship(
                passport.getSeries(), passport.getNumber(), passport.getCitizenship());
        verify(passportRepository, never()).create(any());
    }

    @Test
    void updateSuccess() {
        var passengerId = 1L;
        var existingPassport = getPassport();
        existingPassport.setId(100L);
        var updatedPassport = getPassport();
        updatedPassport.setSeries("NewSeries");
        when(passportRepository.findByPassengerId(passengerId)).thenReturn(Optional.of(existingPassport));
        when(passportRepository.update(existingPassport)).thenReturn(existingPassport);

        var result = passportService.update(updatedPassport, passengerId);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("NewSeries", result.getSeries());
        verify(passportRepository).findByPassengerId(passengerId);
        verify(passportRepository).update(existingPassport);
    }

    @Test
    void updatePassportNotFoundThrowsException() {
        var passengerId = 999L;
        var passport = getPassport();
        when(passportRepository.findByPassengerId(passengerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> passportService.update(passport, passengerId));

        verify(passportRepository).findByPassengerId(passengerId);
        verify(passportRepository, never()).update(any());
    }

    @Test
    void deleteSuccess() {
        var id = 1L;
        when(passportRepository.findById(id)).thenReturn(Optional.of(new Passport()));

        passportService.deleteById(id);

        verify(passportRepository).findById(id);
        verify(passportRepository).deleteById(id);
    }

    @Test
    void deleteThrowsNotFoundException() {
        var id = 999L;
        when(passportRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> passportService.deleteById(id));

        verify(passportRepository).findById(id);
        verify(passportRepository, never()).deleteById(any());
    }

    private Passport getPassport() {
        var passport = new Passport();
        passport.setSeries("Series");
        passport.setNumber("Number");
        passport.setCitizenship("Citizenship");
        passport.setFirstName("FirstName");
        passport.setLastName("LastName");
        passport.setFatherName("FatherName");
        passport.setBirthDate(LocalDate.now().minusYears(20));
        passport.setIssueDate(LocalDate.now());
        passport.setExpiredDate(LocalDate.now().plusYears(5));
        passport.setMale(true);
        return passport;
    }
}
