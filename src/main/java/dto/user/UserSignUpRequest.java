package dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Данные для регистрации нового пользователя")
public record UserSignUpRequest(
        @Schema(description = "Email", example = "example@mail.ru",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @Schema(description = "Пароль", example = "Password321",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Password is required")
        @Size(min = 5, message = "Password must be at least 5 characters")
        String password) {
}
