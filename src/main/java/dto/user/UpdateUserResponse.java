package dto.user;

import domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Результат обновления данных пользователя")
public record UpdateUserResponse(
        @Schema(description = "ID пользователя", example = "1")
        Long id,
        @Schema(description = "Email", example = "user@mail.ru")
        String email,
        @Schema(description = "Новое имя", example = "Александр")
        String firstName,
        @Schema(description = "Новая фамилия", example = "Петров")
        String lastName,
        @Schema(description = "Новое отчество", example = "Сергеевич", nullable = true)
        String fatherName,
        @Schema(description = "Текущая роль")
        Role role
) {
}
