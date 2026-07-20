package unit.converter.passenger;

import converter.passenger.UpdatePassengerRequestConverter;
import converter.passsport.PassportConverter;
import dto.passenger.UpdatePassengerRequest;
import dto.passport.PassportDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePassengerRequestConverterTest {
    @Mock
    private PassportConverter passportConverter;
    @InjectMocks
    private UpdatePassengerRequestConverter converter;

    @Test
    void convertRequestToPassengerSuccess() {
        var passportMock = mock(Passport.class);
        var passportDtoMock = mock(PassportDto.class);
        var request = new UpdatePassengerRequest(1L, passportDtoMock);
        when(passportConverter.convert(passportDtoMock)).thenReturn(passportMock);

        var result = converter.convert(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getUserId());
        assertEquals(passportMock, result.getPassport());
        verify(passportConverter).convert(passportDtoMock);
    }
}
