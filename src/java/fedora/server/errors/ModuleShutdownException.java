package fedora.server.errors;

import fedora.server.Module;

/**
 * Signifies that an error occurred during a module's shutdown.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ModuleShutdownException 
        extends ShutdownException {

    /** The module in which the error occurred */
    private Module m_module;

    /**
     * Creates a ModuleShutdownException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     * @param module The module in which the error occurred.
     */
    public ModuleShutdownException(String message, Module module) {
        super(message);
    }

    /**
     * Gets the module in which the error occurred.
     *
     * @returns Module The module.
     */
    public Module getModule() {
        return m_module;
    }
}