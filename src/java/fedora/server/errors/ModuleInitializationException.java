package fedora.server.errors;

/**
 * Signifies that an error occurred during a module's initialization.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ModuleInitializationException 
        extends InitializationException {

    /** The role of the module in which the error occurred */
    private String m_role;

    /**
     * Creates a ModuleInitializationException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     * @param role The role of the module.
     */
    public ModuleInitializationException(String message, String role) {
        super(message);
        m_role=role;
    }

    /**
     * Gets the role of the module in which the error occurred.
     *
     * @returns The role.
     */
    public String getRole() {
        return m_role;
    }
}