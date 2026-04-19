package unit.converter.airport;

import converter.address.AddressConverter;
import converter.airport.UpdateAirportResponseConverter;
import domain.Address;
import domain.Airport;
import dto.address.AddressDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UpdateAirportResponseConverterTest {
    @Mock
    private AddressConverter addressConverter;
    @InjectMocks
    private UpdateAirportResponseConverter converter;

    @Test
    void convertAirportToDtoSuccess() {
        var addressMock = mock(Address.class);
        var addressDtoMock = mock(AddressDto.class);
        var airport = new Airport();
        airport.setId(1L);
        airport.setCode("SVO");
        airport.setName("Шереметьево");
        airport.setAddress(addressMock);

        when(addressConverter.convert(addressMock)).thenReturn(addressDtoMock);

        var result = converter.convert(airport);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("SVO", result.code());
        assertEquals("Шереметьево", result.name());
        assertEquals(addressDtoMock, result.addressDto());
        verify(addressConverter).convert(addressMock);
    }
}
