package ee.taltech.ite4120.parking.api;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Same shape as a missing license plate: a field error on the request body. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EndNotBeforeStart.Validator.class)
@Documented
public @interface EndNotBeforeStart {

    String message() default "must not be before startDate";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<EndNotBeforeStart, PermitRequest> {

        @Override
        public boolean isValid(PermitRequest value, ConstraintValidatorContext context) {
            if (value == null || value.getStartDate() == null || value.getEndDate() == null) {
                return true;
            }
            if (!value.getEndDate().isBefore(value.getStartDate())) {
                return true;
            }
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            return false;
        }
    }
}
