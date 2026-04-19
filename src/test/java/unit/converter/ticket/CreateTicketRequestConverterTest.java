package unit.converter.ticket;

import converter.flight.FlightDtoConverter;
import converter.passenger.PassengerConverter;
import converter.ticket.CreateTicketRequestConverter;
import domain.Flight;
import domain.Passenger;
import domain.ServiceClass;
import dto.flight.FlightDto;
import dto.passenger.PassengerDto;
import dto.ticket.CreateTicketRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTicketRequestConverterTest {
    @Mock
    private FlightDtoConverter flightDtoConverter;
    @Mock
    private PassengerConverter passengerConverter;
    @InjectMocks
    private CreateTicketRequestConverter converter;

    @Test
    void convertRequestToTicketSuccess() {
        var flightMock = mock(Flight.class);
        var flightDtoMock = mock(FlightDto.class);
        var passengerMock = mock(Passenger.class);
        var passengerDtoMock = mock(PassengerDto.class);
        var request = new CreateTicketRequest(ServiceClass.STANDARD.name(), 10, flightDtoMock, passengerDtoMock,
                10.0, 5.0);
        doReturn(flightMock).when(flightDtoConverter).convert(flightDtoMock);
        doReturn(passengerMock).when(passengerConverter).convert(passengerDtoMock);

        var result = converter.convert(request);

        assertNotNull(result);
        assertEquals(flightMock, result.getFlight());
        assertEquals(passengerMock, result.getPassenger());
        assertEquals(ServiceClass.STANDARD, result.getServiceClass());
        assertEquals(10, result.getSeatNumber());
        assertEquals(10.0, result.getBaggageWeight());
        assertEquals(5.0, result.getCarryOnBaggageWeight());
        verify(flightDtoConverter).convert(flightDtoMock);
        verify(passengerConverter).convert(passengerDtoMock);
    }
}
