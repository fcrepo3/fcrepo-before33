package fedora.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Abstract superclass of all Fedora components that can be configured by
 * a set of name-value pairs.
 *
 * @author cwilper@cs.cornell.edu
 */

/**
 *
 * <p><b>Title:</b> Parameterized.java</p>
 * <p><b>Description:</b> Abstract superclass of all Fedora components that
 * can be configured by a set of name-value pairs.</p>
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
 * @version 1.0
 */
public abstract class Parameterized {

    /** a reference to the provided params for this component */
    private Map m_parameters;

    /** an empty array of strings */
    private final static String[] EMPTY_STRING_ARRAY=new String[] {};

    /**
     * Creates a Parameterized with no parameters.
     */
    public Parameterized() {
        setParameters(null);
    }

    /**
     * Creates a Parameterized with name-value pairs from the supplied Map.
     *
     * @param parameters The map from which to derive the name-value pairs.
     */
    public Parameterized(Map parameters) {
        setParameters(parameters);
    }

    /**
     * Sets the parameters with name-value pairs from the supplied Map.
     *
     * This is protected because it is intended to only be called by
     * subclasses where super(Map m) is not possible to call at the start of
     * the constructor. Server.java:Server(URL) is an example of this.
     *
     * @param parameters The map from which to derive the name-value pairs.
     */
    protected final void setParameters(Map parameters) {
        m_parameters=parameters;
        if (m_parameters==null) {
            m_parameters=new HashMap();
        }
    }

    /**
     * Gets the value of a named configuration parameter.
     *
     * @param name The parameter name.
     * @return String The value, null if undefined.
     */
    public final String getParameter(String name) {
        return (String) m_parameters.get(name);
    }

    protected final void setParameter(String name, String value) {
        m_parameters.put(name, value);
    }

    /**
     * Gets an Iterator over the names of parameters for this component.
     *
     * @return Iterator The names.
     */
    public final Iterator parameterNames() {
        return m_parameters.keySet().iterator();
    }

}