package au.com.voc.raceEntry.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static LocalDate format(String dateStr) {
        return LocalDate.parse(dateStr, FORMAT);
    }
}
