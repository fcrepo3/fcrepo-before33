package fedora.server.search;

import java.util.List;

/**
 *
 * <p><b>Title:</b> FieldSearchQuery.java</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class FieldSearchQuery {

    public final static int CONDITIONS_TYPE=1;
    public final static int TERMS_TYPE=2;

    private List m_conditions;
    private String m_terms;
    private int m_type;

    public FieldSearchQuery(List conditions) {
        m_conditions=conditions;
        m_type=CONDITIONS_TYPE;
    }

    public FieldSearchQuery(String terms) {
        m_terms=terms;
        m_type=TERMS_TYPE;
    }

    public int getType() {
        return m_type;
    }

    public List getConditions() {
        return m_conditions;
    }

    public String getTerms() {
        return m_terms;
    }

}

