package validation;

import exception.ValidationException;

import java.util.List;

public class RequestParameterValidationService {
    public void validateId(Long id) {
        if (id == null || id < 0) {
            throw new ValidationException(List.of("ID must be a positive number, but was: %d".formatted(id)));
        }
    }
}
