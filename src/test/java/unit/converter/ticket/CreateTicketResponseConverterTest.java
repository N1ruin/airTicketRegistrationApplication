package unit.converter.ticket;

import converter.flight.FlightConverter;
import converter.passenger.PassengerDtoConverter;
import converter.ticket.CreateTicketResponseConverter;
import domain.Flight;
import domain.Passenger;
import domain.ServiceClass;
import domain.Ticket;
import dto.flight.FlightDto;
import dto.passenger.PassengerDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateTicketResponseConverterTest {
    @Mock
    private FlightConverter flightConverter;
    @Mock
    private PassengerDtoConverter passengerDtoConverter;
    @InjectMocks
    private CreateTicketResponseConverter converter;

    @Test
    void convertTicketToResponseSuccess() {
        var flightMock = mock(Flight.class);
        var flightDtoMock = mock(FlightDto.class);
        var passengerMock = mock(Passenger.class);
        var passengerDtoMock = mock(PassengerDto.class);
        var purchaseDate = LocalDateTime.now();
        var ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketNumber(5L);
        ticket.setServiceClass(ServiceClass.STANDARD);
        ticket.setSeatNumber(10);
        ticket.setFlight(flightMock);
        ticket.setPassenger(passengerMock);
        ticket.setPurchaseDate(purchaseDate);
        ticket.setBaggageWeight(10.0);
        ticket.setCarryOnBaggageWeight(5.0);
        doReturn(flightDtoMock).when(flightConverter).convert(flightMock);
        doReturn(passengerDtoMock).when(passengerDtoConverter).convert(passengerMock);

        var result = converter.convert(ticket);

        assertNotNull(result);
        assertEquals(flightDtoMock, result.flight());
        assertEquals(passengerDtoMock, result.passenger());
        assertEquals(ServiceClass.STANDARD.name(), result.serviceClass());
        assertEquals(10, result.seatNumber());
        assertEquals(10.0, result.baggageWeight());
        assertEquals(5.0, result.carryOnBaggageWeight());
        verify(flightConverter).convert(flightMock);
        verify(passengerDtoConverter).convert(passengerMock);
    }
}
