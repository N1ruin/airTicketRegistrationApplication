package dto.user;

import domain.Role;

public record UpdateUserResponse(Long id, String email, String firstName, String lastName, String fatherName,
                                 Role role) {
}
