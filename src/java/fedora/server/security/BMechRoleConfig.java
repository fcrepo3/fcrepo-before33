package fedora.server.security;

import java.util.*;

public class BMechRoleConfig extends AbstractRoleConfig {

    private String m_role;
    private SortedMap<String, MethodRoleConfig> m_methodConfigs;

    public BMechRoleConfig(DefaultRoleConfig defaultConfig, String pid) {
        super(defaultConfig);
        m_role = pid;
        m_methodConfigs = new TreeMap<String, MethodRoleConfig>();
    }

    public String getRole() {
        return m_role;
    }

    public SortedMap<String, MethodRoleConfig> getMethodConfigs() {
        return m_methodConfigs;
    }

}