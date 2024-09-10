package app.YUP.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Utility class for converting LocalDateTime to different string formats.
 * This class cannot be instantiated.
 */
public class DateTimeConverter
{
    /**
     * Private constructor to prevent instantiation of utility class.
     * @throws IllegalStateException if an attempt is made to instantiate this class.
     */
    private DateTimeConverter() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Converts a LocalDateTime to a string format of "MMMM d" (e.g., "January 1").
     * @param dateTime the LocalDateTime to be converted.
     * @return a string representation of the date in "MMMM d" format.
     */
    public static String convertToMonthDay(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d", Locale.ENGLISH);
        return dateTime.format(formatter);
    }

    /**
     * Converts a LocalDateTime to a string format of "HH:mm" (24-hour format).
     * @param dateTime the LocalDateTime to be converted.
     * @return a string representation of the time in "HH:mm" format.
     */
    public static String convertTo24Hour(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
        return dateTime.format(formatter);
    }

}