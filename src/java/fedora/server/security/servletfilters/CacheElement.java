
package fedora.server.security.servletfilters;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Bill Niebel
 */
public class CacheElement {

    private static final Calendar EARLIER;
    static {
        Calendar temp = Calendar.getInstance();
        temp.set(Calendar.YEAR, 1999);
        EARLIER = temp;
    }

    private static final boolean s_expired_default = true; // safest

    private static final Log LOG = LogFactory.getLog(CacheElement.class);

    private final String m_cacheid;

    private final String m_cacheabbrev;

    private final String m_userid;

    private String m_password = null;

    private boolean m_valid = false;

    private Calendar m_expiration = null;

    private Boolean m_authenticated = null;

    private Map m_namedValues = null;

    private String m_errorMessage = null;

    //public final HashSet NULL_SET =  new HashSet(); 
    private final Hashtable m_empty_map = new Hashtable();

    public CacheElement(String userid, String cacheid, String cacheabbrev) {
        m_cacheid = cacheid;
        m_cacheabbrev = cacheabbrev;
        m_userid = userid;
        this.invalidate();
        //to do:  refactor to remove unused parm "cache"  
    }

    public String getCacheid() {
        return m_cacheid;
    }

    public String getCacheAbbrev() {
        return m_cacheabbrev;
    }

    public String getUserid() {
        return m_userid;
    }

    public void setPassword(String password) {
        m_password = password;
    }

    public String getPassword() {
        return m_password;
    }

    public void setValid(boolean valid) {
        m_valid = valid;
    }

    public boolean isValid() {
        return m_valid;
    }

    public void setExpiration(Calendar expiration) {
        m_expiration = expiration;
    }

    public Calendar getExpiration() {
        return m_expiration;
    }

    public Boolean getAuthenticated() {
        return m_authenticated;
    }

    public boolean isAuthenticated() {
        boolean rc = false;
        if (getAuthenticated() == null) {
        } else {
            rc = getAuthenticated().booleanValue();
        }
        return rc;
    }

    public void setAuthenticated(Boolean authenticated) {
        m_authenticated = authenticated;
    }

    private Map getNamedValues() {
        return m_namedValues;
    }

    private void setNamedValues(Map namedValues) {
        m_namedValues = namedValues;
    }

    public String getErrorMessage() {
        return m_errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        m_errorMessage = errorMessage;
    }

    public String getInstanceId() {
        String rc = toString();
        int i = rc.indexOf("@");
        if (i > 0) {
            rc = rc.substring(i + 1);
        }
        return rc;
    }

    public void invalidate(String errorMessage) {
        String m = getCacheAbbrev() + " invalidate() ";
        setValid(false);
        m_errorMessage = errorMessage;
        setAuthenticated(null);
        setNamedValues(null);
        setExpiration(EARLIER);
        setPassword(null);
        if (getErrorMessage() != null) {
            LOG.debug(m + getErrorMessage());
        }
    }

    public void invalidate() {
        invalidate(null);
    }

    private final void assertInvalid() {
        assert getAuthenticated() == null;
        assert this.getNamedValues() == null;
        assert !isValid();
        assert isExpired(getExpiration(), false);
        assert getPassword() == null;
    }

    public static final void checkCalcExpiration(int duration, int unit)
            throws IllegalArgumentException {
        if (duration < 0) {
            throw new IllegalArgumentException("bad duration==" + duration);
        }
        switch (unit) {
            case Calendar.MILLISECOND:
            case Calendar.SECOND:
            case Calendar.MINUTE:
            case Calendar.HOUR:
                break;
            default:
                throw new IllegalArgumentException("bad unit==" + unit);
        }
    }

    private void validate(Boolean authenticated, Map namedValues) {
        assertInvalid();
        setAuthenticated(authenticated);
        setNamedValues(namedValues);
        setErrorMessage(null);
        setValid(true);
    }

    public static final void auditNamedValues(String m, Map namedValues) {
        if (LOG.isDebugEnabled()) {
            assert namedValues != null;
            for (Iterator outer = namedValues.keySet().iterator(); outer
                    .hasNext();) {
                Object name = outer.next();
                assert name instanceof String : "not a string, name==" + name;
                StringBuffer sb = new StringBuffer(m + name + "==");
                Object temp = namedValues.get((String) name);
                assert temp instanceof String || temp instanceof Set : "neither string nor set, temp=="
                        + temp;
                if (temp instanceof String) {
                    sb.append(temp.toString());
                } else if (temp instanceof Set) {
                    Set values = (Set) temp;
                    sb.append("(" + values.size() + ") {");
                    String punct = "";
                    for (Iterator it = values.iterator(); it.hasNext();) {
                        temp = it.next();
                        if (!(temp instanceof String)) {
                            LOG.error(m + "set member not string, ==" + temp);
                        } else {
                            String value = (String) temp;
                            sb.append(punct + value);
                            punct = ",";
                        }
                    }
                    sb.append("}");
                }
                LOG.debug(sb.toString());
            }
        }
    }

    public static final String pad(long i, String pad, boolean padLeft) {
        String rc = "";
        String st = Long.toString(i);
        if (st.length() == pad.length()) {
            rc = st;
        } else if (st.length() > pad.length()) {
            rc = st.substring(0, pad.length());
        } else {
            String padNeeded = pad.substring(0, pad.length() - st.length());
            if (padLeft) {
                rc = padNeeded + st;
            } else {
                rc = st + padNeeded;
            }
        }
        return rc;
    }

    public static final String pad(long i, String pad) {
        return CacheElement.pad(i, pad, true);
    }

    public static final String format(long day,
                                      long hour,
                                      long minute,
                                      long second,
                                      long millisecond,
                                      String dayPad) {
        StringBuffer sb = new StringBuffer();
        if (dayPad != null) {
            sb.append(CacheElement.pad(day, "00"));
            sb.append(" ");
        } else {
            sb.append(Long.toString(day));
            sb.append(" days ");
        }
        sb.append(CacheElement.pad(hour, "00"));
        sb.append(":");
        sb.append(CacheElement.pad(minute, "00"));
        sb.append(":");
        sb.append(CacheElement.pad(second, "00"));
        sb.append(".");
        sb.append(CacheElement.pad(millisecond, "000"));
        return sb.toString();
    }

    public static final String format(long year,
                                      long month,
                                      long day,
                                      long hour,
                                      long minute,
                                      long second,
                                      long millisecond) {
        StringBuffer sb = new StringBuffer();
        sb.append(CacheElement.pad(year, "0000"));
        sb.append("-");
        sb.append(CacheElement.pad(month, "00"));
        sb.append("-");
        sb.append(format(day, hour, minute, second, millisecond, "00"));
        return sb.toString();
    }

    public static final String format(Calendar time) {
        return format(time.get(Calendar.YEAR),
                      time.get(Calendar.MONTH) + 1,
                      time.get(Calendar.DATE),
                      time.get(Calendar.HOUR_OF_DAY),
                      time.get(Calendar.MINUTE),
                      time.get(Calendar.SECOND),
                      time.get(Calendar.MILLISECOND));
        /*
         * StringBuffer sb = new StringBuffer();
         * sb.append(CacheElement.pad(time.get(Calendar.YEAR),"0000"));
         * sb.append("/"); sb.append(CacheElement.pad((time.get(Calendar.MONTH) +
         * 1),"00")); sb.append("/");
         * sb.append(CacheElement.pad(time.get(Calendar.DATE),"00"));
         * sb.append(" ");
         * sb.append(CacheElement.pad(time.get(Calendar.HOUR_OF_DAY),"00"));
         * sb.append(":");
         * sb.append(CacheElement.pad(time.get(Calendar.MINUTE),"00"));
         * sb.append(":");
         * sb.append(CacheElement.pad(time.get(Calendar.SECOND),"00"));
         * sb.append(" (");
         * sb.append(CacheElement.pad(time.get(Calendar.MILLISECOND),"000"));
         * sb.append(")"); return sb.toString();
         */
    }

    public static final long MILLIS_IN_SECOND = 1000;

    public static final long MILLIS_IN_MINUTE = 60 * MILLIS_IN_SECOND;

    public static final long MILLIS_IN_HOUR = 60 * MILLIS_IN_MINUTE;

    public static final long MILLIS_IN_DAY = 24 * MILLIS_IN_HOUR;

    public static final String difference(Calendar earlier, Calendar later) {
        long milliseconds = later.getTimeInMillis() - earlier.getTimeInMillis();

        long days = milliseconds / MILLIS_IN_DAY;
        milliseconds = milliseconds % MILLIS_IN_DAY;
        long hours = milliseconds / MILLIS_IN_HOUR;
        milliseconds = milliseconds % MILLIS_IN_HOUR;
        long minutes = milliseconds / MILLIS_IN_MINUTE;
        milliseconds = milliseconds % MILLIS_IN_MINUTE;
        long seconds = milliseconds / MILLIS_IN_SECOND;
        milliseconds = milliseconds % MILLIS_IN_SECOND;
        String rc = format(days, hours, minutes, seconds, milliseconds, null);
        return rc;
    }

    public static final String compareForExpiration(Calendar first,
                                                    Calendar second) {
        String rc = null;
        if (first.before(second)) {
            rc = "expires in " + difference(first, second);
        } else {
            rc = "expired " + difference(second, first) + " ago";
        }
        return rc;
    }

    public final void audit() {
        String m = getCacheAbbrev() + " audit() ";
        if (LOG.isDebugEnabled()) {
            try {
                Calendar now = Calendar.getInstance();
                LOG.debug(m + "> " + getCacheid() + " " + getInstanceId()
                        + " @ " + format(now));
                LOG.debug(m + "valid==" + isValid());
                LOG.debug(m + "userid==" + getUserid());
                LOG.debug(m + "password==" + getPassword());
                LOG.debug(m + "authenticated==" + getAuthenticated());
                LOG.debug(m + "errorMessage==" + getErrorMessage());
                LOG.debug(m + "expiration==" + format(getExpiration()));
                LOG.debug(m + compareForExpiration(now, getExpiration()));
                if (this.getNamedValues() == null) {
                    LOG.debug(m + "(no named attributes");
                } else {
                    CacheElement.auditNamedValues(m, this.getNamedValues());
                }
            } finally {
                LOG.debug(m + "<");
            }
        }
    }

    //callback
    public final void populate(Boolean authenticated,
                               Set predicates,
                               Map namedValues,
                               String errorMessage) {
        String m = getCacheAbbrev() + " populate() ";
        LOG.debug(m + ">");
        try {
            if (predicates != null) {
                //to do:  remove parm predicates as unused
                LOG.warn(m + "non-null parm predicates");
            }
            assertInvalid();
            if (errorMessage != null) {
                LOG.error(m + "errorMessage==" + errorMessage);
                throw new Exception(errorMessage);
            } else {
                validate(authenticated, namedValues);
                //can't set expiration here -- don't have cache reference
                //can't set pwd here, don't have it
            }
        } catch (Throwable t) {
            LOG.error(m + "invalidating to be sure");
            this.invalidate(errorMessage);
        } finally {
            LOG.debug(m + "<");
        }
    }

    private static final Calendar calcExpiration(int duration, int unit) {
        String m = "- calcExpiration(int,int) ";
        LOG.debug(m + ">");
        Calendar now = Calendar.getInstance();
        Calendar rc = Calendar.getInstance();
        try {
            CacheElement.checkCalcExpiration(duration, unit);
            if (duration > 0) {
                rc.add(unit, duration);
                LOG.debug(m + CacheElement.compareForExpiration(now, rc));
            } else {
                LOG.debug(m + "timeout set to now (effectively, no caching)");
            }
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(m + "< " + format(rc));
            }
        }
        return rc;
    }

    public static final int calcCalendarUnit(String unit) {
        String m = "- calcCalendarUnit() ";
        int rc = Calendar.SECOND;
        if (!unit.endsWith("s")) {
            unit += "s";
        }
        if ("milliseconds".equalsIgnoreCase(unit)) {
            rc = Calendar.MILLISECOND;
        } else if ("seconds".equalsIgnoreCase(unit)) {
            rc = Calendar.SECOND;
        } else if ("minutes".equalsIgnoreCase(unit)) {
            rc = Calendar.MINUTE;
        } else if ("hours".equalsIgnoreCase(unit)) {
            rc = Calendar.HOUR;
        } else {
            String msg = "illegal Calendar unit: " + unit;
            LOG.error(m + "(" + msg + ")");
            throw new IllegalArgumentException(msg);
        }
        return rc;
    }

    private static final Calendar calcExpiration(int duration, String unit) {
        String m = "- calcExpiration(int,String) ";
        //LOG.debug(m + ">");
        Calendar rc = Calendar.getInstance();
        int calendarUnit = Calendar.SECOND;
        try {
            calendarUnit = calcCalendarUnit(unit);
        } catch (Throwable t) {
            duration = 0;
            LOG.error(m + "using duration==" + duration);
            LOG.error(m + "using calendarUnit==" + calendarUnit);
        } finally {
            rc = CacheElement.calcExpiration(duration, calendarUnit);
            /*
             * if (LOG.isDebugEnabled()) { LOG.debug(m + "< " + format(rc)); }
             */
        }
        return rc;
    }

    public static final boolean isExpired(Calendar now,
                                          Calendar expiration,
                                          boolean verbose) {
        String m = "- isExpired() ";
        if (verbose) {
            LOG.debug(m + ">");
        }
        boolean rc = CacheElement.s_expired_default;
        try {
            if (now == null) {
                String msg = "illegal parm now==" + now;
                LOG.error(m + "(" + msg + ")");
                throw new IllegalArgumentException(msg);
            }
            if (expiration == null) {
                String msg = "illegal parm expiration==" + expiration;
                LOG.error(m + "(" + msg + ")");
                throw new IllegalArgumentException(msg);
            }
            if (verbose) {
                LOG.debug(m + "now==" + format(now));
                LOG.debug(m + "exp==" + format(expiration));
            }
            rc = !now.before(expiration);
        } catch (Throwable th) {
            LOG.error(m + "failed comparison");
            rc = CacheElement.s_expired_default;
        } finally {
            if (verbose) {
                LOG.debug(m + compareForExpiration(now, expiration));
                LOG.debug(m + "< " + rc);
            }
        }
        return rc;
    }

    public static final boolean isExpired(Calendar now, Calendar expiration) {
        return isExpired(now, expiration, true);
    }

    public static final boolean isExpired(Calendar expiration, boolean verbose) {
        String m = "- isExpired() ";
        /*
         * if (verbose) { LOG.debug(m + ">"); }
         */
        boolean rc = CacheElement.s_expired_default;
        try {
            if (expiration == null) {
                String msg = "illegal parm expiration==" + expiration;
                LOG.error(m + "(" + msg + ")");
                throw new IllegalArgumentException(msg);
            }
            Calendar now = Calendar.getInstance();
            rc = CacheElement.isExpired(now, expiration, verbose);
        } catch (Throwable th) {
            LOG.error(m + "failed comparison");
            rc = CacheElement.s_expired_default;
        } finally {
            /*
             * if (verbose) { LOG.debug(m + "< " + rc); }
             */
        }
        return rc;
    }

    public static final boolean isExpired(Calendar now) {
        return isExpired(now, true);
    }

    /**
     * synchronize so evaluation of cache item state will be sequential,
     * non-interlaced (protect against overlapping calls resulting in redundant
     * authenticator calls)
     */
    public final synchronized Boolean authenticate(Cache cache, String pwd) {
        String m = getCacheAbbrev() + " authenticate() ";
        LOG.debug(m + ">");
        Boolean rc = null;
        try {
            LOG.debug(m + "isValid()==" + isValid());
            if (isValid() && !CacheElement.isExpired(getExpiration())) {
                LOG.debug(m + "valid and not expired, so use");
                if (!isAuthenticated()) {
                    LOG.debug(m + "auth==" + getAuthenticated());
                    rc = getAuthenticated();
                } else {
                    LOG.debug(m + "already authd, request password==" + pwd);
                    if (pwd == null) {
                        LOG.debug(m + "null request password");
                        rc = Boolean.FALSE;
                    } else if ("".equals(pwd)) {
                        LOG.debug(m + "zero-length request password");
                        rc = Boolean.FALSE;
                    } else {
                        LOG.debug(m + "stored password==" + getPassword());
                        rc = pwd.equals(getPassword());
                    }
                }
            } else { // expired or invalid
                LOG.debug(m + "expired or invalid, so try to repopulate");
                this.invalidate();
                CacheElementPopulator cePop = cache.getCacheElementPopulator();
                cePop.populateCacheElement(this, pwd);
                int duration = 0;
                String unit = null;
                setPassword(null);
                if (getAuthenticated() == null || !isValid()) {
                    duration = cache.getAuthExceptionTimeoutDuration();
                    unit = cache.getAuthExceptionTimeoutUnit();
                    LOG.debug(m + "couldn't complete population");
                } else {
                    LOG.debug(m + "populate completed");
                    if (isAuthenticated()) {
                        setPassword(pwd);
                        duration = cache.getAuthSuccessTimeoutDuration();
                        unit = cache.getAuthSuccessTimeoutUnit();
                        LOG.debug(m + "populate succeeded");
                    } else {
                        duration = cache.getAuthFailureTimeoutDuration();
                        unit = cache.getAuthFailureTimeoutUnit();
                        LOG.debug(m + "populate failed");
                    }
                }
                setExpiration(CacheElement.calcExpiration(duration, unit));
                rc = getAuthenticated();
            }
        } catch (Throwable th) {
            this.invalidate();
            rc = getAuthenticated();
            LOG.error(m + "invalidating to be sure");
        } finally {
            audit();
            LOG.debug(m + "< " + rc);
        }
        return rc;
    }

    /*
     * synchronize so evaluation of cache item state will be sequential, non-
     * interlaced (protect against overlapping calls resulting in redundant
     * authenticator calls)
     */
    public final synchronized Map getNamedValues(Cache cache, String pwd) {
        //to do:  refactor method name so that it doesn't look like "getter"
        String m = getCacheAbbrev() + " getNamedValues() ";
        LOG.debug(m + ">");
        Map rc = null;
        try {
            LOG.debug(m + "isValid()==" + isValid());
            if (isValid() && !CacheElement.isExpired(getExpiration())) {
                LOG.debug(m + "valid and not expired, so use");
            } else {
                LOG.debug(m + "expired or invalid, so try to repopulate");
                this.invalidate();
                CacheElementPopulator cePop = cache.getCacheElementPopulator();
                cePop.populateCacheElement(this, pwd);
                int duration = 0;
                String unit = null;
                if (this.getNamedValues() == null || !isValid()) {
                    duration = cache.getAuthExceptionTimeoutDuration();
                    unit = cache.getAuthExceptionTimeoutUnit();
                    LOG.debug(m + "couldn't complete population");
                } else {
                    LOG.debug(m + "populate completed");
                    if (this.getNamedValues() == null) {
                        duration = cache.getAuthFailureTimeoutDuration();
                        unit = cache.getAuthFailureTimeoutUnit();
                        LOG.debug(m + "populate failed");
                    } else {
                        duration = cache.getAuthSuccessTimeoutDuration();
                        unit = cache.getAuthSuccessTimeoutUnit();
                        LOG.debug(m + "populate succeeded");
                    }
                }
                setExpiration(CacheElement.calcExpiration(duration, unit));
            }
        } catch (Throwable th) {
            String msg = m + "invalidating to be sure";
            this.invalidate(msg);
            LOG.error(msg);
        } finally {
            audit();
            rc = this.getNamedValues();
            if (rc == null) {
                rc = m_empty_map;
            }
            LOG.debug(m + "< " + rc);
        }
        return rc;

    }

    static {
        String[] args = {};
        CacheElement.main(args);
    }

    public static final void main(String[] args) {

    }

}
