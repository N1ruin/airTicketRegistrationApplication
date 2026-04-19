package unit.converter.flight;

import converter.airport.AirportDtoConverter;
import converter.flight.CreateFlightRequestConverter;
import domain.Airport;
import dto.airport.AirportDto;
import dto.flight.CreateFlightRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CreateFlightRequestConverterTest {
    @Mock
    private AirportDtoConverter airportDtoConverter;
    @InjectMocks
    private CreateFlightRequestConverter converter;

    @Test
    void convertRequestToFlightSuccess() {
        var departureAirportDtoMock = mock(AirportDto.class);
        var departureAirportMock = mock(Airport.class);
        var arrivalAirportDtoMock = mock(AirportDto.class);
        var arrivalAirportMock = mock(Airport.class);
        var departureDate = LocalDateTime.now();
        var arrivalDate = LocalDateTime.now().plusDays(1);
        var request = new CreateFlightRequest(100, 90, departureAirportDtoMock, arrivalAirportDtoMock,
                departureDate, arrivalDate);

        when(airportDtoConverter.convert(departureAirportDtoMock)).thenReturn(departureAirportMock);
        when(airportDtoConverter.convert(arrivalAirportDtoMock)).thenReturn(arrivalAirportMock);

        var result = converter.convert(request);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(100, result.getAllSeats());
        assertEquals(90, result.getFreeSeats());
        assertEquals(departureAirportMock, result.getDepartureAirport());
        assertEquals(arrivalAirportMock, result.getArrivalAirport());
        assertEquals(departureDate, result.getDepartureDate());
        assertEquals(arrivalDate, result.getArrivalDate());
        verify(airportDtoConverter).convert(arrivalAirportDtoMock);
        verify(airportDtoConverter).convert(departureAirportDtoMock);
    }
}
