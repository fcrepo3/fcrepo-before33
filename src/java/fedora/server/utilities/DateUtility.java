package fedora.server.utilities;

/**
 * <p>Title: DateUtility.java</p>
 * <p>Description: A collection of utility methods for performing</p>
 * <p>frequently require tasks.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */

import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;

public abstract class DateUtility
{
  private static final boolean debug = true; //Testing
  private static final SimpleDateFormat formatter =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

  /**
   * Converts a datetime string into and instance of java.util.Calendar using
   * the date format: yyyy-MM-ddTHH:mm:ss
   *
   * @param dateTime A datetime string
   * @return Corresponding instance of java.util.Calendar
   */
  public static Calendar convertStringToCalendar(String dateTime)
  {
    Calendar calendar = Calendar.getInstance();
    ParsePosition pos = new ParsePosition(0);
    Date date = formatter.parse(dateTime, pos);
    calendar.setTime(date);
    return(calendar);
  }

  /**
   * Converts a datetime string into and instance of java.util.Date using
   * the date format: yyyy-MM-ddTHH:mm:ss
   *
   * @param dateTime A datetime string
   * @return Corresponding instance of java.util.Date
   */
  public static Date convertStringToDate(String dateTime)
  {
    ParsePosition pos = new ParsePosition(0);
    Date date = formatter.parse(dateTime, pos);
    return(date);
  }

  /**
   * Converts an instance of java.util.Calendar into a string using
   * the date format: yyyy-MM-ddTHH:mm:ss
   *
   * @param calendar An instance of java.util.Calendar
   * @return Corresponding datetime string
   */
  public static String convertCalendarToString(Calendar calendar)
  {
    Date date = calendar.getTime();
    String dateTimeString = formatter.format(date);
    return(dateTimeString);
  }

  /**
   * Converts an instance of java.util.Date into a String using
   * the date format: yyyy-MM-ddTHH:mm:ss
   *
   * @param date Instance of java.util.Date
   * @return Corresponding datetime string
   */
  public static String convertDateToString(Date date)
  {
    String dateTimeString = formatter.format(date);
    return(dateTimeString);
  }

  public static void main(String[] args)
  {
    String dateTimeString = "2002-08-22T13:58:06";
    Calendar cal = convertStringToCalendar(dateTimeString);
    System.out.println("DateString: "+dateTimeString+"\nCalendar: "+cal);
    Date date = convertStringToDate(dateTimeString);
    System.out.println("\nDateString: "+dateTimeString+"\nDate: "+
                       convertDateToString(date));
    System.out.println("\nCalendar: "+cal+"\nDateTimeString: "+
                       convertCalendarToString(cal));
    System.out.println("\nDate: "+date+"\nDateTimeString: "+
                       convertDateToString(date));
  }
}