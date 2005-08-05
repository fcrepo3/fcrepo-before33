package fedora.server.utilities;

import java.util.Date;

import junit.framework.TestCase;

/**
 * @author Edwin Shin
 */
public class TestDateUtility extends TestCase {
    protected final Date EPOCH = new Date(0L);
    protected final String EPOCH_DT = "1970-01-01T00:00:00.000Z";
    protected final String EPOCH_D = "1970-01-01Z";
    protected final String EPOCH_T = "00:00:00.000Z";
    protected final String HTTP_DATE = "Thu, 04 Aug 2005 01:35:07 GMT";
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestDateUtility.class);
    }
    
    public void testConvertDateToString() {
        assertEquals(DateUtility.convertDateToString(EPOCH), EPOCH_DT);
    }

    public void testConvertDateToDateString() {
        assertEquals(DateUtility.convertDateToDateString(EPOCH), EPOCH_D);
    }

    public void testConvertDateToTimeString() {
        assertEquals(DateUtility.convertDateToTimeString(EPOCH), EPOCH_T);
    }

    public void testParseDate() {
        String[] dates = {"1970-01-01T00:00:00.000Z",
                          "1970-01-01T00:00:00.00Z",
                          "1970-01-01T00:00:00.0Z",
                          "1970-01-01T00:00:00Z",
                          "1970-01-01Z",
                          "1970-01-01T00:00:00.000",
                          "1970-01-01T00:00:00.00",
                          "1970-01-01T00:00:00.0",
                          "1970-01-01T00:00:00",
                          "1970-01-01",
						  "Thu, 01 Jan 1970 00:00:00 GMT"
        };
        for (int i = 0; i < dates.length; i++) {
            assertEquals(DateUtility.parseDateAsUTC(dates[i]), EPOCH);
        }
        
        String[] badDates = {"",
                             "ABCD-EF-GHTIJ:KL:MN.OPQZ",
                             "1234",
                             "1",
                             "1970-01",
                             "1970-1-1",
                             "12345-01-01T00:00:00.000Z",
                             "12345-01-01T00:00:00."
        };
        for (int i = 0; i < badDates.length; i++) {
            TestCase.assertNull(DateUtility.parseDateAsUTC(badDates[i]));
        }
    }

}
