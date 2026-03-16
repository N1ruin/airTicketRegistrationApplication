package dto.user;

public record UserSignUpResponse(long id,
                                 String email,
                                 String firstName,
                                 String lastName,
                                 String fatherName) {
}
