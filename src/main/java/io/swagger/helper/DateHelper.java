package io.swagger.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

    private static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private static SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private static SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");

    public static String formatDateTime(Date date) {
        if ( date != null ) return DATE_TIME_FORMAT.format( date );
        return null;
    }

    public static Date parseDateTime(String date) throws ParseException {
        if ( date != null && date.length() > 0 ) return DATE_TIME_FORMAT.parse( date );
        return null;
    }

    public static String formatDate(Date date) {
        if ( date != null ) return DATE_FORMAT.format( date );
        return null;
    }

    public static String formatYear(Date date) {
        if ( date != null ) return YEAR_FORMAT.format( date );
        return null;
    }

    public static String formatISO(Date date) {
        if ( date != null ) return ISO_FORMAT.format( date );
        return null;
    }

}
