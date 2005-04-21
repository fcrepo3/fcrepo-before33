package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> ModuleShutdownException.java</p>
 * <p><b>Description:</b> Signifies that an error occurred during a module's
 * shutdown.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
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
     * @param role The role of the module.
     */
    public ModuleShutdownException(String message, String role) {
        super(message);
        m_role=role;
    }

    public ModuleShutdownException(String message, String role, Throwable cause) {
        super(null, message, null, null, cause);
        m_role=role;
    }

    /**
     * Gets the role of the module in which the error occurred.
     *
     * @return The role.
     */
    public String getRole() {
        return m_role;
    }
}
