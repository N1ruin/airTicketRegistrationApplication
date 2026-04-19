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
    private static final String NAME_PATTERN = "^[a-zA-Zа-яА-ЯёЁ]+([\\s-][a-zA-Zа-яА-ЯёЁ]+)*$";

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

        validateFirstName(request.firstName(), errors);
        validateLastName(request.lastName(), errors);
        validateFatherName(request.fatherName(), errors);
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

        validateFirstName(request.firstName(), errors);
        validateLastName(request.lastName(), errors);
        validateFatherName(request.fatherName(), errors);
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

    private void validateFirstName(String name, List<String> errorMessages) {
        if (name == null || name.isBlank()) {
            errorMessages.add("%s must not be empty and without spaces".formatted(name));
            return;
        }

        if (!name.matches(NAME_PATTERN)) {
            errorMessages.add("First name invalid format");
        }
    }

    private void validateLastName(String name, List<String> errorMessages) {
        if (name == null || name.isBlank()) {
            errorMessages.add("%s must not be empty and without spaces".formatted(name));
            return;
        }

        if (!name.matches(NAME_PATTERN)) {
            errorMessages.add("Last name invalid format");
        }
    }

    private void validateFatherName(String name, List<String> errorMessages) {
        if (name == null || name.isBlank()) {
            return;
        }

        if (!name.matches(NAME_PATTERN)) {
            errorMessages.add("Father name invalid format");
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
