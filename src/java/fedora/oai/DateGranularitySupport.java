package fedora.oai;

/**
 *
 * <p><b>Title:</b> DateGranularitySupport.java</p>
 * <p><b>Description:</b> An indicator of the level of granularity in dates a
 * repository supports.</p>
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
public class DateGranularitySupport {

    /**
     * Indicates that the repository supports timestamp granularity in days.
     */
    public static final DateGranularitySupport DAYS=new DateGranularitySupport("YYYY-MM-DD");

    /**
     * Indicates that the repository supports timestamp granularity in seconds.
     */
    public static final DateGranularitySupport SECONDS=new DateGranularitySupport("YYYY-MM-DDThh:mm:ssZ");

    private String m_stringValue;

    private DateGranularitySupport(String stringValue) {
        m_stringValue=stringValue;
    }

    public String toString() {
        return m_stringValue;
    }

}