package fedora.logging;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 *
 * <p><b>Title:</b> DatingFileHandler.java</p>
 * <p><b>Description:</b> </p>
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
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class DatingFileHandler
        extends StreamHandler {

    private File m_logDir;
    private int m_maxSize;
    private int m_flushThreshold;
    private String m_ext;
    private long m_maxMillis;
    private int m_maxFiles;
    private int m_initialSize;
    private int m_currentSize;
    private int m_recordsSinceFlush;
    private long m_startMillis;
    private String m_fnStart;
    private Formatter m_formatter;

    private final static int MILLIS_PER_DAY=1000 * 60 * 60 * 24;

    private String m_nowFormatted;
    private boolean m_settingOutputStream;

    public DatingFileHandler(File logDir, int maxSize, int maxDays,
            int maxFiles, String ext, Formatter formatter,
            int flushThreshold)
            throws IOException, FileNotFoundException, IllegalArgumentException {
        m_flushThreshold=flushThreshold;
        setFormatter(formatter);
        if (maxSize<0 || maxDays<0 || maxFiles<0) {
            throw new IllegalArgumentException();
        }
        m_logDir=logDir;
        m_maxSize=maxSize;
        m_maxMillis=maxDays * MILLIS_PER_DAY;
        m_maxFiles=maxFiles;
        m_ext=ext;

        // come up with starting string using current system time
        m_startMillis=System.currentTimeMillis();
        m_fnStart=getFormatted(new GregorianCalendar()) + "-";
        m_initialSize=formatter.getHead(this).length()
                + formatter.getTail(this).length();
        m_currentSize=m_initialSize;

        setOutputStream(new FileOutputStream(new File(m_logDir, m_fnStart
                + m_ext)));
        m_recordsSinceFlush=0;
    }

    public void setFormatter(Formatter formatter) {
        m_formatter=formatter;
        super.setFormatter(formatter);
    }

    public void publish(LogRecord record) {
        if (isLoggable(record) && m_formatter!=null) {
            m_currentSize+=m_formatter.format(record).length();
            boolean needNewFile=false;
            if (m_maxSize>0) {
                if (m_currentSize>m_maxSize) {
                    // if finish would happen in the same second as
                    // start, wait till some more time has passed
                    if (m_startMillis + 1000 < System.currentTimeMillis()) {
                        needNewFile=true;
                    }
                }
            }
            if (m_maxMillis>0) {
                if (System.currentTimeMillis() - m_startMillis > m_maxMillis) {
                    needNewFile=true;
                }
            }
            if (needNewFile) {
                try {
                    // set the new one first, then rename the old one
                    m_nowFormatted=getFormatted(new GregorianCalendar());
                    m_settingOutputStream=true;
                    setOutputStream(new FileOutputStream(new File(m_logDir,
                            m_nowFormatted + "-" + m_ext)));
                    doRename();
                    m_settingOutputStream=false;
                    // set these for next time
                    m_fnStart=m_nowFormatted + "-";
                    m_currentSize=m_initialSize;
                    m_startMillis=System.currentTimeMillis();
                } catch (FileNotFoundException fnfe) {
                }
                m_recordsSinceFlush=0;
            }
            super.publish(record);
            m_recordsSinceFlush++;
            if (m_recordsSinceFlush==m_flushThreshold) {
                flush();
                m_recordsSinceFlush=0;
            }
        }
    }

    public void close() {
        super.close();
        doRename();
    }

    private void doRename() {
        if (!m_settingOutputStream) {
            m_nowFormatted=getFormatted(new GregorianCalendar());
        }
        File oldFile=new File(m_logDir, m_fnStart + m_ext);
        oldFile.renameTo(new File(m_logDir, m_fnStart
                + m_nowFormatted + m_ext));
    }

    private String getFormatted(GregorianCalendar cal) {
        StringBuffer b=new StringBuffer();
        b.append(cal.get(Calendar.YEAR));
        int month=cal.get(Calendar.MONTH)+1;
        if (month<10) {
            b.append("0");
        }
        b.append(month);
        int day=cal.get(Calendar.DAY_OF_MONTH);
        if (day<10) {
            b.append("0");
        }
        b.append(day);
        int hour=cal.get(Calendar.HOUR_OF_DAY);
        if (hour<10) {
            b.append("0");
        }
        b.append(hour);
        int minute=cal.get(Calendar.MINUTE);
        if (minute<10) {
            b.append("0");
        }
        b.append(minute);
        int second=cal.get(Calendar.SECOND);
        if (second<10) {
            b.append("0");
        }
        b.append(second);
        return b.toString();
    }

}