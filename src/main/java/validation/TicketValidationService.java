package validation;

import dto.ticket.CreateTicketRequest;
import exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class TicketValidationService {

    public void validateCreateRequest(CreateTicketRequest request) {
        List<String> errors = new ArrayList<>();

        validateSeatNumber(request.seatNumber(), errors);
        validateBaggageWeight(request.baggageWeight(), errors);
        validateBaggageWeight(request.carryOnBaggageWeight(), errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void validateSeatNumber(Integer seatNumber, List<String> errors) {
        if (seatNumber == null) {
            errors.add("Seat number cannot be null");
            return;
        }

        if (seatNumber <= 0) {
            errors.add("The seat number cannot be negative.");
        }
    }

    private void validateBaggageWeight(Double baggageWeight, List<String> errors) {
        if (baggageWeight == null) {
            errors.add("Baggage weight cannot be null");
            return;
        }

        if (baggageWeight < 0) {
            errors.add("Carry on baggage weight cannot be null");
        }
    }
}
