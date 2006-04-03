package fedora.server.utilities.status;

import java.text.*;
import java.util.*;

public class ServerStatusMessage {

    public static final ServerStatusMessage NEW_SERVER_MESSAGE = 
            new ServerStatusMessage(ServerState.NEW_SERVER, null, null);

    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ssa z";

    private ServerState _state;
    private Date _date;
    private String _detail;

    public ServerStatusMessage(ServerState state,
                               Date time,
                               String detail) {
        _state = state;
        _date = time;
        if (_date == null) {
            _date = new Date();
        }
        if (detail != null) {
            _detail = detail.trim();
        }
    }

    public ServerState getState() {
        return _state;
    }

    public Date getDate() {
        return _date;
    }

    public String getDetail() {
        return _detail;
    }

    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append("STATE  : " + _state.getName() + "\n");
        out.append("AS OF  : " + dateToString(_date) + "\n");
        if (_detail != null) {
            out.append("DETAIL : " + _detail + "\n");
        }
        return out.toString();
    }

    public static String dateToString(Date d) {
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
        return formatter.format(d);
    }

    public static Date stringToDate(String s) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
        return formatter.parse(s);
    }
}
