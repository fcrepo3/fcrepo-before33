package fedora.server;

import fedora.server.errors.ServerInitializationException;
import fedora.server.errors.ServerShutdownException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ModuleShutdownException;
import fedora.server.storage.DOManager;

import java.io.IOException;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The 
 *
 * <h3>Core Constants</h3>
 * <p></p>
 * All constants for the core Fedora classes are set within the 
 * fedora/server/resources/Server.properties file*, and are available as static 
 * fields of this class.  Non-core and extension classes may use an entirely
 * different scheme for their own constants, and must at least use a different
 * file.
 * <p></p>
 * There are two types of core constants:
 * <ul>
 *   <li> <b>Non-Localizable</b><br>
 *        The values of these constants remain the same regardless of the 
 *        locale, and may be referred to directly in server documentation where 
 *        needed.
 *   </li>
 *   <li> <b>Localizable</b> (Messages)<br>
 *        These constants' values will likely be different across locales.
 *        Locale-specific values are automatically made available to the server
 *        when an appropriate file of the form:
 *        fedora/server/resources/Server.properties_language[_country[_variant]]
 *        exists and the appropriate "locale.language", and (optionally) 
 *        "locale.country", and (optionally) "locale.variant" property values 
 *        are set.
 *   </li>
 * </ul>
 * <p></p>
 * * Or a locale-specific version thereof.  Note that only localizable constants
 *   (messages) may change across locales.
 
#
# - Non-localizable
#   Properties whose values 1) must be consistent across locales,
#   and 2) may be referred to directly in all documentation.
#
# - Localizable
#   Properties whose values 1) will likely differ across locales,
#   and 2) may be referred to directly in locale-specific documentation.
#
# * NOTE *
#
# These property values are not intended to be modified by fedora server
# administrators.  Non-localizable property names and values, and localizable
# property *names* are to only be modified by fedora developers as they are 
# tightly coupled with source code and documentation.  Localizable property 
# *values* may be modified by translators or locale-knowledgeable developers.
# 
#
# LOCALIZABLE (Messages)
#
# Messages are named using the following convention:
#
# execpoint.messagetype.mnemonic
#
# where execpoint is composed of phase[subphase.[subphase.(...)]]
#
# Phase is a mnemonic intended to show (in very general terms) 
# at which point in the server's execution the condition described
# by the message occurs. Subphase is a sub-categorization of a phase.  
# For example, init.config and init.server are subphases of the init phase.
#
# Phase/Subphase       Description
# --------------       -----------
# init                 Server initialization
# init.xmlparser       XML parser initialization
# init.config          Reading and validating configuration file
# init.server          Initializing the server implementation
# init.module          Initializing a module
# storage              In the storage subsystem
# api                  Server front-end
# shutdown.server      Shutting down the server
# shutdown.module      Shutting down a module
#
# MessageType          Description
# -----------          -----------
#
# fatal                Severe errors that cause the server to terminate 
#                      prematurely.
# error                Runtime errors or unexpected conditions that cannot
#                      be recovered from on a per-incident basis, but don't
#                      cause server shutdown.
# warn                 Use of deprecated APIs or runtime conditions that are
#                      undesirable but not by themselves unrecoverable.
# info                 Interesting runtime events such as server or module 
#                      startup, shutdown, and periodic server status messages.
# debug                detailed information on flow through the system. 
# trace                more detailed information.
#
 */
public abstract class Server 
        extends ParameterizedComponent {

    /** 
     * The ResourceBundle that provides access to constants from
     * fedora/server/resources/Server.properties.
     */
    private static ResourceBundle s_const =
            ResourceBundle.getBundle("fedora.server.resources.Server");

    /** The major version of this release. */
    public static String VERSION_MAJOR=s_const.getString("version.major");
    
    /** The minor version of this release. */
    public static String VERSION_MINOR=s_const.getString("version.minor");
    
    /** The name of the property that tells the server where it's based. */
    public static String HOME_PROPERTY=s_const.getString("home.property");

    /** The directory where server configuration is stored, relative to home. */
    public static String CONFIG_DIR=s_const.getString("config.dir");

    /** The configuration filename. */
    public static String CONFIG_FILE=s_const.getString("config.file");

    /** 
     * The prefix to all fedora-defined namespaces for this version.
     * 0={version.major}, 1={version.minor}
     */
    public static String NAMESPACE_PREFIX=MessageFormat.format(
            s_const.getString("namespace.prefix"),
            new Object[] {VERSION_MAJOR, VERSION_MINOR});

    /** The configuration file elements' namespace. 0={namespace.prefix} */
    public static String CONFIG_NAMESPACE=MessageFormat.format(
            s_const.getString("config.namespace"), 
            new Object[] {NAMESPACE_PREFIX});

    /** The configuration file root element's name. */
    public static String CONFIG_ELEMENT_ROOT=
            s_const.getString("config.element.root");

    /** 
     * The configuration file's class-specifying attribute for server and module 
     * elements.
     */
    public static String CONFIG_ATTRIBUTE_CLASS=
            s_const.getString("config.attribute.class");

    /** The required server constructor's first parameter's class. */
    public static String SERVER_CONSTRUCTOR_PARAM1_CLASS=
            s_const.getString("server.constructor.param1.class");

    /** The required server constructor's second parameter's class. */
    public static String SERVER_CONSTRUCTOR_PARAM2_CLASS=
            s_const.getString("server.constructor.param2.class");

    /** The name of the DOManager class (a Fedora server module "role"). */
    public static String DOMANAGER_CLASS=s_const.getString("domanager.class");

    /** The name of the default Server implementation class */
    public static String DEFAULT_SERVER_CLASS=
            s_const.getString("default.server.class");

    /** Indicates that an XML parser could not be found. */
    public static String INIT_XMLPARSER_FATAL_MISSING=
            s_const.getString("init.xmlparser.fatal.missing");

    /** 
     * Indicates that the config file could not be read. 0=config file full 
     * path, 1=additional info from underlying exception
     */
    public static String INIT_CONFIG_FATAL_UNREADABLE=
            s_const.getString("init.config.fatal.unreadable");

    /**
     * Indicates that the config file has malformed XML. 0=config file full 
     * path, 1=additional info from underlying exception
     */
    public static String INIT_CONFIG_FATAL_MALFORMEDXML=
            s_const.getString("init.config.fatal.malformedxml");

    /**
     * Indicates that the config file has a mis-named root element. 0=config 
     * file full path, 1={config.element.root}, 2=actual root element name
     */
    public static String INIT_CONFIG_FATAL_BADROOTELEMENT=
            s_const.getString("init.config.fatal.badrootelement");

    /**
     * Indicates that the config file's element's namespace does not match
     * {config.namespace}. 0=config file full path, 1={config.namespace}
     */
    public static String INIT_CONFIG_FATAL_BADNAMESPACE=
            s_const.getString("init.config.fatal.badnamespace");

    /**
     * Indicates that the server class could not be found. 0=server class 
     * specified in config root element
     */
    public static String INIT_SERVER_FATAL_CLASSNOTFOUND=
            s_const.getString("init.server.fatal.classnotfound");

    /**
     * Indicates that the server class couldn't be accessed due to security
     * misconfiguration. 0=server class specified in config root element
     */
    public static String INIT_SERVER_FATAL_ILLEGALACCESS=
            s_const.getString("init.server.fatal.illegalaccess");

    /**
     * Indicates that the server class constructor was invoked improperly
     * due to programmer error. 0=server class specified in config root element
     */
    public static String INIT_SERVER_FATAL_BADARGS=
            s_const.getString("init.server.fatal.badargs");

    /**
     * Indicates that the server class doesn't have a constructor
     * matching Server(NodeList, File), but needs one. 0=server class specified 
     * in config root element.
     */
    public static String INIT_SERVER_FATAL_MISSINGCONSTRUCTOR=
            s_const.getString("init.server.fatal.missingconstructor");

    /**
     * Indicates that the server class was abstract, but shouldn't be. 0=server 
     * class specified in config root element
     */
    public static String INIT_SERVER_FATAL_ISABSTRACT=
            s_const.getString("init.server.fatal.isabstract");

    /**
     * Holds an instance for each server config file used in getInstance(...)
     */
    private static HashMap s_instances=new HashMap();
    
    /**
     * The server's home directory.
     */
    private File m_homeDir;
    
    /**
     * Modules that have been loaded.
     */
    private HashMap m_loadedModules;

    /**
     * Reads and schema-validates the configuration items in the given
     * DOM NodeList, validates required server params, initializes the 
     * server, then initializes each module, validating its
     * required params, then verifies that the server's required module
     * roles have been met.
     *
     * @param homeDir FEDORA_HOME, used to interpret relative paths
     *                      used in configuration.
     */
    protected Server(NodeList configNodes, File homeDir) 
            throws ServerInitializationException,
                   ModuleInitializationException {
        m_homeDir=homeDir;
        // do what this was gonna do: loadConfiguration(serverParams);
        initServer();
        // foreach module, load an instance with the given
        // params
        
    }
    
    /**
     * Provides an instance of the server specified in the configuration
     * file at homeDir/{config.dir}/{config.file}, or {default.server.class}
     * if unspecified.
     *
     * @param homeDir The base directory for the server.
     * @returns Server The instance.
     * @throws ServerInitializationException If there was an error starting
     *         the server.
     * @throws ModuleInitializationException If there was an error starting
     *         a specified module.
     */
    public final static Server getInstance(File homeDir)
            throws ServerInitializationException,
                   ModuleInitializationException {
        // return an instance if already in memory
        Server instance=(Server) s_instances.get(homeDir);
        if (instance!=null) {
            return instance;
        }
        // else instantiate a new one given the class provided in the
        // root element in the config file and return it
        File configFile=null;
        try {
            DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder=factory.newDocumentBuilder();
            configFile=new File(homeDir + File.separator + CONFIG_DIR 
                    + File.separator + CONFIG_FILE);
            // suck it in
            Element rootElement=builder.parse(configFile).getDocumentElement();
            // ensure root element name ok
            if (!rootElement.getLocalName().equals(CONFIG_ELEMENT_ROOT)) {
                throw new ServerInitializationException(
                        MessageFormat.format(INIT_CONFIG_FATAL_BADROOTELEMENT,
                        new Object[] {configFile, CONFIG_ELEMENT_ROOT,
                        rootElement.getLocalName()}));
            }
            // ensure namespace specified properly
            if (!rootElement.getNamespaceURI().equals(CONFIG_NAMESPACE)) {
                throw new ServerInitializationException(MessageFormat.format(
                        INIT_CONFIG_FATAL_BADNAMESPACE, new Object[] {
                        configFile, CONFIG_NAMESPACE}));
            }
            // select <server class="THIS_PART"> .. </server>
            String className=rootElement.getAttribute(CONFIG_ATTRIBUTE_CLASS);
            if (className.equals("")) {
                className=rootElement.getAttributeNS(CONFIG_NAMESPACE, 
                        CONFIG_ATTRIBUTE_CLASS);
                if (className.equals("")) {
                    className=DEFAULT_SERVER_CLASS;
                }
            }
            try {
                Class serverClass=Class.forName(className);
                Class param1Class=Class.forName(SERVER_CONSTRUCTOR_PARAM1_CLASS);
                Class param2Class=Class.forName(SERVER_CONSTRUCTOR_PARAM2_CLASS);
                Constructor serverConstructor=serverClass.getConstructor(
                        new Class[] {param1Class, param2Class});
                Server inst=(Server) serverConstructor.newInstance( 
                        new Object[] {rootElement.getChildNodes(), 
                        homeDir} );
                return inst;
            } catch (ClassNotFoundException cnfe) {
                throw new ServerInitializationException(
                        MessageFormat.format(INIT_SERVER_FATAL_CLASSNOTFOUND, 
                        new Object[] {className}));
            } catch (IllegalAccessException iae) {
                // improbable
                throw new ServerInitializationException(
                        MessageFormat.format(INIT_SERVER_FATAL_ILLEGALACCESS,
                        new Object[] {className}));
            } catch (IllegalArgumentException iae) {
                // improbable
                throw new ServerInitializationException(
                        MessageFormat.format(INIT_SERVER_FATAL_BADARGS,
                        new Object[] {className}));
            } catch (InstantiationException ie) {
                throw new ServerInitializationException(
                        MessageFormat.format(
                        INIT_SERVER_FATAL_MISSINGCONSTRUCTOR, 
                        new Object[] {className}));
            } catch (NoSuchMethodException nsme) {
                throw new ServerInitializationException(
                        MessageFormat.format(INIT_SERVER_FATAL_ISABSTRACT,
                        new Object[] {className}));
            } catch (InvocationTargetException ite) {
                // throw the constructor's thrown exception, if any
                try {
                    throw ite.getCause();  // as of java 1.4
                } catch (ServerInitializationException sie) {
                    throw sie;
                } catch (ModuleInitializationException mie) {
                    throw mie;
                } catch (Throwable t) {
                    // impossible 
                    return null;
                }
            } 
        } catch (ParserConfigurationException pce) {
            throw new ServerInitializationException(
                    INIT_XMLPARSER_FATAL_MISSING);
        } catch (FactoryConfigurationError fce) {
            throw new ServerInitializationException(
                    INIT_XMLPARSER_FATAL_MISSING);
        } catch (IOException ioe) {
            throw new ServerInitializationException(
                    MessageFormat.format(INIT_CONFIG_FATAL_UNREADABLE,
                    new Object[] {configFile, ioe.getMessage()}));
        } catch (IllegalArgumentException iae) {
            throw new ServerInitializationException(
                    MessageFormat.format(INIT_CONFIG_FATAL_UNREADABLE,
                    new Object[] {configFile, iae.getMessage()}));
        } catch (SAXException saxe) {
            throw new ServerInitializationException(
                    MessageFormat.format(INIT_CONFIG_FATAL_MALFORMEDXML,
                    new Object[] {configFile, saxe.getMessage()}));
        }
    }

    /**
     * Get the server's home directory.
     *
     * @returns File The directory.
     */
    public final File getHomeDir() {
        return m_homeDir;
    }
    
    /**
     * Gets the names of the roles that are required to be fulfilled by
     * modules specified in this server's configuration file.
     *
     * @returns String[] The roles.
     */
    public String[] getRequiredModuleRoles() {
        return new String[] {DOMANAGER_CLASS};
    }

    /**
     * Gets a loaded module.
     *
     * @param role The role of the module to get.
     * @returns Module The module, null if not found.
     */
    public final Module getModule(String role) {
        return (Module) m_loadedModules.get(role);
    }
    
    /**
     * Gets an iterator over the roles that have been loaded.
     *
     * @returns Iterator (Strings) The roles.
     */
    public final Iterator loadedModuleRoles() {
        return m_loadedModules.keySet().iterator();
    }
    
    /**
     * Performs any server start-up tasks particular to this type of Server.
     *
     * @throws ServerInitializationException If a fatal startup-related error
     *         occurred.
     */
    protected void initServer()
            throws ServerInitializationException {
        if (1==2)
            throw new ServerInitializationException(null);
    }
    
    public abstract DOManager getManager(String name);
    
    public final void shutdown()
            throws ServerShutdownException, ModuleShutdownException {
        Iterator roleIterator=loadedModuleRoles();
        while (roleIterator.hasNext()) {
            Module m=getModule((String) roleIterator.next());
            m.shutdownModule();
        }
        shutdownServer();
    }
    
    public void shutdownServer()
            throws ServerShutdownException {
        if (1==2)
            throw new ServerShutdownException(null);
    }
    
    public final void finalize() 
            throws ServerShutdownException, ModuleShutdownException {
        shutdown();
    }
    
    public static void main(String[] args) {
        // use the env var FEDORA_HOME
        // java -Dfedora.home=$FEDORA_HOME
        String serverHome=System.getProperty(Server.HOME_PROPERTY);
        if (serverHome==null) {
            System.err.println("Warning: " + Server.HOME_PROPERTY 
                    + " not set, using \".\"");
            serverHome=".";
        }
        try {
            Server server=Server.getInstance(new File(serverHome));
        } catch (ServerInitializationException sie) {
            System.err.println("Error: Server could not initialize: "
                    + sie.getMessage());
        } catch (ModuleInitializationException mie) {
            Module m=mie.getModule();
            System.err.println("Error: Module '" + m.getClass().getName() 
                    + "' (role='" + m.getRole() + "') could not initialize: " 
                    + mie.getMessage());
        }
    }

}
