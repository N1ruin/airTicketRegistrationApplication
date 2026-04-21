package dto.user;

import domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Результат успешной регистрации пользователя")
public record UserSignUpResponse(
        @Schema(description = "ID созданного пользователя", example = "1")
        Long id,
        @Schema(description = "Email", example = "example@mail.ru")
        String email,
        @Schema(description = "Имя", example = "Иван")
        String firstName,
        @Schema(description = "Фамилия", example = "Иванов")
        String lastName,
        @Schema(description = "Отчество", example = "Иванович", nullable = true)
        String fatherName,
        @Schema(description = "Установленная роль")
        Role role
) {
}
