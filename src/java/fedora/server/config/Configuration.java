package fedora.server.config;

import java.util.*;

public abstract class Configuration {

    private List m_parameters;

    protected Configuration(List parameters) {
        m_parameters = parameters;
    }

    public List getParameters() {
        return m_parameters;
    }

}
