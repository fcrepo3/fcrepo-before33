// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   CalendarHolder.java

package javax.xml.rpc.holders;

import java.util.Calendar;

// Referenced classes of package javax.xml.rpc.holders:
//            Holder

public final class CalendarHolder
    implements Holder {

    public Calendar value;

    public CalendarHolder() {
    }

    public CalendarHolder(Calendar myCalendar) {
        value = myCalendar;
    }
}
