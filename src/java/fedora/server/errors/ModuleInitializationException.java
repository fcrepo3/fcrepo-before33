package fedora.server.errors;

/**
 * Signifies that an error occurred during a module's initialization.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ModuleInitializationException 
        extends InitializationException {

    /** The module in which the error occurred */
    private Module m_module;

    /**
     * Creates a ModuleInitializationException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     * @param module The module in which the error occurred.
     */
    public ModuleInitializationException(String message, Module module) {
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