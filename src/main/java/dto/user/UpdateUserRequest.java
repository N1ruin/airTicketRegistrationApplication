package dto.user;

public record UpdateUserRequest(Long id, String newPassword, String firstName, String lastName, String fatherName) {
}
