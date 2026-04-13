package dto.user;

import domain.Role;

public record UserSignUpResponse(long id,
                                 String email,
                                 String firstName,
                                 String lastName,
                                 String fatherName,
                                 Role role) {
}
