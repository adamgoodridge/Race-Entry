package au.com.voc.raceEntry.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");

    public static LocalDate format(String date) {
        return LocalDate.parse(date, formatter);
    }

    public static String format(LocalDate localDate) {
        return formatter.format(localDate);
    }

    public static String displayDays(LocalDate startDate, int duration) {
        String dateOutput = format(startDate);
        if (duration > 1) {
            startDate.plusDays(duration - 1);
            dateOutput += " TO " + startDate;
        }
        return dateOutput;
    }

    public static String displayDays(LocalDate startDate, LocalDate endDate) {
        return format(startDate) + " TO " + endDate;
    }
}
