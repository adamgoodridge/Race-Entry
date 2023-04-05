package au.com.voc.raceEntry.entry;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldUniqueValidator implements ConstraintValidator<FieldUnique, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldUnique constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(final Object value, ConstraintValidatorContext context) {
        boolean isFieldUnique = true;
        try {
            final Object firstField = new BeanWrapperImpl(value).getPropertyValue(firstFieldName);
            final Object secondField = new BeanWrapperImpl(value).getPropertyValue(secondFieldName);
            System.out.println("=========" + firstField + "-------");
            isFieldUnique = (Integer.parseInt((String) firstField) == 0) || !firstField.equals(secondField);
        } catch (final Exception ignore) {
        }
        return isFieldUnique;
    }
}