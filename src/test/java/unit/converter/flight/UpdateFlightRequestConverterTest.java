package unit.converter.flight;

import converter.airport.AirportDtoConverter;
import converter.flight.UpdateFlightRequestConverter;
import domain.Airport;
import dto.airport.AirportDto;
import dto.flight.UpdateFlightRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateFlightRequestConverterTest {
    @Mock
    private AirportDtoConverter airportDtoConverter;
    @InjectMocks
    private UpdateFlightRequestConverter converter;

    @Test
    void convertRequestToFlightSuccess() {
        var departureAirportDtoMock = mock(AirportDto.class);
        var arrivalAirportDtoMock = mock(AirportDto.class);
        var departureAirportMock = mock(Airport.class);
        var arrivalAirportMock = mock(Airport.class);
        var departureDate = LocalDateTime.now();
        var arrivalDate = LocalDateTime.now().plusDays(1);
        var request = new UpdateFlightRequest(1L, 100, 90, departureAirportDtoMock, arrivalAirportDtoMock,
                departureDate, arrivalDate);
        when(airportDtoConverter.convert(departureAirportDtoMock)).thenReturn(departureAirportMock);
        when(airportDtoConverter.convert(arrivalAirportDtoMock)).thenReturn(arrivalAirportMock);

        var result = converter.convert(request);

        assertNotNull(request);
        assertEquals(1L, result.getId());
        assertEquals(100, result.getAllSeats());
        assertEquals(90, result.getFreeSeats());
        assertEquals(departureAirportMock, result.getDepartureAirport());
        assertEquals(arrivalAirportMock, result.getArrivalAirport());
        assertEquals(departureDate, result.getDepartureDate());
        assertEquals(arrivalDate, result.getArrivalDate());
    }
}
