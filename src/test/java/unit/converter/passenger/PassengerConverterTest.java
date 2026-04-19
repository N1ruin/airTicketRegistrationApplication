package unit.converter.passenger;

import converter.passenger.PassengerConverter;
import converter.passsport.PassportConverter;
import domain.Passport;
import dto.passenger.PassengerDto;
import dto.passport.PassportDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerConverterTest {
    @Mock
    private PassportConverter passportConverter;
    @InjectMocks
    private PassengerConverter passengerConverter;

    @Test
    void convertPassengerDtoToPassengerSuccess() {
        var passportDtoMock = mock(PassportDto.class);
        var passportMock = mock(Passport.class);
        var birthDate = LocalDate.now();
        var passengerDto = new PassengerDto(1L, "FirstName", "LastName", "FatherName",
                true, birthDate, passportDtoMock, 1L);

        when(passportConverter.convert(passportDtoMock)).thenReturn(passportMock);

        var result = passengerConverter.convert(passengerDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("FirstName", result.getFirstName());
        assertEquals("LastName", result.getLastName());
        assertEquals("FatherName", result.getFatherName());
        assertTrue(result.isMale());
        assertEquals(birthDate, result.getBirthDate());
        assertEquals(passportMock, result.getPassport());
        assertEquals(1L, result.getUserId());
        verify(passportConverter).convert(passportDtoMock);
    }
}
