package fedora.server.config;

import java.util.*;

/**
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
 */
public class Parameter {

    private String m_name;
    private String m_value;
    private String m_comment;
    private Map m_profileValues;

    public Parameter(String name,
                     String value,
                     String comment,
                     Map profileValues) {
        m_name = name;
        m_value = value;
        m_comment = comment;
        m_profileValues = profileValues;
    }

    public String getName() {
        return m_name;
    }

    public String getValue() {
        return m_value;
    }

    public void setValue(String newValue) {
        m_value = newValue;
    }

    public Map getProfileValues() {
        return m_profileValues;
    }

    public String getComment() {
        return m_comment;
    }

    public void setComment(String comment) {
        m_comment = comment;
    }

    public String toString() {
        return m_name;
    }

}
