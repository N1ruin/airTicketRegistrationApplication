package validation;

import dto.airport.CreateAirportRequest;
import dto.airport.UpdateAirportRequest;
import exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class AirportValidationService {
    private static final String NAME_PATTERN = "^[a-zA-Z0-9.\\s-]{3,100}$";
    private static final String CODE_PATTERN = "^[A-Z]{3}$";
    private final AddressValidationService addressValidationService;
    private final RequestParameterValidationService requestParameterValidationService;

    public AirportValidationService(AddressValidationService addressValidationService,
                                    RequestParameterValidationService requestParameterValidationService) {
        this.addressValidationService = addressValidationService;
        this.requestParameterValidationService = requestParameterValidationService;
    }

    public void validateCreateRequest(CreateAirportRequest request) {
        List<String> errors = new ArrayList<>();

        validateName(request.name(), errors);
        validateCode(request.code(), errors);
        addressValidationService.validate(request.addressDto(), errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public void validateUpdateRequest(UpdateAirportRequest request) {
        List<String> errors = new ArrayList<>();

        try {
            requestParameterValidationService.validateId(request.id());
        } catch (ValidationException e) {
            errors.add(e.getMessage());
        }
        if (request.name() != null) {
            validateName(request.name(), errors);
        } else {
            errors.add("Airport name cannot be null");
        }
        validateCode(request.code(), errors);
        addressValidationService.validate(request.addressDto(), errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void validateName(String name, List<String> errors) {
        if (name == null || !name.matches(NAME_PATTERN)) {
            var errorMessage = "Invalid name format. The name must contain english uppercase or capital " +
                               "letters, periods, spaces, hyphens. Length from 3 to 100 characters";
            errors.add(errorMessage);
        }
    }

    private void validateCode(String code, List<String> errors) {
        if (code == null || !code.matches(CODE_PATTERN)) {
            var errorMessage = "Invalid code format. Code must contain three english uppercase letters.";
            errors.add(errorMessage);
        }
    }
}
