//demo only
package au.com.voc.raceEntry.entry;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class SecondSignatureValidator implements ConstraintValidator<SecondSignature, EntryFormData> {
    @Override
    public void initialize(SecondSignature constraintAnnotation) {

    }

    public boolean isValid(final EntryFormData value, ConstraintValidatorContext context) {
        boolean isValid = false;
        try {

            if (!(value instanceof EntryFormData)) {
                throw new IllegalAccessException("@SecondSignatureValidator is only for entry form objects");
            }
            EntryFormData entryFormData = value;
            //final Object driverID = new BeanWrapperImpl(value).getPropertyValue(driverIDFieldName);
            if (entryFormData.getSecondDriverId() == 0) {
                isValid = true;
            } else {
                isValid = entryFormData.getDeclarationDriverOneSignature() != null && entryFormData.getDeclarationDriverTwoDate() != null;
            }
        } catch (final Exception ignore) {
        }
        return isValid;
    }
}