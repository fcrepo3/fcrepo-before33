package fedora.oai;

import java.util.Set;

/**
 *
 * <p><b>Title:</b> SimpleRecord.java</p>
 * <p><b>Description:</b> A simple implementation of Record that provides
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class SimpleRecord
        implements Record {

    private Header m_header;
    private String m_metadata;
    private Set m_abouts;

    public SimpleRecord(Header header, String metadata, Set abouts) {
        m_header=header;
        m_metadata=metadata;
        m_abouts=abouts;
    }

    public Header getHeader() {
        return m_header;
    }

    public String getMetadata() {
        return m_metadata;
    }

    public Set getAbouts() {
        return m_abouts;
    }

}
