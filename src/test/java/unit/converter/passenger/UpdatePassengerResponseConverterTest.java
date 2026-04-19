package unit.converter.passenger;

import converter.passenger.UpdatePassengerResponseConverter;
import converter.passsport.PassportDtoConverter;
import domain.Passenger;
import domain.Passport;
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
class UpdatePassengerResponseConverterTest {
    @Mock
    private PassportDtoConverter passportConverter;
    @InjectMocks
    private UpdatePassengerResponseConverter converter;

    @Test
    void convertRequestToPassengerSuccess() {
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

        when(passportConverter.convert(passportMock)).thenReturn(passportDtoMock);

        var result = converter.convert(passenger);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("FirstName", result.firstName());
        assertEquals("LastName", result.lastName());
        assertEquals("FatherName", result.fatherName());
        assertEquals(birthDate, result.birthDate());
        assertTrue(result.male());
        assertEquals(passportDtoMock, result.passportDto());
        verify(passportConverter).convert(passportMock);
    }
}
