// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XSDDateTimeDateEncoder.java

package com.sun.xml.rpc.encoding.simpletype;

import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

// Referenced classes of package com.sun.xml.rpc.encoding.simpletype:
//            SimpleTypeEncoder

public class XSDDateTimeDateEncoder
    implements SimpleTypeEncoder {

    private static final SimpleTypeEncoder encoder = new XSDDateTimeDateEncoder();
    protected static final SimpleDateFormat timeZoneFormatter;
    protected static final TimeZone gmtTimeZone;
    protected static final SimpleDateFormat dateFormatter;

    protected XSDDateTimeDateEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer) throws Exception {
        if(obj == null)
            return null;
        String s;
        synchronized(dateFormatter) {
            s = dateFormatter.format((Date)obj);
        }
        return s;
    }

    public Object stringToObject(String str, XMLReader reader) throws Exception {
        if(str == null)
            return null;
        else
            return decodeDateUtil(str, null);
    }

    public static void validateDateStr(String dateStr) throws Exception {
        if(dateStr.length() < 19)
            throw new DeserializationException("xsd.invalid.date", dateStr);
        else
            return;
    }

    protected static String getDateFormatPattern(String xsdDateTime) {
        String formatPattern = "yyyy";
        int idx = xsdDateTime.indexOf('-', 4);
        for(int i = 4; i < idx; i++)
            formatPattern = formatPattern + "y";

        formatPattern = formatPattern + "-MM-dd'T'HH:mm:ss";
        idx = xsdDateTime.indexOf('.');
        for(int i = idx; i < xsdDateTime.length() - 1 && i < idx + 3; i++) {
            if(!Character.isDigit(xsdDateTime.charAt(i + 1)))
                break;
            if(i == idx)
                formatPattern = formatPattern + ".";
            formatPattern = formatPattern + "S";
        }

        return formatPattern;
    }

    protected static Date decodeDateUtil(String str, StringBuffer zone) throws Exception {
        if(str == null)
            return null;
        validateDateStr(str);
        StringBuffer strBuf = new StringBuffer(30);
        int dateLen = getDateFormatPattern(str, strBuf);
        String pattern = strBuf.toString();
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        df.setTimeZone(gmtTimeZone);
        String tmp = str.substring(0, dateLen);
        Date date = df.parse(str.substring(0, dateLen));
        if(dateLen < str.length()) {
            int start = dateLen;
            if(Character.isDigit(str.charAt(start))) {
                int end;
                for(end = start; end < str.length() && Character.isDigit(str.charAt(end)); end++);
                String tmp2 = str.substring(start, start + 1);
                int fractmilli = Integer.parseInt(str.substring(start, start + 1));
                if(fractmilli >= 5)
                    date.setTime(date.getTime() + 1L);
                start = end;
            }
            if(start < str.length() && str.charAt(start) != 'Z') {
                if(zone != null)
                    zone.append(str.substring(start));
                synchronized(timeZoneFormatter) {
                    tmp = str.substring(start + 1);
                    Date tzOffset = timeZoneFormatter.parse(str.substring(start + 1));
                    long millis = str.charAt(start) != '+' ? tzOffset.getTime() : -tzOffset.getTime();
                    date.setTime(date.getTime() + millis);
                }
            }
        }
        return date;
    }

    protected static int getDateFormatPattern(String dateStr, StringBuffer strBuf) {
        String formatPattern = "yyyy";
        strBuf.append(formatPattern);
        int idx = dateStr.indexOf('-', 4);
        for(int i = 4; i < idx; i++)
            strBuf.append('y');

        strBuf.append("-MM-dd'T'HH:mm:ss");
        idx = dateStr.indexOf('.');
        for(int i = idx; idx > 0 && i < dateStr.length() - 1 && i < idx + 3; i++) {
            if(!Character.isDigit(dateStr.charAt(i + 1)))
                break;
            if(i == idx)
                strBuf.append('.');
            strBuf.append('S');
        }

        return strBuf.length() - 2;
    }

    public void writeAdditionalNamespaceDeclarations(Object obj1, XMLWriter xmlwriter) throws Exception {
    }

    static  {
        timeZoneFormatter = new SimpleDateFormat("HH:mm");
        gmtTimeZone = TimeZone.getTimeZone("GMT");
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatter.setTimeZone(gmtTimeZone);
        timeZoneFormatter.setTimeZone(gmtTimeZone);
    }
}
