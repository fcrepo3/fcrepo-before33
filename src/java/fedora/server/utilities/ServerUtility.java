package fedora.server.utilities;

import java.io.File;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class ServerUtility {

    public static final String FEDORA_SERVER_HOST = "fedoraServerHost";
    public static final String FEDORA_SERVER_PORT = "fedoraServerPort";
    public static final String FEDORA_REDIRECT_PORT = "fedoraRedirectPort";
    public static final String ADMIN_USER = "adminUser";
    public static final String ADMIN_PASSWORD = "adminPassword";
    public static final Properties getServerProperties(String protocol) 
    	throws Exception {
       	Properties properties = new Properties();    	
        String fedoraHome=System.getProperty("fedora.home");
        if (fedoraHome==null) {
        	throw new Exception("ERROR: fedora.home system property not set.");            	
        }        	
        File fedoraHomeDir=new File(fedoraHome);
        File fcfgFile=new File(fedoraHomeDir, "server/config/fedora.fcfg");
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder=factory.newDocumentBuilder();
        Element rootElement=builder.parse(fcfgFile).getDocumentElement();
        NodeList params=rootElement.getElementsByTagName("param");
        for (int i=0; i<params.getLength(); i++) {
            Node nameNode=params.item(i).getAttributes().getNamedItem("name");
            Node valueNode=params.item(i).getAttributes().getNamedItem("value");
    		if (("http".equals(protocol) && FEDORA_SERVER_PORT.equals(nameNode.getNodeValue()))
            ||  ("https".equals(protocol) && FEDORA_REDIRECT_PORT.equals(nameNode.getNodeValue()))
            ||  FEDORA_SERVER_HOST.equals(nameNode.getNodeValue())
            ||  ADMIN_USER.equals(nameNode.getNodeValue())
            ||  ADMIN_PASSWORD.equals(nameNode.getNodeValue())) {
        		properties.put(nameNode.getNodeValue(),valueNode.getNodeValue());            			
    		}
        }
        if ((! properties.containsKey(FEDORA_SERVER_HOST))) {
        	throw new Exception("fedora.fcfg missing " + "http host");            	
        }                
        if ((! properties.containsKey(FEDORA_SERVER_PORT)) 
        &&  (! properties.containsKey(FEDORA_REDIRECT_PORT))) {
        	throw new Exception("fedora.fcfg missing " + "http port");            	
        }
        if ((! properties.containsKey(ADMIN_USER))) {
        	throw new Exception("fedora.fcfg missing " + "admin user");            	
        }
        if ((! properties.containsKey(ADMIN_PASSWORD))) {
        	throw new Exception("fedora.fcfg missing " + "admin passwd");            	
        }            
    	return properties;
    }

}
