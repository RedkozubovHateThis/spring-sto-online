package io.swagger.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

    private static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private static SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");

    public static String formatDateTime(Date date) {
        if ( date != null ) return DATE_TIME_FORMAT.format( date );
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

}
