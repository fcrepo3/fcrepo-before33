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
     * @returns String The value, null if undefined.
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
     * @returns Iterator The names.
     */
    public final Iterator parameterNames() {
        return m_parameters.keySet().iterator();
    }
    
}