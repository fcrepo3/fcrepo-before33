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
    public static String NAMESPACE_PREFIX=s_const.getString("namespace.prefix");

    /** The configuration file elements' namespace. 0={namespace.prefix} */
    public static String CONFIG_NAMESPACE=s_const.getString("config.namespace");

    /** The configuration file root element's name. */
    public static String CONFIG_ELEMENT_ROOT=
            s_const.getString("config.element.root");

    /** 
     * The configuration file's class-specifying attribute for server and module 
     * elements.
     */
    public static String CONFIG_ATTRIBUTE_CLASS=
            s_const.getString("config.attribute.class");

    /** The name of the DOManager class (a Fedora server module "role"). */
    public static String DOMANAGER_CLASS=s_const.getString("domanager.class");

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
    public static String INIT_CONFIG_FATAL_CLASSNOTFOUND=
            s_const.getString("init.server.fatal.classnotfound");

    /**
     * Indicates that the server class couldn't be accessed due to security
     * misconfiguration. 0=server class specified in config root element
     */
    public static String INIT_CONFIG_FATAL_ILLEGALACCESS=
            s_const.getString("init.server.fatal.illegalaccess");

    /**
     * Indicates that the server class constructor was invoked improperly
     * due to programmer error. 0=server class specified in config root element
     */
    public static String INIT_CONFIG_FATAL_BADARGS=
            s_const.getString("init.server.fatal.badargs");

    /**
     * Indicates that the server class doesn't have a constructor
     * matching Server(NodeList, File), but needs one. 0=server class specified 
     * in config root element.
     */
    public static String INIT_CONFIG_FATAL_MISSINGCONSTRUCTOR=
            s_const.getString("init.server.fatal.missingconstructor");

    /**
     * Indicates that the server class was abstract, but shouldn't be. 0=server 
     * class specified in config root element
     */
    public static String INIT_CONFIG_FATAL_ISABSTRACT=
            s_const.getString("init.server.fatal.isabstract");
 
        
    // FIXME: All messages and other locale-sensitive constants should be set 
    // in a resource bundle
    public static String XML_PARSER_UNAVAILABLE="An XML parser is unavailable "
            + "- make sure you're using a 1.4+ jre.";
            
    public static String ERROR_READING_FILE="Error reading file: ";
    
    public static String ERROR_PARSING_FILE="Error parsing file: ";
    
    public static String CONFIG_ROOT_ELEMENT_NAME="server";
    public static String BAD_ROOT_ELEMENT="Root element must be \""
            + CONFIG_ROOT_ELEMENT_NAME + "\".";
    public static String BAD_NAMESPACE_CHECK_VERSION="Fedora config namespace "
            + "is missing or incorrect (wrong version?), should be "
            + CONFIG_NAMESPACE;
    public static String SERVER_CLASS_NOT_FOUND="Specified server class not "
            + "found (misspelling or not installed in ext/) : ";
    public static String SERVER_ILLEGAL_ACCESS="Specified server class "
            + "constructor inaccessible (security misconfiguration or "
            + "programmer error) : ";
    public static String SERVER_BAD_ARGS="Specified server class constructor "
            + "invoked improperly (programmer error?) : ";
    public static String SERVER_MISSING_CONSTRUCTOR="Specified server class "
            + "must implement Server(NodeList, File) : ";
    public static String SERVER_ABSTRACT="Specified server class cannot be "
            + "declared abstract :";
    
    public static String CONFIG_CLASS_ATTRIBUTE="class";
    
    public static String DOMANAGER_CLASSNAME="fedora.server.storage.DOManager";
    
    private static HashMap s_instances=new HashMap();
    
    private File m_fedoraHome;
    
    private HashMap m_loadedModules;

    // these can be accessed from anywhere using Server.CONSTANTS.getString("key");
    public static ResourceBundle CONSTANTS=
            ResourceBundle.getBundle("fedora.server.resources.Server");
    
    /**
     * Reads and schema-validates the configuration items in the given
     * DOM NodeList, validates required server params, initializes the 
     * server, then initializes each module, validating its
     * required params, then verifies that the server's required module
     * roles have been met.
     *
     * @param fedoraHomeDir FEDORA_HOME, used to interpret relative paths
     *                      used in configuration.
     */
    protected Server(NodeList configNodes, File fedoraHomeDir) 
            throws ServerInitializationException,
                   ModuleInitializationException {
        // do what this was gonna do: loadConfiguration(serverParams);
        initServer();
        // foreach module, load an instance with the given
        // params
        
    }

    public final static Server getInstance(File fedoraHomeDir)
            throws ServerInitializationException,
                   ModuleInitializationException {
        // return an instance if already in memory
        Server instance=(Server) s_instances.get(fedoraHomeDir);
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
            configFile=new File(fedoraHomeDir + File.separator + CONFIG_DIR 
                    + File.separator + CONFIG_FILE);
            // suck it in
            Element rootElement=builder.parse(configFile).getDocumentElement();
            // ensure root element name ok
            if (!rootElement.getLocalName().equals(CONFIG_ELEMENT_ROOT)) {
                throw new ServerInitializationException(
                        INIT_CONFIG_FATAL_BADROOTELEMENT); // fixme:do replacements
            }
            // ensure namespace specified properly
            if (!rootElement.getNamespaceURI().equals(CONFIG_NAMESPACE)) {
                throw new ServerInitializationException(ERROR_PARSING_FILE
                        + configFile + " - " + BAD_NAMESPACE_CHECK_VERSION);
            }
            // select <server class="THIS_PART"> .. </server>
            String className=rootElement.getAttribute(CONFIG_CLASS_ATTRIBUTE);
            if (className.equals("")) {
                className=rootElement.getAttributeNS(CONFIG_NAMESPACE, 
                        CONFIG_CLASS_ATTRIBUTE);
                if (className.equals("")) {
                    // FIXME: This default should be set in the schema instead
                    className="fedora.server.BasicServer";
                }
            }
            try {
                Class serverClass=Class.forName(className);
                Class nlClass=Class.forName("org.w3c.dom.NodeList");
                Class fClass=Class.forName("java.io.File");
                Constructor serverConstructor=serverClass.getConstructor(
                        new Class[] {nlClass, fClass});
                Server inst=(Server) serverConstructor.newInstance( 
                        new Object[] {rootElement.getChildNodes(), 
                        fedoraHomeDir} );
                return inst;
            } catch (ClassNotFoundException cnfe) {
                throw new ServerInitializationException(
                        MessageFormat.format(CONSTANTS.getString(
                        "init.server.fatal.classnotfound"), 
                        new Object[] {className}));
            } catch (IllegalAccessException iae) {
                // improbable
                throw new ServerInitializationException(
                        MessageFormat.format(CONSTANTS.getString(
                        "init.server.fatal.illegalaccess"), 
                        new Object[] {className}));
            } catch (IllegalArgumentException iae) {
                // improbable
                throw new ServerInitializationException(
                        MessageFormat.format(CONSTANTS.getString(
                        "init.server.fatal.badargs"), 
                        new Object[] {className}));
            } catch (InstantiationException ie) {
                throw new ServerInitializationException(
                        MessageFormat.format(CONSTANTS.getString(
                        "init.server.fatal.missingconstructor"), 
                        new Object[] {className}));
            } catch (NoSuchMethodException nsme) {
                throw new ServerInitializationException(
                        MessageFormat.format(CONSTANTS.getString(
                        "init.server.fatal.isabstract"), 
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
            throw new ServerInitializationException(CONSTANTS.getString(
                    "init.xmlparser.fatal.missing"));
        } catch (FactoryConfigurationError fce) {
            throw new ServerInitializationException(CONSTANTS.getString(
                    "init.xmlparser.fatal.missing"));
        } catch (IOException ioe) {
            throw new ServerInitializationException(
                    MessageFormat.format(CONSTANTS.getString(
                    "init.config.fatal.unreadable"), 
                    new Object[] {configFile, ioe.getMessage()}));
        } catch (IllegalArgumentException iae) {
            throw new ServerInitializationException(
                    MessageFormat.format(CONSTANTS.getString(
                    "init.config.fatal.unreadable"), 
                    new Object[] {configFile, iae.getMessage()}));
        } catch (SAXException saxe) {
            throw new ServerInitializationException(
                    MessageFormat.format(CONSTANTS.getString(
                    "init.config.fatal.malformedxml"), 
                    new Object[] {configFile, saxe.getMessage()}));
        }
    }
    
    public final File getFedoraHomeDir() {
        return m_fedoraHome;
    }
    
    /**
     * Gets the names of the roles that are required to be fulfilled by
     * modules specified in this server's configuration file.
     *
     * @returns String[] The roles.
     */
    public String[] getRequiredModuleRoles() {
        return new String[] {DOMANAGER_CLASSNAME};
    }
    
    public final Module getModule(String role) {
        return (Module) m_loadedModules.get(role);
    }
    
    public final Iterator loadedModuleRoles() {
        return m_loadedModules.keySet().iterator();
    }
    
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
        String fedoraHome=System.getProperty("fedora.home");
        if (fedoraHome==null) {
            System.err.println("Warning: fedora.home not set, using \".\"");
            fedoraHome=".";
        }
        try {
            Server server=Server.getInstance(new File(fedoraHome));
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
