package fedora.oai;

import java.util.Set;

/**
 *
 * <p><b>Title:</b> SimpleSetInfo.java</p>
 * <p><b>Description:</b> A simple implementation of SetInfo that provides
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
public class SimpleSetInfo
        implements SetInfo {

    private String m_name;
    private String m_spec;
    private Set m_descriptions;

    public SimpleSetInfo(String name, String spec, Set descriptions) {
        m_name=name;
        m_spec=spec;
        m_descriptions=descriptions;
    }

    public String getName() {
        return m_name;
    }

    public String getSpec() {
        return m_spec;
    }

    public Set getDescriptions() {
        return m_descriptions;
    }

}