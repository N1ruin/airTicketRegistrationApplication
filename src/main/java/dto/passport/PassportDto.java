package dto.passport;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Данные паспорта")
public record PassportDto(
        @Schema(description = "Серия паспорта", example = "4510")
        String series,

        @Schema(description = "Номер паспорта", example = "123456")
        String number,

        @Schema(description = "Гражданство", example = "РФ")
        String citizenship,

        @Schema(description = "Дата выдачи", example = "2010-05-20",
                type = "string", pattern = "yyyy-MM-dd")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate issueDate,

        @Schema(description = "Дата окончания срока действия", example = "2030-05-20",
                type = "string", pattern = "yyyy-MM-dd")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate expiredDate) {
}
