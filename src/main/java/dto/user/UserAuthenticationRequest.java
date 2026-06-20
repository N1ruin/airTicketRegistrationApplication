package dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Данные для входа в систему")
public record UserAuthenticationRequest(
        @Schema(description = "Email пользователя", example = "adminemail123@mail.ru",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @Schema(description = "Пароль", example = "adminpassword123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Password is required")
        String password) {
}
