package unit.converter.airport;

import converter.airport.UpdateAirportRequestConverter;
import dto.address.AddressDto;
import dto.airport.UpdateAirportRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAirportRequestConverterTest {
    @Mock
    private AddressDtoConverter addressDtoConverter;
    @InjectMocks
    private UpdateAirportRequestConverter converter;

    @Test
    void convertUpdateAirportRequestToAirportSuccess() {
        var addressMock = mock(Address.class);
        var addressDtoMock = mock(AddressDto.class);
        var request = new UpdateAirportRequest("Шереметьево", true, addressDtoMock);

        when(addressDtoConverter.convert(addressDtoMock)).thenReturn(addressMock);

        var result = converter.convert(request);

        assertNotNull(result);
        assertEquals("Шереметьево", result.getName());
        assertTrue(result.isActive());
        assertEquals(addressMock, result.getAddress());
        verify(addressDtoConverter).convert(addressDtoMock);
    }
}
