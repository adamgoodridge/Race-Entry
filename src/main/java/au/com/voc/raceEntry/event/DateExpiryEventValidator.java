package au.com.voc.raceEntry.event;

import au.com.voc.raceEntry.entry.Entry;
import au.com.voc.raceEntry.utils.DateUtils;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;


public class DateExpiryEventValidator implements ConstraintValidator<DateExpiryEvent, Object> {
    private String eventDateFieldName;
    private String expiryDateFieldName;

    public DateExpiryEventValidator(Entry entry) {
    }

    @Override
    public void initialize(DateExpiryEvent constraintAnnotation) {
        /// eventDateFieldName = constraintAnnotation.getEventDate();
        // expiryDateFieldName = constraintAnnotation.getExpiryDate();
    }


    public boolean isValid(final Object value, ConstraintValidatorContext context) {
        boolean isValid = false;
        try {
            final String eventDateField = (String) new BeanWrapperImpl(value).getPropertyValue(eventDateFieldName);
            final String expiryDateField = (String) new BeanWrapperImpl(value).getPropertyValue(eventDateFieldName);
            LocalDate eventDate = DateUtils.format(eventDateField);
            LocalDate expiryDate = DateUtils.format(expiryDateField);
            return expiryDate.isAfter(eventDate);

        } catch (final Exception ignore) {
        }
        return isValid;
    }
}