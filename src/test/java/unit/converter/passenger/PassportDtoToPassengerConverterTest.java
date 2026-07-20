package unit.converter.passenger;

import converter.passsport.PassportConverter;
import dto.passport.PassportDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassportDtoToPassengerConverterTest {
    @Mock
    private PassportConverter passportConverter;
    @InjectMocks
    private PassportDtoToPassengerConverter converter;

    @Test
    void convertPassportDtoToPassengerSuccess() {
        var passportMock = mock(Passport.class);
        var passportDtoMock = mock(PassportDto.class);
        when(passportConverter.convert(passportDtoMock)).thenReturn(passportMock);

        var result = converter.convert(passportDtoMock);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getUserId());
        assertEquals(passportMock, result.getPassport());
        verify(passportConverter).convert(passportDtoMock);
    }
}
