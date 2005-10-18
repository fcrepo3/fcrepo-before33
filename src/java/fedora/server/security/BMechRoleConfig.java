package fedora.server.security;

import java.util.*;

public class BMechRoleConfig extends AbstractRoleConfig {

    private String m_role;
    private SortedMap m_methodConfigs;

    public BMechRoleConfig(DefaultRoleConfig defaultConfig, String pid) {
        super(defaultConfig);
        m_role = pid;
        m_methodConfigs = new TreeMap();
    }

    public String getRole() {
        return m_role;
    }

    public SortedMap getMethodConfigs() {
        return m_methodConfigs;
    }

}