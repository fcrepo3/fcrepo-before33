package fedora.common.policy;

import fedora.common.Constants;

/**
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 */
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
	
    private HttpRequestNamespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);

    	// Properties
    	MESSAGE_PROTOCOL = addName(new XacmlName(this, "message-protocol"));
    	
    	PROTOCOL = addName(new XacmlName(this, "protocol"));
    	SCHEME = addName(new XacmlName(this, "scheme"));
    	SECURITY = addName(new XacmlName(this, "security"));
    	AUTHTYPE = addName(new XacmlName(this, "authtype"));
    	METHOD = addName(new XacmlName(this, "method"));	
    	SESSION_ENCODING = addName(new XacmlName(this, "session-encoding"));	
    	SESSION_STATUS = addName(new XacmlName(this, "session-status"));		
    	CONTENT_LENGTH = addName(new XacmlName(this, "content-length"));
    	CONTENT_TYPE = addName(new XacmlName(this, "content-type"));
    	CLIENT_FQDN = addName(new XacmlName(this, "client-fqdn"));
    	CLIENT_IP_ADDRESS = addName(new XacmlName(this, "client-ip-address"));	
    	SERVER_FQDN = addName(new XacmlName(this, "server-fqdn"));
    	SERVER_IP_ADDRESS = addName(new XacmlName(this, "server-ip-address"));	
    	SERVER_PORT = addName(new XacmlName(this, "server-port"));

    	// Values of MESSAGE_PROTOCOL	 
    	SOAP               = addName(new XacmlName(this, "soap"));
    	REST               = addName(new XacmlName(this, "rest"));    	
    }

	public static HttpRequestNamespace onlyInstance = new HttpRequestNamespace(EnvironmentNamespace.getInstance(), "http-request");
	
	public static final HttpRequestNamespace getInstance() {
		return onlyInstance;
	}


}
