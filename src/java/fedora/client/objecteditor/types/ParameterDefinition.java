package fedora.client.objecteditor.types;

import java.util.*;

/**
 * Defines a single parameter for a method.
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
 */
public class ParameterDefinition {

    private String m_name;
    private String m_label;
    private boolean m_isRequired;
    private String m_defaultValue;
    private List m_validValues;

    /**
     * Initialize a parameter definition with all values.
     *
     * The label, defaultValue, and validValues may each be null or empty.
     */
    public ParameterDefinition(String name, 
                               String label, 
                               boolean isRequired, 
                               String defaultValue, 
                               List validValues) {
        m_name=name;
        m_label=label;
        m_isRequired=isRequired;
        m_defaultValue=defaultValue;
        m_validValues=validValues;
    }

    public String getName() {
        return m_name;
    }

    public String getLabel() {
        return m_label;
    }

    public boolean isRequired() {
        return m_isRequired;
    }

    public String getDefaultValue() {
        return m_defaultValue;
    }

    public List validValues() {
        return m_validValues;
    }
}
