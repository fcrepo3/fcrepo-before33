package fedora.server.config;

import java.util.*;

public class ModuleConfiguration 
        extends Configuration {

    private String m_roleName;
    private String m_className;

    public ModuleConfiguration(List parameters,
                               String roleName,
                               String className) {
        super(parameters);
        m_roleName = roleName;
        m_className = className;
    }

    public String getRole() {
        return m_roleName;
    }

    public String getClassName() {
        return m_className;
    }

}
