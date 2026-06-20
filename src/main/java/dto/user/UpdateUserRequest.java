package dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Запрос на обновление данных профиля")
public record UpdateUserRequest(
        @Schema(description = "Новый пароль", example = "NewpaSs123!",
                nullable = true)
        @NotBlank(message = "New password is required")
        @Size(min = 5, message = "Password must be at least 5 characters")
        String newPassword) {
}
