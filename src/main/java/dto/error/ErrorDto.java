package dto.error;

import java.time.ZonedDateTime;

public record ErrorDto(Integer status,
                       String payload,
                       ZonedDateTime timestamp) {
}
