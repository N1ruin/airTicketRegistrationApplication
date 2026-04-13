package validation;

import dto.flight.CreateFlightRequest;
import dto.flight.UpdateFlightRequest;
import exception.ValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlightValidationService {
    private final RequestParameterValidationService requestParameterValidationService;

    public FlightValidationService(RequestParameterValidationService requestParameterValidationService) {
        this.requestParameterValidationService = requestParameterValidationService;
    }

    public void validateCreateRequest(CreateFlightRequest request) {
        List<String> errors = new ArrayList<>();

        validateAllSeatsCount(request.allSeats(), errors);
        validateFreeSeatsCount(request.freeSeats(), request.allSeats(), errors);
        validateDepartureDate(request.departureDate(), errors);
        validateArrivalDate(request.arrivalDate(), request.departureDate(), errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public void validateUpdateRequest(UpdateFlightRequest request) {
        List<String> errors = new ArrayList<>();

        try {
            requestParameterValidationService.validateId(request.id());
        } catch (ValidationException e) {
            errors.add(e.getMessage());
        }

        validateAllSeatsCount(request.allSeats(), errors);
        validateFreeSeatsCount(request.freeSeats(), request.allSeats(), errors);
        validateDepartureDate(request.departureDate(), errors);
        validateArrivalDate(request.arrivalDate(), request.departureDate(), errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void validateAllSeatsCount(Integer allSeats, List<String> errors) {
        if (allSeats == null) {
            errors.add("All seats cannot be null");
            return;
        }

        if (allSeats < 0) {
            errors.add("The seat number cannot be negative.");
        }
    }

    private void validateFreeSeatsCount(Integer freeSeats, Integer allSeats, List<String> errors) {
        if (freeSeats == null) {
            errors.add("Free seats cannot be null");
            return;
        }

        if (freeSeats < 0) {
            errors.add("Free seats cannot be negative");
        }

        if (freeSeats > allSeats) {
            errors.add("The number of available seats cannot exceed the total number of seats");
        }
    }

    private void validateDepartureDate(LocalDateTime departureDate, List<String> errors) {
        if (departureDate == null) {
            errors.add("Departure date cannot be null");
            return;
        }

        if (departureDate.isBefore(LocalDateTime.now())) {
            errors.add("The departure date cannot be in the past");
        }
    }

    private void validateArrivalDate(LocalDateTime arrivalDate, LocalDateTime departureDate, List<String> errors) {
        if (arrivalDate == null) {
            errors.add("Arrival date cannot be null");
            return;
        }

        if (arrivalDate.isBefore(LocalDateTime.now())) {
            errors.add("The arrival date cannot be in the past");
        }

        if (arrivalDate.isBefore(departureDate)) {
            errors.add("The arrival date cannot be earlier than the departure date");
        }
    }
}
