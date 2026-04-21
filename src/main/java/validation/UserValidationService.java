package validation;

import dto.user.UpdateUserRequest;
import dto.user.UserSignUpRequest;
import exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class UserValidationService {
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9]+.[a-zA-Z]{2,}$";
    private static final String PASSWORD_PATTERN = "^.{5,}$";

    public void validateSignUpRequest(UserSignUpRequest request) {
        List<String> errors = new ArrayList<>();

        validateEmail(request.email(), errors);
        validatePassword(request.password(), errors);
        validateName(request.firstName(), "First name", errors);
        validateName(request.lastName(), "Last name", errors);
        validateName(request.fatherName(), "Father name", errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public void validateUpdateRequest(UpdateUserRequest request) {
        List<String> errors = new ArrayList<>();

        validateUpdatedPassword(request.newPassword(), errors);
        validateName(request.firstName(), "First name", errors);
        validateName(request.lastName(), "Last name", errors);
        validateName(request.fatherName(), "Father name", errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void validateEmail(String email, List<String> errors) {
        if (email == null || !email.matches(EMAIL_PATTERN)) {
            errors.add("Invalid email format");
        }
    }

    private void validateUpdatedPassword(String password, List<String> errors) {
        if (password == null) {
            return;
        }

        validatePassword(password, errors);
    }

    private void validatePassword(String password, List<String> errors) {
        if (password == null || password.isBlank() || !password.matches(PASSWORD_PATTERN)) {
            errors.add("Invalid password. Min 5 symbols, not empty, not spaces");
        }
    }

    private void validateName(String name, String fieldName, List<String> errors) {
        if (name == null || name.isBlank()) {
            errors.add("%s must not be empty and without spaces".formatted(fieldName));
        }
    }
}
