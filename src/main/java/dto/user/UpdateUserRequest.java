package dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на обновление данных профиля")
public record UpdateUserRequest(
        @Schema(description = "ID пользователя, данные которого обновляются", example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,
        @Schema(description = "Новый пароль", example = "NewpaSs123!",
                nullable = true)
        String newPassword,
        @Schema(description = "Новое имя", example = "Александр")
        String firstName,
        @Schema(description = "Новая фамилия", example = "Иванов")
        String lastName,
        @Schema(description = "Новое отчество", example = "Петрович", nullable = true)
        String fatherName
) {
}
