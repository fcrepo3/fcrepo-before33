package fedora.server.security;

public class MethodRoleConfig extends AbstractRoleConfig {

    private String m_role;

    public MethodRoleConfig(BMechRoleConfig bMechConfig, String methodName) {
        super(bMechConfig);
        m_role = bMechConfig.getRole() + "/" + methodName;
    }

    public String getRole() {
        return m_role;
    }

}