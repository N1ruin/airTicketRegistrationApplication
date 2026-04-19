package unit.converter.airport;

import converter.address.AddressDtoConverter;
import converter.airport.AirportDtoConverter;
import domain.Address;
import dto.address.AddressDto;
import dto.airport.AirportDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AirportDtoConverterTest {
    @Mock
    private AddressDtoConverter addressDtoConverter;
    @InjectMocks
    private AirportDtoConverter airportDtoConverter;

    @Test
    void convertAirportDtoToAirportSuccess() {
        var addressMock = mock(Address.class);
        var airportDtoAddressMock = mock(AddressDto.class);
        var airportDto = new AirportDto(1L, "SVO", "Шереметьево", airportDtoAddressMock);

        when(addressDtoConverter.convert(airportDtoAddressMock)).thenReturn(addressMock);

        var result = airportDtoConverter.convert(airportDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("SVO", result.getCode());
        assertEquals("Шереметьево", result.getName());
        assertEquals(addressMock, result.getAddress());
        verify(addressDtoConverter).convert(airportDtoAddressMock);
    }
}
