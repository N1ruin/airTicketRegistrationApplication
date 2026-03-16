package validation;

import dto.user.UserSignUpRequest;

import java.util.ArrayList;
import java.util.List;

public class UserValidationService {
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9]+.[a-zA-Z]{2,}$";
    private static final String PASSWORD_PATTERN = "^.{5,}$";

    public List<String> validateSignUpRequest(UserSignUpRequest request) {
        List<String> errorMessages = new ArrayList<>();

        validateEmail(request.email(), errorMessages);
        validatePassword(request.password(), errorMessages);
        validateName(request.firstName(), "First name", errorMessages);
        validateName(request.lastName(), "Last name", errorMessages);
        validateName(request.fatherName(), "Father name", errorMessages);

        return errorMessages;
    }

    private void validateEmail(String email, List<String> errorMessages) {
        if (email == null || !email.matches(EMAIL_PATTERN)) {
            errorMessages.add("Invalid email format");
        }
    }

    private void validatePassword(String password, List<String> errorMessages) {
        if (password == null || password.isBlank() || !password.matches(PASSWORD_PATTERN)) {
            errorMessages.add("Invalid password. Min 5 symbols, not empty, not spaces");
        }
    }

    private void validateName(String name, String fieldName, List<String> errorMessages) {
        if (name == null || name.isBlank()) {
            errorMessages.add("%s must not be empty and without spaces".formatted(fieldName));
        }
    }

}
