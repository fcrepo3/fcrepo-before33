// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XSDDateTimeCalendarEncoder.java

package com.sun.xml.rpc.encoding.simpletype;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

// Referenced classes of package com.sun.xml.rpc.encoding.simpletype:
//            XSDDateTimeDateEncoder, SimpleTypeEncoder

public class XSDDateTimeCalendarEncoder extends XSDDateTimeDateEncoder {

    private static final SimpleTypeEncoder encoder = new XSDDateTimeCalendarEncoder();
    private static Method getDSTSavingsMethod = null;
    private static final SimpleDateFormat calendarFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final Calendar gmtCalendar;

    private XSDDateTimeCalendarEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer) throws Exception {
        if(obj == null)
            return null;
        Calendar c = (Calendar)obj;
        int offset = c.get(15);
        if(c.getTimeZone().inDaylightTime(c.getTime()))
            offset += ((Integer)getDSTSavingsMethod.invoke(c.getTimeZone(), null)).intValue();
        int offsetHours = offset / 0x36ee80;
        StringBuffer buf = new StringBuffer(5);
        buf.append(offset >= 0 ? '+' : '-');
        offsetHours = offsetHours >= 0 ? offsetHours : -offsetHours;
        if(offsetHours < 10)
            buf.append('0');
        buf.append(offsetHours + ":00");
        String offsetStr = new String(buf);
        String s;
        synchronized(calendarFormatter) {
            calendarFormatter.setTimeZone(c.getTimeZone());
            s = calendarFormatter.format(c.getTime()) + offsetStr;
        }
        return s;
    }

    public Object stringToObject(String str, XMLReader reader) throws Exception {
        if(str == null)
            return null;
        StringBuffer zone = new StringBuffer(10);
        java.util.Date date = XSDDateTimeDateEncoder.decodeDateUtil(str, zone);
        String zoneStr = zone.toString();
        Calendar cal;
        if(zoneStr.length() == 0) {
            cal = Calendar.getInstance(XSDDateTimeDateEncoder.gmtTimeZone);
            cal.setTime(date);
        } else {
            cal = Calendar.getInstance(XSDDateTimeDateEncoder.gmtTimeZone);
            cal.setTime(date);
            TimeZone tz = TimeZone.getTimeZone("GMT" + zoneStr);
            int rawOffset = tz.getRawOffset();
            TimeZone tz2 = TimeZone.getDefault();
            if(tz2.inDaylightTime(date))
                rawOffset -= ((Integer)getDSTSavingsMethod.invoke(tz2, null)).intValue();
            tz2.setRawOffset(rawOffset);
            tz2.setID("Custom");
            cal.setTimeZone(tz2);
        }
        return cal;
    }

    public void writeAdditionalNamespaceDeclarations(Object obj1, XMLWriter xmlwriter) throws Exception {
    }

    static  {
        gmtCalendar = Calendar.getInstance();
        gmtCalendar.setTimeZone(XSDDateTimeDateEncoder.gmtTimeZone);
        try {
            getDSTSavingsMethod = TimeZone.getDefault().getClass().getMethod("getDSTSavings", null);
        }
        catch(NoSuchMethodException nosuchmethodexception) {
            System.out.println("FATAL ERROR: no getDSTSavings method defined on TimeZone");
        }
    }
}
