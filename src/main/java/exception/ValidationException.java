package exception;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends ApplicationException {
    private final List<String> validationErrorMessages = new ArrayList<>();

    public ValidationException(List<String> validationErrorMessages) {
        super("Validation error");
        this.validationErrorMessages.addAll(validationErrorMessages);
    }

    public ValidationException(String message) {
        super("Validation error");
        this.validationErrorMessages.add(message);
    }

    @Override
    public String getMessage() {
        return validationErrorMessages.toString();
    }

    public List<String> getValidationErrorMessages() {
        return validationErrorMessages;
    }
}
