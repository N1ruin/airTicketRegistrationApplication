package dto.user;

import domain.Role;

public record UserDto(Long id,
                      String email,
                      String firstName,
                      String lastName,
                      String fatherName,
                      Role role,
                      boolean isBlocked) {
}
