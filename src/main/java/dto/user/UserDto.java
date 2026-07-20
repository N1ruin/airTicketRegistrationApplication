package dto.user;

import domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация о пользователе")
public record UserDto(
        @Schema(description = "Уникальный идентификатор", example = "42")
        Long id,

        @Schema(description = "Email адрес", example = "ivanov@google.com")
        String email,

        @Schema(description = "Роль в системе", implementation = Role.class)
        Role role,

        @Schema(description = "Статус блокировки аккаунта")
        Boolean isBlocked) {
}
