package unit.converter.flight;

import converter.flight.FlightConverter;
import domain.Flight;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlightConverterTest {
    private final FlightConverter flightConverter = new FlightConverter();

    @Test
    void convertFlightToFlightDtoSuccess() {
        var departureDate = ZonedDateTime.now();
        var arrivalDate = ZonedDateTime.now().plusDays(2);
        var flight = new Flight();
        flight.setId(1L);
        flight.setAllSeats(100);
        flight.setFreeSeats(90);
        flight.setDepartureAirportCode("SVO");
        flight.setArrivalAirportCode("DME");
        flight.setDepartureDate(departureDate);
        flight.setArrivalDate(arrivalDate);

        var result = flightConverter.convert(flight);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(100, result.allSeats());
        assertEquals(90, result.freeSeats());
        assertEquals("SVO", result.departureAirportCode());
        assertEquals("DME", result.arrivalAirportCode());
        assertEquals(departureDate, result.departureDate());
        assertEquals(arrivalDate, result.arrivalDate());
    }

    @Test
    void convertFlightListToFlightDtoListSuccess() {
        var flightOne = new Flight();
        flightOne.setDepartureAirportCode("SVO");
        flightOne.setArrivalAirportCode("DME");
        var flightTwo = new Flight();
        flightTwo.setDepartureAirportCode("LED");
        flightTwo.setArrivalAirportCode("MSQ");

        var dtoList = flightConverter.convertAll(List.of(flightOne, flightTwo));

        assertNotNull(dtoList);
        assertEquals(2, dtoList.size());
        assertEquals("SVO", dtoList.get(0).departureAirportCode());
        assertEquals("DME", dtoList.get(0).arrivalAirportCode());
        assertEquals("LED", dtoList.get(1).departureAirportCode());
        assertEquals("MSQ", dtoList.get(1).arrivalAirportCode());
    }
}
