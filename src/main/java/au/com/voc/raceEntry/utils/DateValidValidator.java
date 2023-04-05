package au.com.voc.raceEntry.utils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateValidValidator implements ConstraintValidator<DateConstraint, String> {

    @Override
    public boolean isValid(final String inputDate, ConstraintValidatorContext context) {
        boolean isDateValid;
        if (inputDate == null)
            isDateValid = false;
        else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                sdf.parse(inputDate);
                isDateValid = true;
            } catch (ParseException exception) {
                isDateValid = false;
            }
        }
        return isDateValid;
    }
}