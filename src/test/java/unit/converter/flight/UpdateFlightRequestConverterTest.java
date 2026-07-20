package unit.converter.flight;

import converter.flight.UpdateFlightRequestConverter;
import dto.flight.UpdateFlightRequest;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UpdateFlightRequestConverterTest {
    private final UpdateFlightRequestConverter converter = new UpdateFlightRequestConverter();

    @Test
    void convertRequestToFlightSuccess() {
        var departureDate = ZonedDateTime.now();
        var arrivalDate = ZonedDateTime.now().plusDays(1);
        var request = new UpdateFlightRequest(1L, 100, 90, "SVO", "DME",
                departureDate, arrivalDate);

        var result = converter.convert(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100, result.getSeatsCount());
        assertEquals(90, result.getFreeSeats());
        assertEquals("SVO", result.getDepartureAirportId());
        assertEquals("DME", result.getArrivalAirportId());
        assertEquals(departureDate, result.getDepartureDate());
        assertEquals(arrivalDate, result.getArrivalDate());
    }
}
