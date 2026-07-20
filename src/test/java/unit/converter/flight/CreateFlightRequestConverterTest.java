package unit.converter.flight;

import converter.flight.CreateFlightRequestConverter;
import dto.flight.CreateFlightRequest;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CreateFlightRequestConverterTest {
    private final CreateFlightRequestConverter converter = new CreateFlightRequestConverter();

    @Test
    void convertRequestToFlightSuccess() {
        String departureAirportCode = "SVO";
        String arrivalAirportCode = "DME";
        var departureDate = ZonedDateTime.now();
        var arrivalDate = ZonedDateTime.now().plusDays(1);
        var request = new CreateFlightRequest(100, 90, departureAirportCode, arrivalAirportCode,
                departureDate, arrivalDate);

        var result = converter.convert(request);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(100, result.getSeatsCount());
        assertEquals(90, result.getFreeSeats());
        assertEquals(departureAirportCode, result.getDepartureAirportId());
        assertEquals(arrivalAirportCode, result.getArrivalAirportId());
        assertEquals(departureDate, result.getDepartureDate());
        assertEquals(arrivalDate, result.getArrivalDate());
    }
}
