package validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import validation.annotation.ValidDateRange;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class DateRangeConstraintValidator implements ConstraintValidator<ValidDateRange, Object> {
    private String startDateField;
    private String endDateField;

    @Override
    public void initialize(ValidDateRange annotation) {
        this.startDateField = annotation.startDate();
        this.endDateField = annotation.endDate();
    }

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        try {
            var startMethod = dto.getClass().getMethod(startDateField);
            var endMethod = dto.getClass().getMethod(endDateField);

            var start = startMethod.invoke(dto);
            var end = endMethod.invoke(dto);

            if (start == null || end == null) {
                return true;
            }

            var isValid = compareDates(start, end);

            if (!isValid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("The arrival date must be later than the departure date")
                        .addConstraintViolation();
            }

            return isValid;

        } catch (Exception e) {
            return false;
        }
    }

    private boolean compareDates(Object start, Object end) {
        if (start instanceof ZonedDateTime && end instanceof ZonedDateTime) {
            return ((ZonedDateTime) end).isAfter((ZonedDateTime) start);
        }
        if (start instanceof LocalDateTime && end instanceof LocalDateTime) {
            return ((LocalDateTime) end).isAfter((LocalDateTime) start);
        }
        if (start instanceof LocalDate && end instanceof LocalDate) {
            return ((LocalDate) end).isAfter((LocalDate) start);
        }
        return false;
    }
}
