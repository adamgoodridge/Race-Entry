package au.com.voc.raceEntry.utils;

import au.com.voc.raceEntry.entry.Entry;

import java.time.LocalDate;


public class CheckExpiryValidator {
    private final Entry entry;
    private String message;
    private boolean isValid;

    public CheckExpiryValidator(Entry event) {
        this.entry = event;
        isValid = true;
        validationAttributes();
    }

    private void validationAttributes() {
        StringBuilder sb = new StringBuilder();
        LocalDate eventDate = entry.getEvent().getEndDate();
        if (entry.getBoat() != null && (eventDate.isAfter(entry.getBoat().getSbaLicenceExpiryDate()) ||
                eventDate.isEqual(entry.getBoat().getSbaLicenceExpiryDate()))) {
            isValid = false;
            sb.append("Boat's S.B.A Licence expires on ");
            sb.append(DateUtils.format(entry.getBoat().getSbaLicenceExpiryDate()));
            sb.append(" and needs to updating as it expires before the event.\n");
        }
        if (entry.getDriverOne() != null && (eventDate.isAfter(entry.getPersonOne().getSbaLicenceExpiryDate()) ||
                eventDate.isEqual(entry.getPersonOne().getSbaLicenceExpiryDate()))) {
            isValid = false;
            sb.append("The first driver ");
            sb.append(entry.getPersonOne().getFullName());
            sb.append("'s S.B.A licence expires on ");
            sb.append(DateUtils.format(entry.getPersonOne().getSbaLicenceExpiryDate()));
            sb.append(" and needs to updating as it expires before the event.\n");
        }
        if (entry.getPersonTwo() != null && (eventDate.isAfter(entry.getPersonTwo().getSbaLicenceExpiryDate()) ||
                eventDate.equals(entry.getPersonTwo().getSbaLicenceExpiryDate()))) {
            isValid = false;
            sb.append("The second driver ");
            sb.append(entry.getPersonTwo().getFullName());
            sb.append("'s S.B.A licence expires on ");
            sb.append(DateUtils.format(entry.getPersonTwo().getSbaLicenceExpiryDate()));
            sb.append(" and needs to updating as it expires before the event.\n");
        }
        if (!sb.isEmpty()) {
            message = sb.toString();
            message = message.substring(0, message.length() - 1);
        }
    }

    public boolean isValid() {
        return isValid;
    }

    public String getMessage() {
        return message;
    }
}