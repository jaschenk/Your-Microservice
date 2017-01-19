package your.microservice.core.util;

import java.util.Calendar;

/**
 * Simple TimeStamp Object to provide various timestamp formats
 *
 * @author jeff.a.schenk@gmail.com on 8/29/15.
 */
public class TimeStamp {
    /**
     * <p>getTimeStamp</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static final String getTimeStamp() {
        return getTimeStampForFileName(Calendar.getInstance());
    } // End of getTimestamp Method.

    /**
     * <p>getTimeStampForFileName</p>
     *
     * @param current_calendar a {@link java.util.Calendar} object.
     * @return a {@link java.lang.String} object.
     */
    public static final String getTimeStampForFileName(Calendar current_calendar) {
        StringBuffer sb = new StringBuffer();
        sb.append(formatDigit(current_calendar.get(Calendar.YEAR)));
        sb.append(formatDigit(current_calendar.get(Calendar.MONTH) + 1));
        sb.append(formatDigit(current_calendar.get(Calendar.DAY_OF_MONTH)));
        sb.append(formatDigit(current_calendar.get(Calendar.HOUR)));
        sb.append(formatDigit(current_calendar.get(Calendar.MINUTE)));
        sb.append(formatDigit(current_calendar.get(Calendar.SECOND)));
        sb.append(current_calendar.get(Calendar.MILLISECOND));

        // ***********************
        // Return the String.
        return sb.toString();
    } // End of getTimestamp Method.

    /**
     * <p>getGenerationTimeStamp</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static final String getGenerationTimeStamp() {
        return getTimeStampForReadableText(Calendar.getInstance());
    } // End of getTimestamp Method.

    /**
     * <p>getTimeStampForReadableText</p>
     *
     * @param current_calendar a {@link java.util.Calendar} object.
     * @return a {@link java.lang.String} object.
     */
    public static final String getTimeStampForReadableText(Calendar current_calendar) {
        StringBuffer sb = new StringBuffer();
        sb.append(formatDigit(current_calendar.get(Calendar.YEAR)) + "-");
        sb.append(formatDigit(current_calendar.get(Calendar.MONTH) + 1) + "-");
        sb.append(formatDigit(current_calendar.get(Calendar.DAY_OF_MONTH)) + " ");
        sb.append(formatDigit(current_calendar.get(Calendar.HOUR)) + ":");
        sb.append(formatDigit(current_calendar.get(Calendar.MINUTE)) + ":");
        sb.append(formatDigit(current_calendar.get(Calendar.SECOND)) + ".");
        sb.append(current_calendar.get(Calendar.MILLISECOND));

        // ***********************
        // Return the String.
        return sb.toString();
    } // End of getTimestamp Method.

    /**
     * Private Helper Method to Format Digits.
     */
    private static String formatDigit(int number) {
        if (number > 9) {
            return Integer.toString(number);
        } else {
            return "0" + (Integer.toString(number));
        }
    } // End of private Helper Method.
} ///:>~ End of TimeStamp Public Utility Class.

