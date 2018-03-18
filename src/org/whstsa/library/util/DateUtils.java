package org.whstsa.library.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility static methods for Date conversions<br>
 * Used in conjunction with World class
 * @see org.whstsa.library.World
 */
public class DateUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    public static String toDateString(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static Date fromDateString(String string) {
        try {
            return DATE_FORMAT.parse(string);
        } catch (ParseException e) {
            return null;
        }
    }

}
