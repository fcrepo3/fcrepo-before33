package fedora.server;

import java.util.Map;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ModuleShutdownException;

/**
 * The base class for Fedora server modules.
 * <p></p>
 * A <code>Module</code> is a singleton object of a Fedora <code>Server</code> 
 * instance with a simple lifecycle, supported by the <code>initModule()</code>
 * and <code>shutdownModule()</code> methods, which are automatically called
 * during server startup and shutdown, respectively.
 * <p></p>
 * Modules are configured via "param" elements inside module elements
 * in the configuration file.  An instance of each module specified in the
 * configuration file is automatically created at startup and is available
 * via the <code>getModule(String)</code> instance method of the 
 * <code>Server</code> class.
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public abstract class Module 
        extends Pluggable 
        implements Logging {

    private String m_role;
    private Server m_server;

    /**
     * Creates and initializes the Module.
     * <p></p>
     * When the server is starting up, this is invoked as part of the
     * initialization process.
     * 
     * @param moduleParameters A pre-loaded Map of name-value pairs comprising
     *        the intended configuration of this Module.
     * @param server The <code>Server</code> instance.
     * @param role The role this module fulfills, a java class name.
     * @throws ModuleInitializationException If initilization values are
     *         invalid or initialization fails for some other reason.
     */
    public Module(Map moduleParameters, Server server, String role)
            throws ModuleInitializationException {
        super(moduleParameters);
        m_role=role;
        m_server=server;
        initModule();
    }

    /**
     * Gets the <code>Server</code> instance to which this <code>Module</code>
     * belongs.
     *
     * @returns The <code>Server</code> instance.
     */
    public Server getServer() {
        return m_server;
    }

    /**
     * Gets the role this module fulfills, as given in the constructor.
     * <p></p>
     * <i>Role</i> is the name of the class or interface that this 
     * concrete <code>Module</code> extends or implements.
     *
     * @returns String The role.
     */
    public final String getRole() {
        return m_role;
    }
    
    /**
     * Initializes the Module based on configuration parameters.
     *
     * @throws ModuleInitializationException If initialization values are
     *         invalid or initialization fails for some other reason.
     */
    public void initModule()
            throws ModuleInitializationException {
        if (1==2) throw new ModuleInitializationException(null, null);
    }

    /**
     * Second stage of Module initialization.
     *
     * This is guaranteed to run after all Module's initModule() methods
     * have run.
     *
     * @throws ModuleInitializationException If initialization values are
     *         invalid or initialization fails for some other reason.
     */
    public void postInitModule()
            throws ModuleInitializationException {
        if (1==2) throw new ModuleInitializationException(null, null);
    }

    /**
     * Frees system resources allocated by this Module.
     *
     * @throws ModuleShutdownException If there is a problem freeing
     *         system resources.  Note that if there is a problem, it won't end 
     *         up aborting the shutdown process.  Therefore, this method should 
     *         do everything possible to recover from exceptional situations
     *         before throwing an exception.
     */
    public void shutdownModule() 
            throws ModuleShutdownException {
        if (1==2) throw new ModuleShutdownException(null, null);
    }

    /**
     * Logs a SEVERE message, indicating that the server is inoperable or 
     * unable to start.
     *
     * @param message The message.
     */
    public final void logSevere(String message) {
        StringBuffer m=new StringBuffer();
        m.append(getClass().getName());
        m.append(": ");
        m.append(message);
        getServer().logSevere(m.toString());
    }
    
    public final boolean loggingSevere() {
        return getServer().loggingSevere();
    }
    
    /**
     * Logs a WARNING message, indicating that an undesired (but non-fatal)
     * condition occured.
     *
     * @param message The message.
     */
    public final void logWarning(String message) {
        StringBuffer m=new StringBuffer();
        m.append(getClass().getName());
        m.append(": ");
        m.append(message);
        getServer().logWarning(m.toString());
    }
    
    public final boolean loggingWarning() {
        return getServer().loggingWarning();
    }
    
    /**
     * Logs an INFO message, indicating that something relatively uncommon and
     * interesting happened, like server or module startup or shutdown, or
     * a periodic job.
     *
     * @param message The message.
     */
    public final void logInfo(String message) {
        StringBuffer m=new StringBuffer();
        m.append(getClass().getName());
        m.append(": ");
        m.append(message);
        getServer().logInfo(m.toString());
    }
    
    public final boolean loggingInfo() {
        return getServer().loggingInfo();
    }
    
    /**
     * Logs a CONFIG message, indicating what occurred during the server's
     * (or a module's) configuration phase.
     *
     * @param message The message.
     */
    public final void logConfig(String message) {
        StringBuffer m=new StringBuffer();
        m.append(getClass().getName());
        m.append(": ");
        m.append(message);
        getServer().logConfig(m.toString());
    }
    
    public final boolean loggingConfig() {
        return getServer().loggingConfig();
    }
    
    /**
     * Logs a FINE message, indicating basic information about a request to
     * the server (like hostname, operation name, and success or failure).
     *
     * @param message The message.
     */
    public final void logFine(String message) {
        StringBuffer m=new StringBuffer();
        m.append(getClass().getName());
        m.append(": ");
        m.append(message);
        getServer().logFine(m.toString());
    }
    
    public final boolean loggingFine() {
        return getServer().loggingFine();
    }
    
    /**
     * Logs a FINER message, indicating detailed information about a request
     * to the server (like the full request, full response, and timing
     * information).
     *
     * @param message The message.
     */
    public final void logFiner(String message) {
        StringBuffer m=new StringBuffer();
        m.append(getClass().getName());
        m.append(": ");
        m.append(message);
        getServer().logFiner(m.toString());
    }
    
    public final boolean loggingFiner() {
        return getServer().loggingFiner();
    }
    
    /**
     * Logs a FINEST message, indicating method entry/exit or extremely
     * verbose information intended to aid in debugging.
     *
     * @param message The message.
     */
    public final void logFinest(String message) {
        StringBuffer m=new StringBuffer();
        m.append(getClass().getName());
        m.append(": ");
        m.append(message);
        getServer().logFinest(m.toString());
    }

    public final boolean loggingFinest() {
        return getServer().loggingFinest();
    }
    
}