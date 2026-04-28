package unit.converter.passenger;

import converter.passenger.CreatePassengerRequestConverter;
import converter.passsport.PassportConverter;
import domain.Passport;
import dto.passenger.CreatePassengerRequest;
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
class CreatePassengerRequestConverterTest {
    @Mock
    private PassportConverter passportConverter;
    @InjectMocks
    private CreatePassengerRequestConverter converter;

    @Test
    void convertRequestToPassengerSuccess() {
        var passportMock = mock(Passport.class);
        var passportDtoMock = mock(PassportDto.class);
        var birthDate = LocalDate.now();
        var request = new CreatePassengerRequest("FirstName", "LastName", "FatherName",
                true, birthDate, passportDtoMock);

        when(passportConverter.convert(passportDtoMock)).thenReturn(passportMock);

        var result = converter.convert(request);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getUser());
        assertEquals("FirstName", result.getFirstName());
        assertEquals("LastName", result.getLastName());
        assertEquals("FatherName", result.getFatherName());
        assertEquals(birthDate, result.getBirthDate());
        assertTrue(result.isMale());
        assertEquals(passportMock, result.getPassport());
        verify(passportConverter).convert(passportDtoMock);
    }
}
