package fedora.server.resourceIndex;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;

/**
 * @author Edwin Shin
 */
public class TestFoo extends TestCase {
    public static final int DF8601_MSEC = 0;
    public static final int DF8601_SEC = 1;
    public static final int FMT_COUNT = 2;
    
    private static final DateFormat[] df8601;
    
    static {
        df8601 = new SimpleDateFormat[FMT_COUNT];
      
        df8601[DF8601_MSEC] =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        df8601[DF8601_SEC] =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestFoo.class);
    }
    
//    public void testBar() throws Exception {
//        String dateString = "2005-01-14T22:33:44.0";
//        System.out.println("* " + getDate(dateString));
//    }
    
    public void testBaz() throws Exception {
        String dateString = "2005-01-14T22:33:44.01";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        
        int pos = dateString.length() - 4;
        int index = dateString.indexOf('.', pos);
        if (index == -1) {
            dateString = dateString.concat(".000");
        } else {
            int pad = pos - index;
            while (pad < 0) {
                dateString = dateString.concat("0");
                pad++;
            }
        }
        Date date = df.parse(dateString);
        System.out.println(df.format(date));
    }
    
    private String getDate(String date) {
        int len = date.length();
        if (date.indexOf('.', len - 4) != -1) {
            while (date.charAt(len -1) == '0') {
                len--;
            }
            if (date.charAt(len - 1) == '.') {
                len--;
            }
            return date.substring(0, len);
        } else {
            return date;
        }
    }
}
