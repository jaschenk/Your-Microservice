package your.microservice.example.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Time
 *
 * @author jeff.a.schenk@gmail.com on 12/29/15.
 */
public final class Time {

    private Time() {}

    public static Date now() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

}
