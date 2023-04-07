package au.com.voc.raceEntry.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter USER_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String format(LocalDate localDate) {
        return localDate.format(USER_FORMAT);
    }

    public static LocalDate format(String dateTyped) {

        //default, ISO_LOCAL_DATE
        return LocalDate.parse(dateTyped, USER_FORMAT);
    }

    @Deprecated
    public static String displayDays(LocalDate startDate, int days) {
        days--;
        LocalDate endDate = startDate.plusDays(days);
        String output = DateUtils.format(startDate);
        if (days != 1) {
            output += " to " + DateUtils.format(endDate);

        }
        return output;
    }

    public static String displayDays(LocalDate startDate, LocalDate endDate) {
        String output = DateUtils.format(startDate);
        if (endDate != null) {
            output += " to " + DateUtils.format(endDate);

        }
        return output;
    }


}
