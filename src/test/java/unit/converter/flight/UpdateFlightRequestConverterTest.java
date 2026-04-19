package unit.converter.flight;

import converter.flight.UpdateFlightRequestConverter;
import dto.airport.AirportDto;
import dto.flight.UpdateFlightRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class UpdateFlightRequestConverterTest {
    private final UpdateFlightRequestConverter converter = new UpdateFlightRequestConverter();

    @Test
    void convertRequestToFlightSuccess() {
        var departureAirportDtoMock = mock(AirportDto.class);
        var arrivalAirportDtoMock = mock(AirportDto.class);
        var departureDate = LocalDateTime.now();
        var arrivalDate = LocalDateTime.now().plusDays(1);
        var request = new UpdateFlightRequest(1L, 100, 90, departureAirportDtoMock, arrivalAirportDtoMock,
                departureDate, arrivalDate);

        var result = converter.convert(request);

        assertNotNull(request);
        assertEquals(1L, result.getId());
        assertEquals(100, result.getAllSeats());
        assertEquals(90, result.getFreeSeats());
        assertEquals(departureDate, result.getDepartureDate());
        assertEquals(arrivalDate, result.getArrivalDate());
    }
}
