package unit.converter.passenger;

import converter.passenger.PassengerDtoConverter;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerDtoConverterTest {
    @Mock
    private PassportDtoConverter passportDtoConverter;
    @InjectMocks
    private PassengerDtoConverter converter;

    @Test
    void convertPassengerToPassengerDtoSuccess() {
        var birthDate = LocalDate.now();
        var passportMock = mock(Passport.class);
        var passportDtoMock = mock(PassportDto.class);
        var passenger = new Passenger();
        passenger.setId(1L);
        passenger.setFirstName("FirstName");
        passenger.setLastName("LastName");
        passenger.setFatherName("FatherName");
        passenger.setMale(true);
        passenger.setBirthDate(birthDate);
        passenger.setUser(1L);
        passenger.setPassport(passportMock);
        when(passportDtoConverter.convert(passportMock)).thenReturn(passportDtoMock);

        var result = converter.convert(passenger);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("FirstName", result.firstName());
        assertEquals("LastName", result.lastName());
        assertEquals("FatherName", result.fatherName());
        assertTrue(result.male());
        assertEquals(birthDate, result.birthDate());
        assertEquals(passportDtoMock, result.passportDto());
        assertEquals(1L, result.userId());
        verify(passportDtoConverter).convert(passportMock);
    }

    @Test
    void convertPassengerListToPassengerDtoListSuccess() {
        var passengerOne = new Passenger();
        var passengerTwo = new Passenger();

        var dtoList = converter.convertAll(List.of(passengerOne, passengerTwo));

        assertNotNull(dtoList);
        assertEquals(2, dtoList.size());
        verify(passportDtoConverter, times(2)).convert(any());
    }
}
