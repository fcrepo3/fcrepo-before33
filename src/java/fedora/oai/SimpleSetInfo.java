package fedora.oai;

import java.util.Set;

/**
 * A simple implementation of SetInfo that provides getters on the values
 * passed in the constructor.
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