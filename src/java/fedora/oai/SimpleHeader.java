package fedora.oai;

import java.util.Date;
import java.util.Set;

/**
 *
 * <p><b>Title:</b> SimpleHeader.java</p>
 * <p><b>Description:</b> A simple implementation of Header that provides
 * getters on the values passed in the constructor.</p>
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
public class SimpleHeader
        implements Header {

    private String m_identifier;
    private Date m_datestamp;
    private Set m_setSpecs;
    private boolean m_isAvailable;

    public SimpleHeader(String identifier, Date datestamp, Set setSpecs,
            boolean isAvailable) {
        m_identifier=identifier;
        m_datestamp=datestamp;
        m_setSpecs=setSpecs;
        m_isAvailable=isAvailable;
    }

    public String getIdentifier() {
        return m_identifier;
    }

    public Date getDatestamp() {
        return m_datestamp;
    }

    public Set getSetSpecs() {
        return m_setSpecs;
    }

    public boolean isAvailable() {
        return m_isAvailable;
    }

}