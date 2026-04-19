package unit.converter.flight;

import converter.airport.AirportDtoConverter;
import converter.flight.FlightDtoConverter;
import domain.Airport;
import dto.airport.AirportDto;
import dto.flight.FlightDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightDtoConverterTest {
    @Mock
    private AirportDtoConverter airportDtoConverter;
    @InjectMocks
    private FlightDtoConverter flightDtoConverter;

    @Test
    void convertFlightDtoToFlightSuccess() {
        var arrivalAirportMock = mock(Airport.class);
        var departureAirportMock = mock(Airport.class);
        var arrivalAirportDtoMock = mock(AirportDto.class);
        var departureAirportDtoMock = mock(AirportDto.class);
        var departureDate = LocalDateTime.now();
        var arrivalDate = LocalDateTime.now().plusDays(2);
        var flightDto = new FlightDto(1L, 100, 90, departureAirportDtoMock, arrivalAirportDtoMock,
                departureDate, arrivalDate);
        when(airportDtoConverter.convert(departureAirportDtoMock)).thenReturn(departureAirportMock);
        when(airportDtoConverter.convert(arrivalAirportDtoMock)).thenReturn(arrivalAirportMock);

        var result = flightDtoConverter.convert(flightDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100, result.getAllSeats());
        assertEquals(90, result.getFreeSeats());
        assertEquals(departureAirportMock, result.getDepartureAirport());
        assertEquals(arrivalAirportMock, result.getArrivalAirport());
        assertEquals(departureDate, result.getDepartureDate());
        assertEquals(arrivalDate, result.getArrivalDate());
        verify(airportDtoConverter).convert(departureAirportDtoMock);
        verify(airportDtoConverter).convert(arrivalAirportDtoMock);
    }
}
