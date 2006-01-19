package fedora.server;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fedora.common.Constants;

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
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public abstract class Parameterized implements Constants {

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
     * Same as getParameter(String name) but prepends the location of 
     * FEDORA_HOME if the parameter location does not specify an absolute 
     * pathname.
     * 
     * @param name The parameter name
     * @return Null if undefined, an absolute pathname (relative to FEDORA_HOME 
     * if not absolute to begin with).
     */
    public final String getFileParameter(String name) {
    	String param = getParameter(name);
    	if (param != null) {
	    	File f = new File(param);
	    	if (!f.isAbsolute()) {
				param = FEDORA_HOME + File.separator + param;
			}
    	}
    	return param;
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

    public Map getParameters() {
        return m_parameters;
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