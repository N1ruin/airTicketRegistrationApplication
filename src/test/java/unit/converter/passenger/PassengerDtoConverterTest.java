package unit.converter.passenger;

import converter.passenger.PassengerDtoConverter;
import domain.Passenger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerDtoConverterTest {
    private final PassengerDtoConverter converter = new PassengerDtoConverter();

    @Test
    void convertPassengerToPassengerDtoSuccess() {
        var passportMock = mock(Passport.class);
        Long passportId = 2L;
        when(passportMock.getId()).thenReturn(passportId);

        var passenger = new Passenger();
        passenger.setId(1L);
        passenger.setUserId(1L);
        passenger.setPassport(passportMock);

        var result = converter.convert(passenger);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(passportId, result.passportId());
        assertEquals(1L, result.userId());
        verify(passportMock).getId();
    }

    @Test
    void convertPassengerListToPassengerDtoListSuccess() {
        var passportMock1 = mock(Passport.class);
        when(passportMock1.getId()).thenReturn(1L);
        var passportMock2 = mock(Passport.class);
        when(passportMock2.getId()).thenReturn(2L);
        var passengerOne = new Passenger();
        passengerOne.setId(10L);
        passengerOne.setUserId(100L);
        passengerOne.setPassport(passportMock1);
        var passengerTwo = new Passenger();
        passengerTwo.setId(20L);
        passengerTwo.setUserId(200L);
        passengerTwo.setPassport(passportMock2);

        var dtoList = converter.convertAll(List.of(passengerOne, passengerTwo));

        assertNotNull(dtoList);
        assertEquals(2, dtoList.size());
        assertEquals(10L, dtoList.getFirst().id());
        assertEquals(1L, dtoList.get(0).passportId());
        assertEquals(100L, dtoList.get(0).userId());
        assertEquals(20L, dtoList.get(1).id());
        assertEquals(2L, dtoList.get(1).passportId());
        assertEquals(200L, dtoList.get(1).userId());
        verify(passportMock1).getId();
        verify(passportMock2).getId();
    }
}
