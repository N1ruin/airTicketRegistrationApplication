package unit.converter.airport;

import converter.airport.AirportDtoConverter;
import dto.airport.AirportDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirportDtoConverterTest {
    private final AirportDtoConverter airportDtoConverter = new AirportDtoConverter();

    @Test
    void convertAirportDtoToAirportSuccess() {
        var airportDto = new AirportDto("SVO", "Шереметьево", 1L);

        var result = airportDtoConverter.convert(airportDto);

        assertNotNull(result);
        assertEquals("SVO", result.getCode());
        assertEquals("Шереметьево", result.getName());
        assertNull(result.getAddress());
    }
}
