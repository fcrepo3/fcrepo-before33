/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.security;

public class MethodRoleConfig
        extends AbstractRoleConfig {

    private final String m_role;

    public MethodRoleConfig(BMechRoleConfig bMechConfig, String methodName) {
        super(bMechConfig);
        m_role = bMechConfig.getRole() + "/" + methodName;
    }

    @Override
    public String getRole() {
        return m_role;
    }

}