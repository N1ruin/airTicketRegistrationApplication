package unit.converter.airport;

import converter.address.AddressDtoConverter;
import converter.airport.UpdateAirportRequestConverter;
import domain.Address;
import dto.address.AddressDto;
import dto.airport.UpdateAirportRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UpdateAirportRequestConverterTest {
    @Mock
    private AddressDtoConverter addressDtoConverter;
    @InjectMocks
    private UpdateAirportRequestConverter converter;

    @Test
    void convertAirportToDtoSuccess() {
        var addressMock = mock(Address.class);
        var addressDtoMock = mock(AddressDto.class);
        var request = new UpdateAirportRequest(1L, "SVO", "Шереметьево", addressDtoMock);

        when(addressDtoConverter.convert(addressDtoMock)).thenReturn(addressMock);

        var result = converter.convert(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("SVO", result.getCode());
        assertEquals("Шереметьево", result.getName());
        assertEquals(addressMock, result.getAddress());
        verify(addressDtoConverter).convert(addressDtoMock);
    }
}
