package unit.converter.flight;

import converter.flight.FlightDtoConverter;
import dto.flight.FlightDto;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FlightDtoConverterTest {
    private final FlightDtoConverter flightDtoConverter = new FlightDtoConverter();

    @Test
    void convertFlightDtoToFlightSuccess() {
        var departureDate = ZonedDateTime.now();
        var arrivalDate = ZonedDateTime.now().plusDays(2);
        var flightDto = new FlightDto(1L, 100, 90, "SVO", "DME",
                departureDate, arrivalDate);

        var result = flightDtoConverter.convert(flightDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100, result.getAllSeats());
        assertEquals(90, result.getFreeSeats());
        assertEquals("SVO", result.getDepartureAirportCode());
        assertEquals("DME", result.getArrivalAirportCode());
        assertEquals(departureDate, result.getDepartureDate());
        assertEquals(arrivalDate, result.getArrivalDate());
    }
}
