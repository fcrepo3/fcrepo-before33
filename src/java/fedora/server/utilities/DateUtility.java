package fedora.server.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

/**
 * 
 * <p>
 * <b>Title: </b> DateUtility.java
 * </p>
 * <p>
 * <b>Description: </b> A collection of utility methods for performing
 * </p>
 * <p>
 * frequently require tasks.
 * </p>
 * 
 * @author rlw@virginia.edu
 * @version $Id$
 */
public abstract class DateUtility {

    /**
     * <p>
     * Converts a datetime string into and instance of java.util.Date using the
     * date format: yyyy-MM-ddTHH:mm:ss.SSSZ.
     * </p>
     * 
     * <p>
     * Follows Postel's Law (lenient about what it accepts, as long as it's
     * sensible)
     * </p>
     * 
     * @param dateTime
     *            A datetime string
     * @return Corresponding instance of java.util.Date (returns null if
     *         dateTime string argument is empty string or null)
     */
    public static Date convertStringToDate(String dateTime) {
        return parseDateAsUTC(dateTime);
    }

    /**
     * <p>
     * Converts an instance of java.util.Date into a String using the date
     * format: yyyy-MM-ddTHH:mm:ss.SSSZ.
     * </p>
     * 
     * @param  date Instance of java.util.Date.
     * @return      ISO 8601 String representation (yyyy-MM-ddTHH:mm:ss.SSSZ) 
     *              of the Date argument or null if the Date argument is null.
     */
    public static String convertDateToString(Date date) {
        return convertDateToString(date, true);
    }
    
    /**
     * Converts an instance of java.util.Date into an ISO 8601 String 
     * representation. 
     * Uses the date format yyyy-MM-ddTHH:mm:ss.SSSZ or 
     * yyyy-MM-ddTHH:mm:ssZ, depending on whether millisecond precision is 
     * desired.
     * 
     * @param date		Instance of java.util.Date.
     * @param millis	Whether or not the return value should include 
     * 					milliseconds.
     * @return			ISO 8601 String representation of the Date argument or 
     * 					null if the Date argument is null.
     */
    public static String convertDateToString(Date date, boolean millis) {
        if (date == null) {
            return null;
        } else {
        	DateFormat df;
        	if (millis) {
        		df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        	} else {
        		df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        	}
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            return df.format(date);
        }
    }
    
    
    /**
     * <p>
     * Converts an instance of java.util.Date into a String using the date
     * format: yyyy-MM-ddZ.
     * </p>
     * 
     * @param date
     *            Instance of java.util.Date.
     * @return Corresponding date string (returns null if Date argument is
     *         null).
     */
    public static String convertDateToDateString(Date date) {
        if (date == null) {
            return null;
        } else {
            DateFormat df = new SimpleDateFormat(
                    "yyyy-MM-dd'Z'");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            return df.format(date);
        }
    }
    
    
    /**
     * <p>
     * Converts an instance of java.util.Date into a String using the date
     * format: HH:mm:ss.SSSZ.
     * </p>
     * 
     * @param date
     *            Instance of java.util.Date.
     * @return Corresponding time string (returns null if Date argument is
     *         null).
     */
    public static String convertDateToTimeString(Date date) {
        if (date == null) {
            return null;
        } else {
            DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS'Z'");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            return df.format(date);
        }
    }    

    /**
     * Attempt to parse the given string of form: yyyy-MM-dd[THH:mm:ss[.SSS][Z]]
     * as a Date. If the string is not of that form, return null.
     * 
     * @param dateString the date string to parse
     * @return Date the date, if parse was successful; null otherwise
     */
    public static Date parseDateAsUTC(String dateString) {
        if (dateString == null || dateString.length() == 0) {
            return null;
        }
        
        SimpleDateFormat formatter = new SimpleDateFormat();
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (dateString.endsWith("Z")) {
            if (dateString.length() == 11) {
                formatter.applyPattern("yyyy-MM-dd'Z'");
            } else if (dateString.length() == 20) {
                formatter.applyPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            } else if (dateString.length() == 22) {
                formatter.applyPattern("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
            } else if (dateString.length() == 23) {
                formatter.applyPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'");
            } else if (dateString.length() == 24) {
                formatter.applyPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            }
        } else {
            if (dateString.length() == 10) {
                formatter.applyPattern("yyyy-MM-dd");
            } else if (dateString.length() == 19) {
                formatter.applyPattern("yyyy-MM-dd'T'HH:mm:ss");
            } else if (dateString.length() == 21) {
                formatter.applyPattern("yyyy-MM-dd'T'HH:mm:ss.S");
            } else if (dateString.length() == 22) {
                formatter.applyPattern("yyyy-MM-dd'T'HH:mm:ss.SS");
            } else if (dateString.length() == 23) {
                formatter.applyPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
            } else if (dateString.endsWith("GMT") || dateString.endsWith("UTC")) {
            	formatter.applyPattern("EEE, dd MMMM yyyyy HH:mm:ss z");
            }
        }
        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        String dateTimeString = "2002-08-22T13:58:06";
        Date date = convertStringToDate(dateTimeString);
        System.out.println("\nDateString: " + dateTimeString
                + "\nConvertDateToString: " + convertDateToString(date));
        System.out.println("\nDate: " + convertDateToString(date)
                + "\nConvertDateToString: " + convertDateToString(date));
        System.out.println("\nDate: " + convertDateToDateString(date)
                + "\nConvertDateToDateString: " + convertDateToDateString(date));
        System.out.println("\nDate: " + convertDateToTimeString(date)
                + "\nConvertDateToTimeString: " + convertDateToTimeString(date));        
    }
}