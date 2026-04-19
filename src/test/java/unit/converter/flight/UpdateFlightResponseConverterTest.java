package unit.converter.flight;

import converter.airport.AirportConverter;
import converter.flight.UpdateFlightResponseConverter;
import domain.Airport;
import domain.Flight;
import dto.airport.AirportDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateFlightResponseConverterTest {
    @Mock
    private AirportConverter airportConverter;
    @InjectMocks
    private UpdateFlightResponseConverter converter;

    @Test
    void convertFlightToResponseSuccess() {
        var departureAirportDtoMock = mock(AirportDto.class);
        var departureAirportMock = mock(Airport.class);
        var arrivalAirportDtoMock = mock(AirportDto.class);
        var arrivalAirportMock = mock(Airport.class);
        var departureDate = LocalDateTime.now();
        var arrivalDate = LocalDateTime.now().plusDays(1);
        var flight = new Flight();
        flight.setId(1L);
        flight.setAllSeats(100);
        flight.setFreeSeats(90);
        flight.setDepartureAirport(departureAirportMock);
        flight.setArrivalAirport(arrivalAirportMock);
        flight.setDepartureDate(departureDate);
        flight.setArrivalDate(arrivalDate);
        when(airportConverter.convert(departureAirportMock)).thenReturn(departureAirportDtoMock);
        when(airportConverter.convert(arrivalAirportMock)).thenReturn(arrivalAirportDtoMock);

        var result = converter.convert(flight);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(100, result.allSeats());
        assertEquals(90, result.freeSeats());
        assertEquals(departureAirportDtoMock, result.departureAirportDto());
        assertEquals(arrivalAirportDtoMock, result.arrivalAirportDto());
        assertEquals(departureDate, result.departureDate());
        assertEquals(arrivalDate, result.arrivalDate());
    }
}
