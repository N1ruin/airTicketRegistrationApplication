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
        flight.setSeatsCount(100);
        flight.setFreeSeats(90);
        flight.setDepartureAirportId("SVO");
        flight.setArrivalAirportId("DME");
        flight.setDepartureDate(departureDate);
        flight.setArrivalDate(arrivalDate);

        var result = flightConverter.convert(flight);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(100, result.seatsCount());
        assertEquals(90, result.freeSeats());
        assertEquals("SVO", result.departureAirportId());
        assertEquals("DME", result.arrivalAirportId());
        assertEquals(departureDate, result.departureDate());
        assertEquals(arrivalDate, result.arrivalDate());
    }

    @Test
    void convertFlightListToFlightDtoListSuccess() {
        var flightOne = new Flight();
        flightOne.setDepartureAirportId("SVO");
        flightOne.setArrivalAirportId("DME");
        var flightTwo = new Flight();
        flightTwo.setDepartureAirportId("LED");
        flightTwo.setArrivalAirportId("MSQ");

        var dtoList = flightConverter.convertAll(List.of(flightOne, flightTwo));

        assertNotNull(dtoList);
        assertEquals(2, dtoList.size());
        assertEquals("SVO", dtoList.get(0).departureAirportId());
        assertEquals("DME", dtoList.get(0).arrivalAirportId());
        assertEquals("LED", dtoList.get(1).departureAirportId());
        assertEquals("MSQ", dtoList.get(1).arrivalAirportId());
    }
}
