package unit.converter.ticket;

import converter.ticket.CreateTicketRequestConverter;
import domain.TicketRank;
import dto.ticket.CreateTicketRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreateTicketRequestConverterTest {
    private final CreateTicketRequestConverter converter = new CreateTicketRequestConverter();

    @Test
    void convertRequestToTicketSuccess() {
        Long flightId = 1L;
        Long passengerId = 2L;
        var request = new CreateTicketRequest(
                TicketRank.STANDARD,
                10,
                flightId,
                passengerId,
                10.0,
                5.0);

        var result = converter.convert(request);

        assertNotNull(result);
        assertEquals(flightId, result.getFlightId());
        assertEquals(passengerId, result.getPassengerId());
        assertEquals(TicketRank.STANDARD, result.getTicketRank());
        assertEquals(10, result.getSeatNumber());
        assertEquals(10.0, result.getBaggageWeight());
        assertEquals(5.0, result.getCarryOnBaggageWeight());
    }
}
