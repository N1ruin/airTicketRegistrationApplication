package validation.service;

import exception.ValidationException;

public class RequestParameterValidationService {
    public void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Id must be a positive number, but was: %d".formatted(id));
        }
    }
}
