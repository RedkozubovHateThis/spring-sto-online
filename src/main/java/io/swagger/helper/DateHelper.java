package io.swagger.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public static String formatDate(Date date) {
        if ( date != null ) return DATE_FORMAT.format( date );
        return null;
    }

}
