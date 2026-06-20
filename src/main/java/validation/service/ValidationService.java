package validation.service;

import exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Set;
import java.util.stream.Collectors;

public class ValidationService {
    private final Validator validator;

    public ValidationService(Validator validator) {
        this.validator = validator;
    }

    public <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (!violations.isEmpty()) {
            var errorMessage = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            throw new ValidationException(errorMessage);
        }
    }
}
