package unit.converter.flight;

import converter.airport.AirportConverter;
import converter.flight.FlightConverter;
import domain.Airport;
import domain.Flight;
import dto.airport.AirportDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightConverterTest {
    @Mock
    private AirportConverter airportConverter;
    @InjectMocks
    private FlightConverter flightConverter;

    @Test
    void convertFlightToFlightDtoSuccess() {
        var arrivalAirportMock = mock(Airport.class);
        var departureAirportMock = mock(Airport.class);
        var arrivalAirportDtoMock = mock(AirportDto.class);
        var departureAirportDtoMock = mock(AirportDto.class);
        var departureDate = LocalDateTime.now();
        var arrivalDate = LocalDateTime.now().plusDays(2);
        var flight = new Flight();
        flight.setId(1L);
        flight.setAllSeats(100);
        flight.setFreeSeats(90);
        flight.setArrivalAirport(arrivalAirportMock);
        flight.setDepartureAirport(departureAirportMock);
        flight.setDepartureDate(departureDate);
        flight.setArrivalDate(arrivalDate);
        when(airportConverter.convert(departureAirportMock)).thenReturn(departureAirportDtoMock);
        when(airportConverter.convert(arrivalAirportMock)).thenReturn(arrivalAirportDtoMock);

        var result = flightConverter.convert(flight);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(100, result.allSeats());
        assertEquals(90, result.freeSeats());
        assertEquals(departureAirportDtoMock, result.departureAirportDto());
        assertEquals(arrivalAirportDtoMock, result.arrivalAirportDto());
        assertEquals(departureDate, result.departureDate());
        assertEquals(arrivalDate, result.arrivalDate());
        verify(airportConverter).convert(arrivalAirportMock);
        verify(airportConverter).convert(departureAirportMock);
    }

    @Test
    void convertFlightListToFlightDtoListSuccess() {
        var flightOne = new Flight();
        var flightTwo = new Flight();

        var dtoList = flightConverter.convertAll(List.of(flightOne, flightTwo));

        assertNotNull(dtoList);
        assertEquals(2, dtoList.size());
        verify(airportConverter, times(4)).convert(any());
    }
}
