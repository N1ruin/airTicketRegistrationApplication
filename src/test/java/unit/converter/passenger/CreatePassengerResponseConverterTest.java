package unit.converter.passenger;

import converter.passenger.CreatePassengerRequestConverter;
import converter.passenger.CreatePassengerResponseConverter;
import converter.passsport.PassportConverter;
import converter.passsport.PassportDtoConverter;
import domain.Passenger;
import domain.Passport;
import dto.passenger.CreatePassengerRequest;
import dto.passenger.CreatePassengerResponse;
import dto.passport.PassportDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CreatePassengerResponseConverterTest {
    @Mock
    private PassportDtoConverter passportDtoConverter;
    @InjectMocks
    private CreatePassengerResponseConverter converter;

    @Test
    void convertPassengerToResponseSuccess() {
        var passportMock = mock(Passport.class);
        var passportDtoMock = mock(PassportDto.class);
        var birthDate = LocalDate.now();
        var passenger = new Passenger();
        passenger.setId(1L);
        passenger.setFirstName("FirstName");
        passenger.setLastName("LastName");
        passenger.setFatherName("FatherName");
        passenger.setMale(true);
        passenger.setBirthDate(birthDate);
        passenger.setPassport(passportMock);
        passenger.setUserId(1L);
        when(passportDtoConverter.convert(passportMock)).thenReturn(passportDtoMock);

        var result = converter.convert(passenger);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(1L, result.userId());
        assertEquals("FirstName", result.firstName());
        assertEquals("LastName", result.lastName());
        assertEquals("FatherName", result.fatherName());
        assertEquals(birthDate, result.birthDate());
        assertTrue(result.male());
        assertEquals(passportDtoMock, result.passportDto());
        verify(passportDtoConverter).convert(passportMock);
    }
}
