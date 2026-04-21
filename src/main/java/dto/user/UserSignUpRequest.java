package dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Данные для регистрации нового пользователя")
public record UserSignUpRequest(
        @Schema(description = "Email", example = "example@mail.ru",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @Schema(description = "Пароль", example = "Password321",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String password,
        @Schema(description = "Имя пользователя", example = "Иван", requiredMode = Schema.RequiredMode.REQUIRED)
        String firstName,
        @Schema(description = "Фамилия пользователя", example = "Иванов", requiredMode = Schema.RequiredMode.REQUIRED)
        String lastName,
        @Schema(description = "Отчество (если есть)", example = "Иванович", nullable = true)
        String fatherName
) {
}
