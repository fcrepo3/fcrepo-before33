package fedora.server;

import java.util.Iterator;
import java.util.Map;

/**
 * Abstract superclass of all Fedora components that can be configured by
 * a set of name-value pairs.
 *
 * @author cwilper@cs.cornell.edu
 */
public abstract class ParameterizedComponent {

    /** a reference to the provided params for this component */
    private Map m_parameters;
    
    /** an empty array of strings */
    private final static String[] EMPTY_STRING_ARRAY=new String[] {};

    /** 
     * Creates a ParameterizedComponent with no parameters.
     */
    public ParameterizedComponent() {
    }
    
    /** 
     * Creates a ParameterizedComponent with name-value pairs from the
     * supplied Map.
     *
     * @param componentParameters The map from which to derive the
     *                            name-value pairs.
     */
    public ParameterizedComponent(Map componentParameters) {
        setParameters(componentParameters);
    }
    
    /** 
     * Sets the parameters with name-value pairs from the supplied Map.
     *
     * This is protected because it is intended to only be called by 
     * subclasses where super(Map m) is not possible to call at the start of 
     * the constructor. Server.java:Server(URL) is an example of this.
     *
     * @param componentParameters The map from which to derive the
     *                            name-value pairs.
     */
    protected final void setParameters(Map componentParameters) {
        m_parameters=componentParameters;
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
    
    /**
     * Gets an Iterator over the names of parameters for this component.
     *
     * @returns Iterator The names
     */
    public final Iterator parameterNames() {
        return m_parameters.keySet().iterator();
    }
    
    /**
     * Gets the names of required parameters for this component.
     *
     * @returns String[] The required parameter names.
     */
    public String[] getRequiredParameters() {
        return EMPTY_STRING_ARRAY;
    }
    /**
     * Gets the names of optional parameters for this component.
     *
     * @returns String[] The required parameter names.
     */
    public String[] getOptionalParameters() {
        return EMPTY_STRING_ARRAY;
    }
    
    /**
     * Gets a short explanation of how to use a named parameter.
     *
     * @param name The name of the parameter.
     * @returns String The explanation, null if no help is available or 
     *                 the parameter is unknown.
     */
    public String getParameterHelp(String name) {
        return null;
    }
    
    /**
     * Gets an explanation of how this component is to be configured via
     * parameters.
     *
     * This should not include the information available via getParameterHelp,
     * but is more intended as an overall explanation or an explanation of
     * those parameters whose names might be dynamic.
     */
    public abstract String getHelp();

}