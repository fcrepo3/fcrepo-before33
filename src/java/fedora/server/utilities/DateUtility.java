package fedora.server.utilities;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * <p><b>Title:</b> DateUtility.java</p>
 * <p><b>Description:</b> A collection of utility methods for performing</p>
 * <p>frequently require tasks.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public abstract class DateUtility
{

  /**
   * <p>Converts a datetime string into and instance of java.util.Date using
   * the date format: yyyy-MM-ddTHH:mm:ssZ.</p>
   *
   * <p>Follows Postel's Law (lenient about what it accepts, as long as
   * it's sensible)</p>
   *
   * @param dateTime A datetime string
   * @return Corresponding instance of java.util.Date (returns null
   *         if dateTime string argument is empty string or null)
   */
  public static Date convertStringToDate(String dateTime) {
    return parseDate(dateTime);
  }

  /**
   * <p>Converts an instance of java.util.Date into a String using
   * the date format: yyyy-MM-ddTHH:mm:ssZ.</p>
   *
   * @param date Instance of java.util.Date.
   * @return Corresponding datetime string (returns null if Date argument
   *         is null).
   */
  public static String convertDateToString(Date date)
  {
    String dateTime = null;
    SimpleDateFormat formatter =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    if (!(date == null))
    {
      dateTime = formatter.format(date);
    }
    return(dateTime);
  }

  public static Date convertLocalDateToUTCDate(Date localDate)
  {
    // figure out the time zone offset of this machine (in millisecs)
    Calendar cal=Calendar.getInstance();
    int tzOffset=cal.get(Calendar.ZONE_OFFSET);
    // ...and account for daylight savings time, if applicable
    TimeZone tz = cal.getTimeZone();
    if (tz.inDaylightTime(localDate))
    {
      tzOffset+=cal.get(Calendar.DST_OFFSET);
    }
    // now we have UTF offset in millisecs... so subtract it from localDate.millisecs
    // and return a new Date object.
    Date UTCDate=new Date();
    UTCDate.setTime(localDate.getTime() - tzOffset);
    return UTCDate;
  }

    /**
     * Attempt to parse the given string of form: yyyy-MM-dd[THH:mm:ss[Z]]
     * as a Date.  If the string is not of that form, return null.
     *
     * @param str the date string to parse
     * @return Date the date, if parse was successful; null otherwise
     */
    public static Date parseDate(String str) {
        if (str==null || str.length()==0) return null;
        if (str.indexOf("T")!=-1) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(str);
            } catch (ParseException pe) {
                try {
                    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(str);
                } catch (ParseException pe2) {
                    return null;
                }
            }
        } else {
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse(str);
            } catch (ParseException pe3) {
                return null;
            }
        }

    }

  public static void main(String[] args)
  {
    String dateTimeString = "2002-08-22T13:58:06";
    Date date = convertStringToDate(dateTimeString);
    System.out.println("\nDateString: " + dateTimeString
                       + "\nConvertDateToString: "
                       + convertDateToString(date));
    System.out.println("\nDate: " + convertDateToString(date)
                       + "\nConvertDateT0String: "
                       + convertDateToString(date));
  }
}
