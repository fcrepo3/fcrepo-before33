package fedora.oai;

import java.util.Date;

/**
 *
 * <p><b>Title:</b> SimpleResumptionToken.java</p>
 * <p><b>Description:</b> A simple implementation of ResumptionToken that
 * provides getters on the values passed in the constructor.</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class SimpleResumptionToken
        implements ResumptionToken {

    private String m_value;
    private Date m_expirationDate;
    private long m_completeListSize;
    private long m_cursor;

    public SimpleResumptionToken(String value, Date expirationDate,
            long completeListSize, long cursor) {
        m_value=value;
        m_expirationDate=expirationDate;
        m_completeListSize=completeListSize;
        m_cursor=cursor;
    }

    public String getValue() {
        return m_value;
    }

    public Date getExpirationDate() {
        return m_expirationDate;
    }

    public long getCompleteListSize() {
        return m_completeListSize;
    }

    public long getCursor() {
        return m_cursor;
    }

}