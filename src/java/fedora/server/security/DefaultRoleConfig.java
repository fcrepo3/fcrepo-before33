package fedora.server.security;

public class DefaultRoleConfig extends AbstractRoleConfig {

    public static final String ROLE = "default";

    public DefaultRoleConfig() {
        super(null);
    }

    public String getRole() {
        return ROLE;
    }

}