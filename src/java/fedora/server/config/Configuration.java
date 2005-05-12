package fedora.server.config;

import java.util.*;

/**
 *
 */
public abstract class Configuration {

    private List m_parameters;

    protected Configuration(List parameters) {
        m_parameters = parameters;
    }

    public List getParameters() {
        return m_parameters;
    }

    public Parameter getParameter(String name) {
        for (int i = 0; i < m_parameters.size(); i++) {
            Parameter param = (Parameter) m_parameters.get(i);
            if (param.getName().equals(name)) return param;
        }
        return null;
    }

    public void setParameterValue(String name, String value, boolean autoCreate) {
        Parameter param = getParameter(name);
        if (param == null) {
            m_parameters.add(new Parameter(name, value, null, new HashMap()));
        } else {
            param.setValue(value);
        }
    }

}
