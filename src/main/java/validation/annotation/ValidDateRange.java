package validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import validation.validator.DateRangeConstraintValidator;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeConstraintValidator.class)
@Documented
public @interface ValidDateRange {
    String message() default "The arrival date must be later than the departure date";

    String startDate();

    String endDate();
}
