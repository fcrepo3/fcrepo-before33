package fedora.common.policy;

import com.sun.xacml.attr.IntegerAttribute;
import com.sun.xacml.attr.StringAttribute;

public class HttpRequestNamespace extends XacmlNamespace {
	
	// Properties
	public final XacmlName MESSAGE_PROTOCOL;
	
	public final XacmlName PROTOCOL;
	public final XacmlName SCHEME;
	public final XacmlName SECURITY;
	public final XacmlName AUTHTYPE;
	public final XacmlName METHOD;	
	public final XacmlName SESSION_ENCODING;	
	public final XacmlName SESSION_STATUS;		
	public final XacmlName CONTENT_LENGTH;
	public final XacmlName CONTENT_TYPE;
	public final XacmlName CLIENT_FQDN;
	public final XacmlName CLIENT_IP_ADDRESS;	
	public final XacmlName SERVER_FQDN;
	public final XacmlName SERVER_IP_ADDRESS;	
	public final XacmlName SERVER_PORT;			

	// Values of MESSAGE_PROTOCOL	 
	public final XacmlName SOAP;
	public final XacmlName REST;
	
	// Values of SECURITY	 
	public final XacmlName SECURE;
	public final XacmlName INSECURE;
	
    private HttpRequestNamespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);

    	// Properties
    	MESSAGE_PROTOCOL = addName(new XacmlName(this, "message-protocol"));
    	
    	PROTOCOL = addName(new XacmlName(this, "protocol", StringAttribute.identifier));
    	SCHEME = addName(new XacmlName(this, "scheme", StringAttribute.identifier));
    	SECURITY = addName(new XacmlName(this, "security", StringAttribute.identifier));
    	AUTHTYPE = addName(new XacmlName(this, "authtype", StringAttribute.identifier));
    	METHOD = addName(new XacmlName(this, "method", StringAttribute.identifier));	
    	SESSION_ENCODING = addName(new XacmlName(this, "session-encoding", StringAttribute.identifier));	
    	SESSION_STATUS = addName(new XacmlName(this, "session-status", StringAttribute.identifier));		
    	CONTENT_LENGTH = addName(new XacmlName(this, "content-length", IntegerAttribute.identifier));
    	CONTENT_TYPE = addName(new XacmlName(this, "content-type", StringAttribute.identifier));
    	CLIENT_FQDN = addName(new XacmlName(this, "client-fqdn", StringAttribute.identifier));
    	CLIENT_IP_ADDRESS = addName(new XacmlName(this, "client-ip-address", StringAttribute.identifier));	
    	SERVER_FQDN = addName(new XacmlName(this, "server-fqdn", StringAttribute.identifier));
    	SERVER_IP_ADDRESS = addName(new XacmlName(this, "server-ip-address", StringAttribute.identifier));
    	//urn:oasis:names:tc:xacml:1.0:data-type:ipAddress
    	SERVER_PORT = addName(new XacmlName(this, "server-port", StringAttribute.identifier));

    	// Values of MESSAGE_PROTOCOL	 
    	SOAP               = addName(new XacmlName(this, "soap"));
    	REST               = addName(new XacmlName(this, "rest"));    	
    	
    	// Values of SECURITY	 
    	SECURE               = addName(new XacmlName(this, "secure"));
    	INSECURE               = addName(new XacmlName(this, "insecure"));     	
    }

	public static HttpRequestNamespace onlyInstance = new HttpRequestNamespace(EnvironmentNamespace.getInstance(), "http-request");
	
	public static final HttpRequestNamespace getInstance() {
		return onlyInstance;
	}


}
