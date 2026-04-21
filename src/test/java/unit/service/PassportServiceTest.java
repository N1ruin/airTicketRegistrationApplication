package unit.service;

import domain.Passport;
import exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.PassportRepository;
import repository.TransactionHelper;
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
    void saveSuccess() {
        var passport = getPassport();
        when(passportRepository.findBySeriesAndNumberAndCitizenship(anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(passportRepository.save(any(Passport.class))).thenAnswer(invocation -> {
            Passport p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        var result = passportService.save(passport);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Series", result.getSeries());
        assertEquals("Number", result.getNumber());
        assertEquals("Citizenship", result.getCitizenship());
        assertEquals(LocalDate.now(), result.getIssueDate());
        assertEquals(LocalDate.now().plusYears(5), result.getExpiredDate());
        verify(passportRepository)
                .findBySeriesAndNumberAndCitizenship(result.getSeries(), result.getNumber(), result.getCitizenship());
        verify(passportRepository).save(passport);
    }

    @Test
    void saveExistingPassportReturnsExisted() {
        var passportRequest = getPassport();
        var existingPassport = getPassport();
        existingPassport.setId(99L);
        when(passportRepository.findBySeriesAndNumberAndCitizenship(anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(existingPassport));

        var result = passportService.save(passportRequest);

        assertNotNull(result);
        assertEquals(99L, result.getId());
        verify(passportRepository, never()).save(any());
    }

    @Test
    void updateSuccess() {
        var passport = getPassport();
        var existedPassport = getPassport();
        existedPassport.setId(1L);
        when(passportRepository.findBySeriesAndNumberAndCitizenship(passport.getSeries(), passport.getNumber(),
                passport.getCitizenship()))
                .thenReturn(Optional.of(existedPassport));
        when(passportRepository.update(passport)).thenReturn(passport);

        var result = passportService.update(passport);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Series", result.getSeries());
        assertEquals("Number", result.getNumber());
        assertEquals("Citizenship", result.getCitizenship());
        assertEquals(LocalDate.now(), result.getIssueDate());
        assertEquals(LocalDate.now().plusYears(5), result.getExpiredDate());
        verify(passportRepository)
                .findBySeriesAndNumberAndCitizenship(passport.getSeries(), passport.getNumber(), passport.getCitizenship());
        verify(passportRepository).update(passport);
    }

    @Test
    void updatePassportNotFoundThrowsException() {
        var passport = getPassport();
        when(passportRepository.findBySeriesAndNumberAndCitizenship(passport.getSeries(), passport.getNumber(),
                passport.getCitizenship()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> passportService.update(passport));

        verify(passportRepository)
                .findBySeriesAndNumberAndCitizenship(passport.getSeries(), passport.getNumber(), passport.getCitizenship());
        verify(passportRepository, times(0)).update(passport);
    }

    @Test
    void deleteSuccess() {
        var id = 1L;
        var passport = getPassport();
        passport.setId(id);
        when(passportRepository.findById(id)).thenReturn(Optional.of(new Passport()));

        passportService.deleteById(id);

        verify(passportRepository).findById(id);
        verify(passportRepository).deleteById(id);
    }

    @Test
    void deleteThrowsNotFoundException() {
        var id = 1L;
        var address = getPassport();
        address.setId(id);
        when(passportRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> passportService.deleteById(id));

        verify(passportRepository).findById(id);
    }

    @Test
    void checkPassportExistSuccess() {
        var series = "Series";
        var number = "Number";
        var citizenship = "Citizenship";
        when(passportRepository.findBySeriesAndNumberAndCitizenship(series, number, citizenship))
                .thenReturn(Optional.of(new Passport()));

        assertDoesNotThrow(() ->
                passportService.findBySeriesAndNumberAndCitizenshipExist(series, number, citizenship));

        verify(passportRepository).findBySeriesAndNumberAndCitizenship(series, number, citizenship);
    }

    @Test
    void checkPassportExistThrowsNotFoundException() {
        var series = "Series";
        var number = "Number";
        var citizenship = "Citizenship";
        when(passportRepository.findBySeriesAndNumberAndCitizenship(series, number, citizenship))
                .thenReturn(Optional.empty());

        var result = passportService.findBySeriesAndNumberAndCitizenshipExist(series, number, citizenship);

        assertTrue(result.isEmpty());
        verify(passportRepository).findBySeriesAndNumberAndCitizenship(series, number, citizenship);
    }

    private Passport getPassport() {
        var passport = new Passport();
        passport.setSeries("Series");
        passport.setNumber("Number");
        passport.setCitizenship("Citizenship");
        passport.setIssueDate(LocalDate.now());
        passport.setExpiredDate(LocalDate.now().plusYears(5));

        return passport;
    }
}
