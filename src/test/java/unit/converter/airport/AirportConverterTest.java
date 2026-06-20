package unit.converter.airport;

import converter.airport.AirportConverter;
import domain.Address;
import domain.Airport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirportConverterTest {
    private final AirportConverter airportConverter = new AirportConverter();

    @Test
    void convertAirportToDtoSuccess() {
        var address = mock(Address.class);
        var airport = new Airport();
        airport.setCode("SVO");
        airport.setName("Шереметьево");
        airport.setAddress(address);
        var addressId = 123L;
        when(address.getId()).thenReturn(addressId);

        var result = airportConverter.convert(airport);

        assertNotNull(result);
        assertEquals("SVO", result.code());
        assertEquals("Шереметьево", result.name());
        assertEquals(addressId, result.addressId());
        verify(address, times(1)).getId();
    }


    @Test
    void convertListAirportsSuccess() {
        var airportSVO = new Airport();
        airportSVO.setCode("SVO");
        airportSVO.setName("Шереметьево");
        var svoAddress = mock(Address.class);
        airportSVO.setAddress(svoAddress);
        var airportDME = new Airport();
        airportDME.setCode("DME");
        airportDME.setName("Домодедово");
        var dmeAddress = mock(Address.class);
        airportDME.setAddress(dmeAddress);
        Long svoAddressId = 1L;
        Long dmeAddressId = 2L;
        when(svoAddress.getId()).thenReturn(svoAddressId);
        when(dmeAddress.getId()).thenReturn(dmeAddressId);
        var resultList = List.of(airportSVO, airportDME);

        var result = airportConverter.convertAll(resultList);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("SVO", result.get(0).code());
        assertEquals(svoAddressId, result.get(0).addressId());
        assertEquals("DME", result.get(1).code());
        assertEquals(dmeAddressId, result.get(1).addressId());
        verify(svoAddress, times(1)).getId();
        verify(dmeAddress, times(1)).getId();
    }
}
