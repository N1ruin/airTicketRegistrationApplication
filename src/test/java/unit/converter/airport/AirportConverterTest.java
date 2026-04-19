package unit.converter.airport;

import converter.address.AddressConverter;
import converter.airport.AirportConverter;
import domain.Address;
import domain.Airport;
import dto.address.AddressDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirportConverterTest {
    @Mock
    private AddressConverter addressConverter;
    @InjectMocks
    private AirportConverter airportConverter;

    @Test
    void convertAirportToDtoSuccess() {
        var address = mock(Address.class);
        var airport = new Airport();
        airport.setId(1L);
        airport.setCode("SVO");
        airport.setName("Шереметьево");
        airport.setAddress(address);
        var addressDto = mock(AddressDto.class);

        when(addressConverter.convert(address)).thenReturn(addressDto);

        var result = airportConverter.convert(airport);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("SVO", result.code());
        assertEquals("Шереметьево", result.name());
        assertEquals(addressDto, result.address());
        verify(addressConverter).convert(address);
    }


    @Test
    void convertListAirportsSuccess() {
        var airportSVO = new Airport();
        airportSVO.setId(1L);
        airportSVO.setCode("SVO");
        airportSVO.setName("Шереметьево");
        var svoAddress = mock(Address.class);
        airportSVO.setAddress(svoAddress);
        var airportDME = new Airport();
        airportDME.setId(1L);
        airportDME.setCode("DME");
        airportDME.setName("Домодедово");
        var dmeAddress = mock(Address.class);
        airportDME.setAddress(dmeAddress);
        var resultList = List.of(airportSVO, airportDME);
        var dmeAddressDtoMock = mock(AddressDto.class);
        var svoAddressDtoMock = mock(AddressDto.class);

        when(addressConverter.convert(svoAddress)).thenReturn(svoAddressDtoMock);
        when(addressConverter.convert(dmeAddress)).thenReturn(dmeAddressDtoMock);

        var result = airportConverter.convertAll(resultList);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("SVO", result.get(0).code());
        assertEquals(svoAddressDtoMock, result.get(0).address());
        assertEquals("DME", result.get(1).code());
        assertEquals(dmeAddressDtoMock, result.get(1).address());
        verify(addressConverter).convert(svoAddress);
        verify(addressConverter).convert(dmeAddress);
    }
}
