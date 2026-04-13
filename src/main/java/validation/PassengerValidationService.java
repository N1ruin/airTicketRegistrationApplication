package validation;


import dto.passenger.CreatePassengerRequest;
import dto.passenger.UpdatePassengerRequest;
import exception.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PassengerValidationService {
    private final RequestParameterValidationService requestParameterValidationService;
    private final PassportValidationService passportValidationService;

    public PassengerValidationService(RequestParameterValidationService requestParameterValidationService,
                                      PassportValidationService passportValidationService) {
        this.requestParameterValidationService = requestParameterValidationService;
        this.passportValidationService = passportValidationService;
    }

    public void validateCreateRequest(CreatePassengerRequest request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            throw new ValidationException("Request body is missing");
        }

        validateName(request.firstName(), "First name", errors);
        validateName(request.lastName(), "Last name", errors);
        validateName(request.fatherName(), "Father name", errors);
        passportValidationService.validatePassportDto(request.passportDto(), errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public void validateUpdateRequest(UpdatePassengerRequest request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            throw new ValidationException("Update request body is missing");
        }

        try {
            requestParameterValidationService.validateId(request.id());
        } catch (ValidationException e) {
            errors.add(e.getMessage());
        }

        validateName(request.firstName(), "First name", errors);
        validateName(request.lastName(), "Last name", errors);
        validateName(request.fatherName(), "Father name", errors);
        validateUserId(request.userId(), errors);
        validateBirthDate(request.birthDate(), errors);

        if (request.passportDto() == null) {
            errors.add("Passport data is required");
        } else {
            passportValidationService.validatePassportDto(request.passportDto(), errors);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void validateUserId(Long userId, List<String> errors) {
        if (userId == null) {
            errors.add("User id cannot be null");
            return;
        }

        if (userId < 0) {
            errors.add("User id cannot be negative");
        }
    }

    private void validateName(String name, String fieldName, List<String> errorMessages) {
        if (name == null || name.isBlank()) {
            errorMessages.add("%s must not be empty and without spaces".formatted(fieldName));
        }
    }

    private void validateBirthDate(LocalDate birthDate, List<String> errors) {
        if (birthDate == null) {
            errors.add("Birth date is required");
            return;
        }
        if (birthDate.isAfter(LocalDate.now())) {
            errors.add("Birth date cannot be in the future");
        }
        if (birthDate.isBefore(LocalDate.now().minusYears(120)))
            errors.add("Invalid birth date (too old)");
    }
}
