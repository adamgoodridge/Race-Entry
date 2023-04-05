//demo only
package au.com.voc.raceEntry.user.register;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(final Object value, ConstraintValidatorContext context) {
        boolean isFieldMatch = true;
        try {
            final Object firstField = new BeanWrapperImpl(value).getPropertyValue(firstFieldName);
            final Object secondField = new BeanWrapperImpl(value).getPropertyValue(secondFieldName);
            //fields can be blank to be valid
            isFieldMatch = (firstField == null && secondField == null) || (firstField != null && firstField.equals(secondField));
        } catch (final Exception ignore) {
        }
        return isFieldMatch;
    }
}