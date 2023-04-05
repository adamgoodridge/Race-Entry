package au.com.voc.raceEntry.utils;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = DateValidValidator.class)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DateConstraint {

    String message() default "Date must be valid and in format dd/mm/yyyy";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}