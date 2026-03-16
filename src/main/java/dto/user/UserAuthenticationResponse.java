package dto.user;

public record UserAuthenticationResponse(long id,
                                         String email,
                                         String firstName,
                                         String lastName,
                                         String fatherName) {
}
