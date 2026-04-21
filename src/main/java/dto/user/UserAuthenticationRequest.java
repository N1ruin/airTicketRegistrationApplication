package dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Данные для входа в систему")
public record UserAuthenticationRequest(
        @Schema(description = "Email пользователя", example = "adminemail123@mail.ru",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @Schema(description = "Пароль", example = "adminpassword123", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {
}
