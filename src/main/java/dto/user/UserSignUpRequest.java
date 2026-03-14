package dto.user;

public record UserSignUpRequest(String email,
                                String password,
                                String firstName,
                                String lastName,
                                String fatherName) {
}
