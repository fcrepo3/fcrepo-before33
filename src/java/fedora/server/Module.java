package fedora.server;

import java.util.Map;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ModuleShutdownException;

/**
 * The base class for Fedora server modules.
 *
 * A ParameterizedComponent with initModule() and shutdownModule()
 * methods.
 *
 * Modules are configured via a &lt;module&gt; element in conf/fedora.fcfc.  
 * The schema for this element is in fedora-config.xsd.
 *
 * Modules are instantiated during server start-up.
 *
 * @author cwilper@cs.cornell.edu
 */
public abstract class Module 
        extends ParameterizedComponent {

    private String m_role;

    /**
     * Creates and initializes the Module.
     *
     * When the server is starting up, this is invoked as part of the
     * initialization process.
     *
     * @param moduleParameters A pre-loaded Map of name-value pairs comprising
     *                         the intended configuration of this Module.
     * @param role The role this module fulfills, a java class name.
     * @throws ModuleInitializationException If initilization values are
     *                                       invalid or initialization fails
     *                                       for some other reason.
     */
    public Module(Map moduleParameters, String role)
            throws ModuleInitializationException {
        super(moduleParameters);
        m_role=role;
        initModule();
    }

    /**
     * Gets the role this module fulfills, as given in the constructor.
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
     *                                       invalid or initialization fails
     *                                       for some other reason.
     */
    public abstract void initModule()
            throws ModuleInitializationException;

    /**
     * Frees system resources allocated by this Module.
     *
     * @throws ModuleShutdownException If there is a problem freeing
     *                                 system resources.  Note that if there
     *                                 is a problem, it won't end up aborting
     *                                 the shutdown process.  Therefore, this
     *                                 method should do everything possible
     *                                 to recover from exceptional situations.
     */
    public abstract void shutdownModule() 
            throws ModuleShutdownException;

}