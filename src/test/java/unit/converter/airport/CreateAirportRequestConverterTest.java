package unit.converter.airport;

import dto.address.AddressDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAirportRequestConverterTest {
    @Mock
    private AddressDtoConverter addressDtoConverter;
    @InjectMocks
    private CreateAirportRequestConverter converter;

    @Test
    void convertCreateAirportRequestToAirportSuccess() {
        var addressMock = mock(Address.class);
        var addressDtoMock = mock(AddressDto.class);
        var request = new CreateAirportRequest("SVO", "Шереметьево", addressDtoMock);

        when(addressDtoConverter.convert(addressDtoMock)).thenReturn(addressMock);

        var result = converter.convert(request);

        assertNotNull(result);
        assertEquals("SVO", result.getId());
        assertEquals("Шереметьево", result.getName());
        assertEquals(addressMock, result.getAddress());
        verify(addressDtoConverter).convert(addressDtoMock);
    }
}
