package fedora.server.errors;

/**
 * Signifies that an error occurred during a module's shutdown.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ModuleShutdownException 
        extends ShutdownException {

    /** The role of the module in which the error occurred */
    private String m_role;

    /**
     * Creates a ModuleShutdownException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     * @param role The role of the module in which the error occurred.
     */
    public ModuleShutdownException(String message, String role) {
        super(message);
        m_role=role;
    }

    /**
     * Gets the module in which the error occurred.
     *
     * @returns Module The module.
     */
    public String getRole() {
        return m_role;
    }
}