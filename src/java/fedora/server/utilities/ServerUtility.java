package fedora.server.utilities;

import java.io.File;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import fedora.common.HttpClient;
import fedora.server.errors.GeneralException;

public class ServerUtility {
    public static final String FINE = "fine";
    public static final String FINER = "finer";
    public static final String FINEST = "finest";
    public static final String INFO = "info";
    public static final String WARNING = "warning";
    public static final String SEVERE = "severe";
    
    public static final String FEDORA_SERVER_HOST = "fedoraServerHost";
    public static final String FEDORA_SERVER_PORT = "fedoraServerPort";
    public static final String FEDORA_REDIRECT_PORT = "fedoraRedirectPort";
    public static final String FEDORA_SHUTDOWN_PORT = "fedoraShutdownPort";
    public static final String ADMIN_USER = "adminUser";
    public static final String ADMIN_PASSWORD = "adminPassword";
    private static final Properties readServerProperties(boolean httpRequired, boolean httpsRequired) 
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
    		if (FEDORA_SERVER_PORT.equals(nameNode.getNodeValue())
            ||  FEDORA_REDIRECT_PORT.equals(nameNode.getNodeValue())
            ||  FEDORA_SHUTDOWN_PORT.equals(nameNode.getNodeValue())			
            ||  FEDORA_SERVER_HOST.equals(nameNode.getNodeValue())
            ||  ADMIN_USER.equals(nameNode.getNodeValue())
            ||  ADMIN_PASSWORD.equals(nameNode.getNodeValue())) {
        		properties.put(nameNode.getNodeValue(),valueNode.getNodeValue());            			
    		}
        }
        if ((! properties.containsKey(FEDORA_SERVER_HOST))) {
        	throw new Exception("fedora.fcfg missing " + "http host");            	
        }                
        if (httpRequired && ! properties.containsKey(FEDORA_SERVER_PORT)) {
        	throw new Exception("fedora.fcfg missing " + "http port");        	
        }
        if (httpsRequired && ! properties.containsKey(FEDORA_REDIRECT_PORT)) {
        	throw new Exception("fedora.fcfg missing " + "http port");            	
        }
        if (! properties.containsKey(FEDORA_SHUTDOWN_PORT)) {
        	throw new Exception("fedora.fcfg missing " + "shutdown port");            	
        }        
        if ((! properties.containsKey(ADMIN_USER))) {
        	throw new Exception("fedora.fcfg missing " + "admin user");            	
        }
        if ((! properties.containsKey(ADMIN_PASSWORD))) {
        	throw new Exception("fedora.fcfg missing " + "admin passwd");            	
        }            
    	return properties;
    }

    public static final Properties getServerProperties() {
    	return serverProperties;
    }

    
    private static Properties serverProperties = null;
    static {
    	try {
			serverProperties = readServerProperties(false, false);
		} catch (Exception e) {
	        slog(FINEST, "problem during static read of server properties, "+e.getMessage());
		}
    }

    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    
    public static final String getPort(String protocol) throws GeneralException {
    	String port = null;
    	if (! HTTP.equals(protocol) && ! HTTPS.equals(protocol)) {
        	System.err.println("bad protocol parm = "+protocol);
    		throw new GeneralException("bad protocol parm");    	
    	}
    	System.err.println("serverProperties = "+serverProperties);
    	System.err.println("HTTP.equals(protocol) = "+HTTP.equals(protocol));    	
    	System.err.println("HTTPS.equals(protocol) = "+HTTPS.equals(protocol));    	
    	System.err.println("FEDORA_SERVER_PORT = "+FEDORA_SERVER_PORT);    	
    	System.err.println("FEDORA_REDIRECT_PORT = "+FEDORA_REDIRECT_PORT); 
    	System.err.println("serverProperties.containsKey(FEDORA_SERVER_PORT) = "+serverProperties.containsKey(FEDORA_SERVER_PORT));    	
    	System.err.println("serverProperties.containsKey(FEDORA_REDIRECT_PORT) = "+serverProperties.containsKey(FEDORA_REDIRECT_PORT));    	
    	if (HTTP.equals(protocol) && serverProperties.containsKey(FEDORA_SERVER_PORT)) {	
    		port = (String) serverProperties.get(FEDORA_SERVER_PORT);
    	} else if (HTTPS.equals(protocol) && serverProperties.containsKey(FEDORA_REDIRECT_PORT)) {	
    		port = (String) serverProperties.get(FEDORA_REDIRECT_PORT);
    	} else {
        	System.err.println("specified port not configured");
    		throw new GeneralException("specified port not configured");    		
    	}
    	System.err.println(protocol+"=>"+port);
    	return port;
    }
    
    public static final ProtocolPort getProtocolPort(String preferredProtocol, String fallbackProtocol) throws GeneralException {
    	String protocol = null;
    	String port = getPort(preferredProtocol);
    	if (port != null) {
    		protocol = preferredProtocol;
    	} else if (fallbackProtocol != null) {
    		port = getPort(fallbackProtocol);
        	if (port != null) {
        		protocol = fallbackProtocol;
        	}
    	}    	
    	System.err.println("protocol="+protocol+"port="+port);
    	return new ProtocolPort(protocol, port);
    }
    
    public static boolean pingServletContainer(String path, int secondsTimeout, int maxConnectionAttemptsPerUrl) throws GeneralException {
        boolean pingsOk = false;
        try {
        	slog(FINEST, "getServerProperties()="+getServerProperties());
        	ProtocolPort protocolPort = getProtocolPort(HTTP, HTTPS);
    		slog(FINEST, "protocolPort="+protocolPort);
    		slog(FINEST, "protocolPort.getProtocol()="+protocolPort.getProtocol());
    		slog(FINEST, "protocolPort.getPort()="+protocolPort.getPort());
    		slog(FINEST, "serverProperties.get(FEDORA_SERVER_HOST)="+serverProperties.get(FEDORA_SERVER_HOST));
    		slog(FINEST, "path="+path);    		
        	HttpClient client = new HttpClient(protocolPort.getProtocol(), (String) getServerProperties().get(FEDORA_SERVER_HOST), protocolPort.getPort(), path);
    		slog(FINEST, "client="+client);
        	GetMethod getMethod = client.doNoAuthnGet(1000 * secondsTimeout, 25, maxConnectionAttemptsPerUrl);
    		slog(FINEST, "getMethod="+getMethod);
    		slog(FINEST, "getMethod.getStatusCode()="+getMethod.getStatusCode());    		
        	pingsOk = (getMethod.getStatusCode() == java.net.HttpURLConnection.HTTP_OK);
    		slog(FINEST, "pingsOk="+pingsOk);
        } catch (Exception e) {			
        	throw new GeneralException(slog(FINEST, "op failure pinging fedora server"), e);
		}
        return pingsOk;    	
    }
    
	public static final int MAX_CONNECTION_ATTEMPTS_PER_URL = 15;   
    public static boolean pingServletContainerStartup(String path, int secondsTimeout) throws GeneralException {
    	return pingServletContainer(path, secondsTimeout, MAX_CONNECTION_ATTEMPTS_PER_URL);
    }
    
    public static boolean pingServletContainerRunning(String path, int secondsTimeout) throws GeneralException {
    	return pingServletContainer(path, secondsTimeout, 1);
    }

    
    public static final String slog(String level, String msg) {
		System.err.println(level + ": " + msg);
		return msg;
    }
    
}
