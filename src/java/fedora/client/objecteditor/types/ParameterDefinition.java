package fedora.client.objecteditor.types;

import java.util.*;

/**
 * Defines a single parameter for a method.
 * 
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
