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
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The starting point for working with a Fedora repository. This class 
 * provides access to an appropriate <code>DOManager</code> for working with 
 * digital objects, handles loading, starting, and stopping modules (the module 
 * lifecycle), and provides access to core constants.
 * <p></p>
 * The <code>Server</code> class itself is abstract - it may not be 
 * instantiated.  Instead, it provides an instance of the <code>Server</code>
 * subclass specified in the configuration file. 
 * (see <code>CONFIG_DIR</code> and <code>CONFIG_FILE</code>)
 * <p></p>
 * <h3>Example Use</h3>
 * <p></p>
 * The following program illustrates use of the <code>Server</code> class.
 * <pre>
 * import fedora.server.Server;
 * import fedora.server.storage.DOManager;
 * import fedora.server.storage.DOReader;
 *
 * /**
 *  * Prints the label of the objects whose PIDs are passed at the command line.
 *  *&#47;
 * public class PrintLabel {
 *
 *     public static void main(String[] args) {
 *         try {
 *             Server s=Server.getInstance(System.getProperty(
 *                     Server.HOME_PROPERTY));
 *             DOManager m=s.getManager("fast");
 *             for (int i=0; i&lt;args.length; i++) 
 *                 System.out.println(m.getReader(args[i]).getObjectLabel());
 *             s.shutdown();
 *         } catch (Exception e) {
 *             System.out.println("Error: " + e.getMessage());
 *         }
 *     }
 * }
 * </pre>
 * <p></p>
 * <h3>Core Constants</h3>
 * <p></p>
 * All constants for the core Fedora classes are set within the 
 * <code>fedora/server/resources/Server.properties</code> file*, and are 
 * available as static fields of this class.  Non-core and extension classes 
 * may use an entirely different scheme for their own constants, and must at 
 * least use a different file.
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
 *        <code>fedora/server/resources/Server.properties_language[_country[_variant]]</code>
 *        exists and the appropriate "locale.language", and (optionally) 
 *        "locale.country", and (optionally) "locale.variant" property values 
 *        are set.
 *   </li>
 * </ul>
 * <p></p>
 * * Or a locale-specific version thereof.  Note that only localizable constants
 *   (messages) may change across locales.
 * <p></p> 
 * Messages are named using the following convention:
 * <p></p> 
 * <code>execpoint.messagetype.errname</code>
 * <p></p> 
 * where <code>execpoint</code> is composed of 
 * <code>phase[subphase.[subphase.(...)]]</code>
 * <p></p> 
 * Phase is a short string intended to show
 * at which point in the server's execution the condition described
 * by the message occurs. Subphase is a sub-categorization of a phase.  
 * For example, <code>init.config</code> and <code>init.server</code> are 
 * subphases of the init phase.
 * <p></p> 
 * <pre>
 * Phase/Subphase       Description
 * --------------       -----------
 * init                 Server initialization
 * init.xmlparser       XML parser initialization
 * init.config          Reading and validating configuration file
 * init.server          Initializing the server implementation
 * init.module          Initializing a module
 * storage              In the storage subsystem
 * api                  Server front-end
 * shutdown.server      Shutting down the server
 * shutdown.module      Shutting down a module
 * </pre>
 * There are several possible message types, described below.  These coincide
 * with jdk1.4's new 
 * <a href="http://java.sun.com/j2se/1.4/docs/api/java/util/logging/package-summary.html">java.util.logging</a>
 * package's log levels.
 * <pre>
 * MessageType          Description
 * -----------          -----------
 * severe               Errors that render the server inoperable or prevent
 *                      it from starting up in the first place.
 * warning              Errors that signal an undesired condition, but may be
 *                      recovered from.
 * info                 Interesting events that don't occur often, such as
 *                      Server and Module startup and shutdown.
 * config               Significant Server or Module configuration steps.
 * fine                 Request hosts and operations, success or fail.
 * finer                Full request, response, and timing information.
 * finest               Method entry and exit, and extremely verbose messages.
 * </pre>
 *
 * @author cwilper@cs.cornell.edu
 */
public abstract class Server 
        extends Pluggable {
 
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

    /** The directory where server extensions are stored, relative to home. */
    public static String EXTENSION_DIR=s_const.getString("extension.dir");

    /** The directory where server executables are stored, relative to home. */
    public static String BIN_DIR=s_const.getString("bin.dir");

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

    /** The configuration file comment element's name. */
    public static String CONFIG_ELEMENT_COMMENT=
            s_const.getString("config.element.comment");

    /** The configuration file datastore element's name. */
    public static String CONFIG_ELEMENT_DATASTORE=
            s_const.getString("config.element.datastore");

    /** The configuration file module element's name. */
    public static String CONFIG_ELEMENT_MODULE=
            s_const.getString("config.element.module");

    /** The configuration file param element's name. */
    public static String CONFIG_ELEMENT_PARAM=
            s_const.getString("config.element.param");

    /** 
     * The configuration file's class-specifying attribute for server and module 
     * elements.
     */
    public static String CONFIG_ATTRIBUTE_CLASS=
            s_const.getString("config.attribute.class");

    /** The configuration file param element's name attribute. */
    public static String CONFIG_ATTRIBUTE_NAME=
            s_const.getString("config.attribute.name");

    /** The configuration file param element's value attribute. */
    public static String CONFIG_ATTRIBUTE_VALUE=
            s_const.getString("config.attribute.value");

    /** The configuration file datastore element's id attribute. */
    public static String CONFIG_ATTRIBUTE_ID=
            s_const.getString("config.attribute.id");

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
    public static String INIT_XMLPARSER_SEVERE_MISSING=
            s_const.getString("init.xmlparser.severe.missing");

    /** 
     * Indicates that the config file could not be read. 0=config file full 
     * path, 1=additional info from underlying exception
     */
    public static String INIT_CONFIG_SEVERE_UNREADABLE=
            s_const.getString("init.config.severe.unreadable");

    /**
     * Indicates that the config file has malformed XML. 0=config file full 
     * path, 1=additional info from underlying exception
     */
    public static String INIT_CONFIG_SEVERE_MALFORMEDXML=
            s_const.getString("init.config.severe.malformedxml");

    /**
     * Indicates that the config file has a mis-named root element. 0=config 
     * file full path, 1={config.element.root}, 2=actual root element name
     */
    public static String INIT_CONFIG_SEVERE_BADROOTELEMENT=
            s_const.getString("init.config.severe.badrootelement");

    /**
     * Indicates that the config file's element's namespace does not match
     * {config.namespace}. 0=config file full path, 1={config.namespace}
     */
    public static String INIT_CONFIG_SEVERE_BADNAMESPACE=
            s_const.getString("init.config.severe.badnamespace");

    /**
     * Indicates that a parameter element in the config file is missing
     * a required element. 0=config file full path, 1={config.element.param},
     * 2={config.attribute.name}, 3={config.attribute.value}
     */
    public static String INIT_CONFIG_SEVERE_INCOMPLETEPARAM=MessageFormat.format(
            s_const.getString("init.config.severe.incompleteparam"),
            new Object[] {"{0}",CONFIG_ELEMENT_PARAM,CONFIG_ATTRIBUTE_NAME,
            CONFIG_ATTRIBUTE_VALUE});

    /**
     * Indicates that the server class could not be found. 0=server class 
     * specified in config root element
     */
    public static String INIT_SERVER_SEVERE_CLASSNOTFOUND=
            s_const.getString("init.server.severe.classnotfound");

    /**
     * Indicates that the server class couldn't be accessed due to security
     * misconfiguration. 0=server class specified in config root element
     */
    public static String INIT_SERVER_SEVERE_ILLEGALACCESS=
            s_const.getString("init.server.severe.illegalaccess");

    /**
     * Indicates that the server class constructor was invoked improperly
     * due to programmer error. 0=server class specified in config root element
     */
    public static String INIT_SERVER_SEVERE_BADARGS=
            s_const.getString("init.server.severe.badargs");

    /**
     * Indicates that the server class doesn't have a constructor
     * matching Server(NodeList, File), but needs one. 0=server class specified 
     * in config root element.
     */
    public static String INIT_SERVER_SEVERE_MISSINGCONSTRUCTOR=
            s_const.getString("init.server.severe.missingconstructor");

    /**
     * Indicates that the server class was abstract, but shouldn't be. 0=server 
     * class specified in config root element
     */
    public static String INIT_SERVER_SEVERE_ISABSTRACT=
            s_const.getString("init.server.severe.isabstract");

    /**
     * Holds an instance of a <code>Server</code> for each distinct 
     * <code>File</code> given as a parameter to <code>getInstance(...)</code>
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
     * Initializes the Server based on configuration.
     * <p></p>
     * Reads and schema-validates the configuration items in the given
     * DOM <code>NodeList</code>, validates required server params, 
     * initializes the <code>Server</code>, then initializes each module, 
     * validating its required params, then verifies that the server's 
     * required module roles have been met.
     *
     * @param configNode The children elements of the root element of the
     *                   config file.
     * @param homeDir The home directory of the server, used to interpret 
     *                relative paths used in configuration.
     * @throws ServerInitializationException If there was an error starting
     *         the server.
     * @throws ModuleInitializationException If there was an error starting
     *         a module.
     */
    protected Server(NodeList configNodes, File homeDir) 
            throws ServerInitializationException,
                   ModuleInitializationException {
        m_homeDir=homeDir;
        File configFile=new File(homeDir + File.separator + CONFIG_DIR 
                + File.separator + CONFIG_FILE);
        HashMap serverParams=new HashMap();
        for (int rootChildIndex=0; rootChildIndex<configNodes.getLength(); 
                rootChildIndex++) {
            Node n=configNodes.item(rootChildIndex);
            if (n.getNodeType()==Node.ELEMENT_NODE) {
                if (n.getLocalName().equals(CONFIG_ELEMENT_PARAM)) {
                    // if name-value pair, save in the server's config HashMap
                    NamedNodeMap attrs=n.getAttributes();
                    Node nameNode=attrs.getNamedItemNS(CONFIG_NAMESPACE,
                            CONFIG_ATTRIBUTE_NAME);
                    if (nameNode==null) {
                        nameNode=attrs.getNamedItem(CONFIG_ATTRIBUTE_NAME);
                    }
                    Node valueNode=attrs.getNamedItemNS(CONFIG_NAMESPACE,
                            CONFIG_ATTRIBUTE_VALUE);
                    if (valueNode==null) {
                        valueNode=attrs.getNamedItem(CONFIG_ATTRIBUTE_VALUE);
                    }
                    if (nameNode==null || valueNode==null) {
                        throw new ServerInitializationException(
                                MessageFormat.format(
                                INIT_CONFIG_SEVERE_INCOMPLETEPARAM, new Object[]
                                {configFile}));
                    }
                    serverParams.put(nameNode.getNodeValue(), 
                            valueNode.getNodeValue());
                } else if (n.getLocalName().equals(CONFIG_ELEMENT_DATASTORE)) {
                    // if datastore element, verify it has a unique id, load
                    // its params into a HashMap, instantiate a DatastoreConfig,
                    // and add it to m_datastoreConfigs (key=id)
                    NamedNodeMap attrs=n.getAttributes();
                    Node idNode=attrs.getNamedItemNS(CONFIG_NAMESPACE, 
                        CONFIG_ATTRIBUTE_ID);
                    if (idNode==null) {
                        idNode=attrs.getNamedItem(CONFIG_ATTRIBUTE_ID);
                    }
                    if (idNode==null) {
                        throw new ServerInitializationException("todo");
                    }
                    // read the params and instantiate the datasource
                } else if (n.getLocalName().equals(CONFIG_ELEMENT_MODULE)) {
                    // read the params and instantiate the class
                } else if (!n.getLocalName().equals(CONFIG_ELEMENT_COMMENT)) {
                    // warning, unrec element, ignored
                }
                System.out.println("found element: " + n.getLocalName());
            } else {
                // warning, unrec non-element, ignored
            }
        }
        setParameters(serverParams);
        
        
        // print em..to test
        Iterator i=parameterNames();
        while (i.hasNext()) {
            String n=(String) i.next();
            System.out.println("name=\"" + n + "\", value=\"" + getParameter(n) + "\"");
        }
        
        
        initServer();
        // foreach module, load an instance with the given
        // params
        
    }

    public Logger getLogger() {
        return null;
    }
    
    /**
     * Provides an instance of the server specified in the configuration
     * file at homeDir/CONFIG_DIR/CONFIG_FILE, or DEFAULT_SERVER_CLASS
     * if unspecified.
     *
     * @param homeDir The base directory for the server.
     * @return The instance.
     * @throws ServerInitializationException If there was an error starting
     *         the server.
     * @throws ModuleInitializationException If there was an error starting
     *         a module.
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
                        MessageFormat.format(INIT_CONFIG_SEVERE_BADROOTELEMENT,
                        new Object[] {configFile, CONFIG_ELEMENT_ROOT,
                        rootElement.getLocalName()}));
            }
            // ensure namespace specified properly
            if (!rootElement.getNamespaceURI().equals(CONFIG_NAMESPACE)) {
                throw new ServerInitializationException(MessageFormat.format(
                        INIT_CONFIG_SEVERE_BADNAMESPACE, new Object[] {
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
                s_instances.put(homeDir, inst);
                return inst;
            } catch (ClassNotFoundException cnfe) {
                throw new ServerInitializationException(
                        MessageFormat.format(INIT_SERVER_SEVERE_CLASSNOTFOUND, 
                        new Object[] {className}));
            } catch (IllegalAccessException iae) {
                // improbable
                throw new ServerInitializationException(
                        MessageFormat.format(INIT_SERVER_SEVERE_ILLEGALACCESS,
                        new Object[] {className}));
            } catch (IllegalArgumentException iae) {
                // improbable
                throw new ServerInitializationException(
                        MessageFormat.format(INIT_SERVER_SEVERE_BADARGS,
                        new Object[] {className}));
            } catch (InstantiationException ie) {
                throw new ServerInitializationException(
                        MessageFormat.format(
                        INIT_SERVER_SEVERE_MISSINGCONSTRUCTOR, 
                        new Object[] {className}));
            } catch (NoSuchMethodException nsme) {
                throw new ServerInitializationException(
                        MessageFormat.format(INIT_SERVER_SEVERE_ISABSTRACT,
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
                    INIT_XMLPARSER_SEVERE_MISSING);
        } catch (FactoryConfigurationError fce) {
            throw new ServerInitializationException(
                    INIT_XMLPARSER_SEVERE_MISSING);
        } catch (IOException ioe) {
            throw new ServerInitializationException(
                    MessageFormat.format(INIT_CONFIG_SEVERE_UNREADABLE,
                    new Object[] {configFile, ioe.getMessage()}));
        } catch (IllegalArgumentException iae) {
            throw new ServerInitializationException(
                    MessageFormat.format(INIT_CONFIG_SEVERE_UNREADABLE,
                    new Object[] {configFile, iae.getMessage()}));
        } catch (SAXException saxe) {
            throw new ServerInitializationException(
                    MessageFormat.format(INIT_CONFIG_SEVERE_MALFORMEDXML,
                    new Object[] {configFile, saxe.getMessage()}));
        }
    }

    /**
     * Gets the server's home directory.
     *
     * @return The directory.
     */
    public final File getHomeDir() {
        return m_homeDir;
    }
    
    /**
     * Gets the names of the roles that are required by this <code>Server</code>.
     *
     * @return The roles.
     */
    public String[] getRequiredModuleRoles() {
        return new String[] {DOMANAGER_CLASS};
    }

    /**
     * Gets a loaded <code>Module</code>.
     *
     * @param role The role of the <code>Module</code> to get.
     * @return The <code>Module</code>, <code>null</code> if not found.
     */
    public final Module getModule(String role) {
        return (Module) m_loadedModules.get(role);
    }
    
    /**
     * Gets an <code>Iterator</code> over the roles that have been loaded.
     *
     * @return (<code>String</code>s) The roles.
     */
    public final Iterator loadedModuleRoles() {
        return m_loadedModules.keySet().iterator();
    }
    
    /**
     * Performs any server start-up tasks particular to this type of Server.
     * <p></p>
     * The default implementation does nothing.
     * 
     * @throws ServerInitializationException If a severe server startup-related 
     *         error occurred.
     */
    protected void initServer()
            throws ServerInitializationException {
        if (1==2)
            throw new ServerInitializationException(null);
    }

    /**
     * Gets the <code>DOManager</code> instance appropriate for this 
     * <code>Server</code> instance, given the the interface (client program) 
     * profile.
     * <p></p>
     * <h3>About The Interface Profile</h3>
     * <p></p>
     * The <code>Server</code> subclass implementing this method is responsible
     * for defining the syntax and meaning of the interfaceProfile.
     * Generally, an interface profile identifies aspect(s) of the client 
     * program so that the <code>Server</code> can decide which DOManager 
     * instance is appropriate to return.
     * <p></p>
     * The interface profile concept is intentionally generic so that this
     * method may be overridden to encapsulate virtually any 
     * <code>DOManager</code> selection logic.
     *
     * @param profile The interface profile. (see above discussion)
     */
    public abstract DOManager getManager(String interfaceProfile);
    
    /**
     * Performs shutdown tasks for the modules and the server.
     * <p></p>
     * All loaded modules' shutdownModule() methods are called, then
     * shutdownServer is called.
     * <p></p>
     * <h3>How to Ensure Clean Server Shutdown</h3>
     * <p></p>
     * After having used a <code>Server</code> instance,
     * if you know your program is the only client of the <code>Server</code>
     * in the VM instance, you should make an explicit call to this method
     * so that you can catch and handle it's exceptions properly.
     * If you are usure or know that there may be at least one other client
     * of the <code>Server</code> in the VM instance, you should call
     * <code>System.runFinalization()</code> after ensuring you no longer
     * have a reference.  In this case, if there is no other reference
     * to the object in the VM, finalization will be called (but you will
     * be unable to catch <code>ShutdownException</code> variants, if thrown).
     *
     * @throws ServerShutdownException If a severe server shutdown-related error
     *         occurred.
     * @throws ModuleShutdownException If a severe module shutdown-related error
     *         occurred.
     */
    public final void shutdown()
            throws ServerShutdownException, ModuleShutdownException {
        Iterator roleIterator=loadedModuleRoles();
        while (roleIterator.hasNext()) {
            Module m=getModule((String) roleIterator.next());
            m.shutdownModule();
        }
        shutdownServer();
    }

    /**
     * Performs shutdown tasks for the server itself.
     * <p></p>
     * The default implementation does nothing - it should be overridden
     * in <code>Server</code> implementations that tie up system resources.
     * <p></p>
     * This should be written so that system resources are always freed,
     * regardless of whether there is an error.  If an error occurs,
     * it should be thrown as a <code>ServerShutdownException</code> after 
     * attempts to free every resource have been made.
     *
     * @throws ServerShutdownException If a severe server shutdown-related error
     *         occurred.
     */
    protected void shutdownServer()
            throws ServerShutdownException {
        if (1==2)
            throw new ServerShutdownException(null);
    }

    /**
     * Calls <code>shutdown()</code> when finalization occurs.
     *
     * @throws ServerShutdownException If a severe server shutdown-related error
     *         occurred.
     * @throws ModuleShutdownException If a severe module shutdown-related error
     *         occurred.
     */
    public final void finalize() 
            throws ServerShutdownException, ModuleShutdownException {
        shutdown();
    }

    /**
     * Tests this class.
     * 
     * @param Command-line arguments.
     */
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
