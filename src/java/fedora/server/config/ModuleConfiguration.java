package fedora.server.config;

import java.util.*;

/**
 *
 */
public class ModuleConfiguration 
        extends Configuration {

    private String m_roleName;
    private String m_className;
    private String m_comment;

    public ModuleConfiguration(List parameters,
                               String roleName,
                               String className,
                               String comment) {
        super(parameters);
        m_roleName = roleName;
        m_className = className;
        m_comment = comment;
    }

    public String getRole() {
        return m_roleName;
    }

    public String getClassName() {
        return m_className;
    }

    public String getComment() {
        return m_comment;
    }

}
