package fedora.server.utilities;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fedora.server.errors.ServerInitializationException;
import fedora.server.DatastoreConfig;
import fedora.server.Server;

/**
 * Utility class to load Fedora configuration files (e.g. fedora.fcfg).
 *
 * @author Edwin Shin
 */
public class ConfigurationLoader {
    
    private static String s_serverProfile=System.getProperty("fedora.serverProfile");
    
    private Map serverParams;
    private ArrayList mdInfo;
    private HashMap moduleParams;
    private HashMap moduleClassNames;
    private HashMap datastoreParams;
    private HashMap m_datastoreConfigs;
    
    public ConfigurationLoader(String homeDir, String configFilename) {
        try {
            serverParams = loadParameters(homeDir, configFilename);
        } catch (ServerInitializationException e) {
            e.printStackTrace();
        }
        mdInfo=(ArrayList) serverParams.get(null);
        moduleParams=(HashMap) mdInfo.get(0);
        moduleClassNames=(HashMap) mdInfo.get(1);
        datastoreParams=(HashMap) mdInfo.get(2);
        serverParams.remove(null);
        
        // create the datastore configs and set the instance variable
        // so they can be seen with getDatastoreConfig(...)
        Iterator dspi=datastoreParams.keySet().iterator();
        m_datastoreConfigs=new HashMap();
        while (dspi.hasNext()) {
            String id=(String) dspi.next();
            m_datastoreConfigs.put(id, new DatastoreConfig(
                    (HashMap) datastoreParams.get(id)));
        }
    }
    
    public Map getParameters() {
        return serverParams;
    }
    
    public Map getModuleParameters() {
        return moduleParams;
    }
    
    public Map getModuleParameters(String module) {
        return (Map)moduleParams.get(module);
    }
    
    public DatastoreConfig getDatastoreConfig(String datastoreId) {
        return (DatastoreConfig)m_datastoreConfigs.get(datastoreId);
    }

    public static HashMap loadParameters(String homeDir, String configFilename) throws ServerInitializationException {
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Element element;
        try {
            builder = factory.newDocumentBuilder();
        
            File configFile=new File(homeDir + File.separator + "server"
                    + File.separator + Server.CONFIG_DIR
                    + File.separator + configFilename);
            // suck it in
            element = builder.parse(configFile).getDocumentElement();
        
            String dAttribute = "";
            return loadParameters(element, dAttribute);
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new ServerInitializationException("");
        } catch (SAXException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            throw new ServerInitializationException("");
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            throw new ServerInitializationException("");
        }
    }

    public static final HashMap loadParameters(Element element, String dAttribute)
            throws ServerInitializationException {
        HashMap params=new HashMap();
        if (element.getLocalName().equals(Server.CONFIG_ELEMENT_ROOT)) {
            ArrayList moduleAndDatastreamInfo=new ArrayList(3);
            moduleAndDatastreamInfo.add(new HashMap());
            moduleAndDatastreamInfo.add(new HashMap());
            moduleAndDatastreamInfo.add(new HashMap());
            params.put(null, moduleAndDatastreamInfo);
        }

        for (int i=0; i<element.getChildNodes().getLength(); i++) {
            Node n=element.getChildNodes().item(i);
            if (n.getNodeType()==Node.ELEMENT_NODE) {
                if (n.getLocalName().equals(Server.CONFIG_ELEMENT_PARAM)) {
                    // if name-value pair, save in the HashMap
                    NamedNodeMap attrs=n.getAttributes();
                    Node nameNode=attrs.getNamedItemNS(Server.CONFIG_NAMESPACE,
                            Server.CONFIG_ATTRIBUTE_NAME);
                    if (nameNode==null) {
                        nameNode=attrs.getNamedItem(Server.CONFIG_ATTRIBUTE_NAME);
                    }
                    Node valueNode=null;
                    if (s_serverProfile!=null) {
                        valueNode=attrs.getNamedItemNS(Server.CONFIG_NAMESPACE,
                            s_serverProfile + "value");
                        if (valueNode==null) {
                            valueNode=attrs.getNamedItem(s_serverProfile + "value");
                        }
                    }
                    if (valueNode==null) {
                        valueNode=attrs.getNamedItemNS(Server.CONFIG_NAMESPACE,
                                Server.CONFIG_ATTRIBUTE_VALUE);
                        if (valueNode==null) {
                            valueNode=attrs.getNamedItem(Server.CONFIG_ATTRIBUTE_VALUE);
                        }
                        if (nameNode==null || valueNode==null) {
                            throw new ServerInitializationException(
                                    Server.INIT_CONFIG_SEVERE_INCOMPLETEPARAM);
                        }
                    }
                    if (nameNode.getNodeValue().equals("") ||
                            valueNode.getNodeValue().equals("")) {
                        throw new ServerInitializationException(
                                MessageFormat.format(
                                Server.INIT_CONFIG_SEVERE_INCOMPLETEPARAM, new Object[]
                                {Server.CONFIG_ELEMENT_PARAM, Server.CONFIG_ATTRIBUTE_NAME,
                                Server.CONFIG_ATTRIBUTE_VALUE}));
                    }
                    if (params.get(nameNode.getNodeValue())!=null) {
                        throw new ServerInitializationException(
                                MessageFormat.format(
                                Server.INIT_CONFIG_SEVERE_REASSIGNMENT, new Object[]
                                {Server.CONFIG_ELEMENT_PARAM, Server.CONFIG_ATTRIBUTE_NAME,
                                nameNode.getNodeValue()}));
                    }
                    params.put(nameNode.getNodeValue(),
                            valueNode.getNodeValue());
                } else if (!n.getLocalName().equals(Server.CONFIG_ELEMENT_COMMENT)) {
                    if (element.getLocalName().equals(Server.CONFIG_ELEMENT_ROOT)) {
                        if (n.getLocalName().equals(Server.CONFIG_ELEMENT_MODULE)) {
                            NamedNodeMap attrs=n.getAttributes();
                            Node roleNode=attrs.getNamedItemNS(Server.CONFIG_NAMESPACE,
                                    Server.CONFIG_ATTRIBUTE_ROLE);
                            if (roleNode==null) {
                                roleNode=attrs.getNamedItem(
                                        Server.CONFIG_ATTRIBUTE_ROLE);
                                if (roleNode==null) {
                                    throw new ServerInitializationException(
                                            Server.INIT_CONFIG_SEVERE_NOROLEGIVEN);
                                }
                            }
                            String moduleRole=roleNode.getNodeValue();
                            if (moduleRole.equals("")) {
                                throw new ServerInitializationException(
                                        Server.INIT_CONFIG_SEVERE_NOROLEGIVEN);
                            }
                            HashMap moduleImplHash=(HashMap) ((ArrayList)
                                    params.get(null)).get(1);
                            if (moduleImplHash.get(moduleRole)!=null) {
                                throw new ServerInitializationException(
                                        MessageFormat.format(
                                        Server.INIT_CONFIG_SEVERE_REASSIGNMENT,
                                        new Object[] {Server.CONFIG_ELEMENT_MODULE,
                                        Server.CONFIG_ATTRIBUTE_ROLE, moduleRole}));
                            }
                            Node classNode=attrs.getNamedItemNS(
                                    Server.CONFIG_NAMESPACE, Server.CONFIG_ATTRIBUTE_CLASS);
                            if (classNode==null) {
                                classNode=attrs.getNamedItem(
                                        Server.CONFIG_ATTRIBUTE_CLASS);
                                if (classNode==null) {
                                    throw new ServerInitializationException(
                                            Server.INIT_CONFIG_SEVERE_NOCLASSGIVEN);
                                }
                            }
                            String moduleClass=classNode.getNodeValue();
                            if (moduleClass.equals("")) {
                                throw new ServerInitializationException(
                                        Server.INIT_CONFIG_SEVERE_NOCLASSGIVEN);
                            }
                            moduleImplHash.put(moduleRole, moduleClass);
                            ((HashMap) ((ArrayList)
                                    params.get(null)).get(0)).put(moduleRole,
                                    loadParameters((Element) n,
                                    Server.CONFIG_ATTRIBUTE_ROLE + "=\"" + moduleRole
                                    + "\""));
                        } else if (n.getLocalName().equals(
                                Server.CONFIG_ELEMENT_DATASTORE)) {
                            NamedNodeMap attrs=n.getAttributes();
                            Node idNode=attrs.getNamedItemNS(Server.CONFIG_NAMESPACE,
                                    Server.CONFIG_ATTRIBUTE_ID);
                            if (idNode==null) {
                                idNode=attrs.getNamedItem(Server.CONFIG_ATTRIBUTE_ID);
                                if (idNode==null) {
                                    throw new ServerInitializationException(
                                            Server.INIT_CONFIG_SEVERE_NOIDGIVEN);
                                }
                            }
                            String dConfigId=idNode.getNodeValue();
                            if (dConfigId.equals("")) {
                                throw new ServerInitializationException(
                                        Server.INIT_CONFIG_SEVERE_NOIDGIVEN);
                            }
                            HashMap dParamHash=(HashMap) ((ArrayList)
                                    params.get(null)).get(2);
                            if (dParamHash.get(dConfigId)!=null) {
                                throw new ServerInitializationException(
                                    MessageFormat.format(
                                    Server.INIT_CONFIG_SEVERE_REASSIGNMENT,
                                    new Object[] {Server.CONFIG_ELEMENT_DATASTORE,
                                    Server.CONFIG_ATTRIBUTE_ID, dConfigId}));
                            }
                            dParamHash.put(dConfigId,loadParameters((Element) n,
                                    Server.CONFIG_ATTRIBUTE_ID + "=\"" + dConfigId
                                    + "\""));
                        } else {
                            throw new ServerInitializationException(
                                MessageFormat.format(
                                Server.INIT_CONFIG_SEVERE_BADELEMENT, new Object[]
                                {n.getLocalName()}));
                        }
                    }
                }

            } // else { // ignore non-Element nodes }
        }
        return params;
    }
}
