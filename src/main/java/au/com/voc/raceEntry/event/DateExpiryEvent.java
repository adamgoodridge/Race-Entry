//demo only
package au.com.voc.raceEntry.event;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = DateExpiryEventValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DateExpiryEvent {


    String message() default "{constraints.field-match}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String eventDate();

    String expiryDate();


    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        DateExpiryEvent[] value();
    }

}