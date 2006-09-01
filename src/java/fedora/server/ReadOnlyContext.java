/*
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Educational Community License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.opensource.org/licenses/ecl1.txt">
 * http://www.opensource.org/licenses/ecl1.txt.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2006 by 
 * The Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */

package fedora.server;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.catalina.realm.GenericPrincipal;

import fedora.common.Constants;
import fedora.server.utilities.DateUtility;

/**
 *
 * <p><b>Title:</b> ReadOnlyContext.java</p>
 * <p><b>Description:</b> Context that is read-only.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class ReadOnlyContext implements Context {
	
    public static ReadOnlyContext EMPTY=new ReadOnlyContext(null, null, "", true);
    static {
    	EMPTY.setActionAttributes(null);
    	EMPTY.setResourceAttributes(null);
    }
    
    private final Date now = new Date();
    
    private MultiValueMap m_environmentAttributes;
    public final MultiValueMap getEnvironmentAttributes() {
    	return m_environmentAttributes;
    }

    private MultiValueMap m_subjectAttributes;
    
    private MultiValueMap m_actionAttributes;
    
    private MultiValueMap m_resourceAttributes;
    
    private String password;
    
    private static final String NOOP_PARAMETER_NAME = "noOp";
    public static final boolean NO_OP = true;
    public static final boolean DO_OP = false;
    
    public static final String BACKEND_SERVICE = "backendService";

    private boolean noOp = false; 

    /**
     * Creates and initializes the <code>Context</code>.
     *
     * @param parameters A pre-loaded Map of name-value pairs
     *        comprising the context.
     */
    private ReadOnlyContext(MultiValueMap environmentAttributes, MultiValueMap subjectAttributes, String password, boolean noOp) {
        //super(parameters);
        m_environmentAttributes=environmentAttributes;
        if (m_environmentAttributes==null) {
            m_environmentAttributes=new MultiValueMap();
        }
        m_environmentAttributes.lock();
        m_subjectAttributes=subjectAttributes;
        if (m_subjectAttributes==null) {
            m_subjectAttributes=new MultiValueMap();
        }
        m_subjectAttributes.lock(); 
        m_actionAttributes=new MultiValueMap();
        m_actionAttributes.lock();
        m_resourceAttributes=new MultiValueMap();
        m_resourceAttributes.lock();
        if (password == null) {
        	password = "";
        }
        this.password = password;
        this.noOp = noOp;
    }
 
    public Iterator environmentAttributes() {
        return m_environmentAttributes.names();
    }

    public int nEnvironmentValues(String name) {
        return m_environmentAttributes.length(name);
    }
    
    public String getEnvironmentValue(String name) {
        return m_environmentAttributes.getString(name);
    }
    
    public String[] getEnvironmentValues(String name) {
        return m_environmentAttributes.getStringArray(name);
    }

    public Iterator subjectAttributes() {
        return m_subjectAttributes.names();
    }

    public int nSubjectValues(String name) {
        return m_subjectAttributes.length(name);
    }
    
    public String getSubjectValue(String name) {
        return m_subjectAttributes.getString(name);
    }
    
    public String[] getSubjectValues(String name) {
        return m_subjectAttributes.getStringArray(name);
    }
    
    public void setActionAttributes(MultiValueMap actionAttributes) {
        m_actionAttributes = actionAttributes;
        if (m_actionAttributes == null) {
            m_actionAttributes = new MultiValueMap();
        }
        m_actionAttributes.lock();    	
    }

    public Iterator actionAttributes() {
        return m_actionAttributes.names();
    }

    public int nActionValues(String name) {
        return m_actionAttributes.length(name);
    }
    
    public String getActionValue(String name) {
        return m_actionAttributes.getString(name);
    }
    
    public String[] getActionValues(String name) {
        return m_actionAttributes.getStringArray(name);
    }

    public Iterator resourceAttributes() {
        return m_resourceAttributes.names();
    }

    public void setResourceAttributes(MultiValueMap resourceAttributes) {
        m_resourceAttributes = resourceAttributes;
        if (m_resourceAttributes == null) {
            m_resourceAttributes = new MultiValueMap();
        }
        m_resourceAttributes.lock();    	
    }
    
    public int nResourceValues(String name) {
        return m_resourceAttributes.length(name);
    }
    
    public String getResourceValue(String name) {
        return m_resourceAttributes.getString(name);
    }
    
    public String[] getResourceValues(String name) {
        return m_resourceAttributes.getStringArray(name);
    }
    
    public String toString() {
    	StringBuffer buffer = new StringBuffer();
    	buffer.append("READ-ONLY CONTEXT:\n");
    	buffer.append(m_environmentAttributes);
    	buffer.append(m_subjectAttributes);
    	buffer.append(m_actionAttributes);
    	buffer.append(m_resourceAttributes);
    	buffer.append("(END-READ ONLY CONTEXT)\n");
    	return buffer.toString();
    }
    
    
    public Date now() {
    	return now;
    }
    
    private static final MultiValueMap beginEnvironmentMap(String messageProtocol) throws Exception {
    	MultiValueMap environmentMap = new MultiValueMap();
  	    environmentMap.set(Constants.HTTP_REQUEST.MESSAGE_PROTOCOL.uri, messageProtocol);
		Date now = new Date();
		environmentMap.set(Constants.ENVIRONMENT.CURRENT_DATE_TIME.uri, DateUtility.convertDateToString(now));
		environmentMap.set(Constants.ENVIRONMENT.CURRENT_DATE.uri, DateUtility.convertDateToDateString(now));
		environmentMap.set(Constants.ENVIRONMENT.CURRENT_TIME.uri, DateUtility.convertDateToTimeString(now));
		return environmentMap;
    }

    //will need fixup for noOp 
    public static Context getSoapContext() {
        HttpServletRequest req=(HttpServletRequest) MessageContext.
                getCurrentContext().getProperty(
                HTTPConstants.MC_HTTP_SERVLETREQUEST);
      return ReadOnlyContext.getContext(Constants.HTTP_REQUEST.SOAP.uri, req);
    }
    
    /* i don't see any references.  needed? let's see . . .  
    public static final ReadOnlyContext getContext(Context existingContext, String subjectId, String password, String[] roles) {
  		return getContext(existingContext.getEnvironmentAttributes(), subjectId, password, roles, existingContext.getNoOp());
    }
    */
    
    private static final ReadOnlyContext getContext(MultiValueMap environmentMap, String subjectId, String password, String[] roles, Map auxSubjectRoles, boolean noOp) {
    	MultiValueMap subjectMap = new MultiValueMap(); 
      	try {		
      		subjectMap.set(Constants.SUBJECT.LOGIN_ID.uri, (subjectId == null) ? "" : subjectId);
      		for (int i = 0; (roles != null) && (i < roles.length); i++) {
      			String[] parts = parseRole(roles[i]);
     			if ((parts != null) && parts.length == 2) {
    				subjectMap.set(parts[0],parts[1]); //todo:  handle multiple values (ldap)
     			}
      		}
      		Iterator auxSubjectRoleKeys = auxSubjectRoles.keySet().iterator();
      		while (auxSubjectRoleKeys.hasNext()) {
      			Object name = (String) auxSubjectRoleKeys.next();
      			if (name instanceof String) {
          			Object value = auxSubjectRoles.get(name);
          			if ((value instanceof String) || (value instanceof String[])) {
        				subjectMap.set((String) name, value);
          			}      				
      			}
      		}
      	} catch (Exception e) {	
      		slog("caught exception building subjectMap " + e.getMessage());
      		if (e.getCause() != null) {
      			slog(e.getCause().getMessage());
      		}
      	} finally {
      		subjectMap.lock();
      	}
      	return new ReadOnlyContext(environmentMap, subjectMap, (password == null) ? "" : password, noOp);
    }

    // needed for, e.g., rebuild
    public static final ReadOnlyContext getContext(String messageProtocol, String subjectId, String password, String[] roles, boolean noOp) throws Exception {
    	MultiValueMap environmentMap = beginEnvironmentMap(messageProtocol);
  		environmentMap.lock(); // no request to grok more from 
  		return getContext(environmentMap, subjectId, password, roles, null, noOp);
    }

    public static final ReadOnlyContext getContext(String messageProtocol, HttpServletRequest request, String[] overrideRoles) {
		MultiValueMap environmentMap = null;
	  	try {
	  		environmentMap = beginEnvironmentMap(messageProtocol);  			
	  		
	  		environmentMap.set(Constants.HTTP_REQUEST.SECURITY.uri, 
	  				(request.isSecure()) ? Constants.HTTP_REQUEST.SECURE.uri : Constants.HTTP_REQUEST.INSECURE.uri);
	  	    environmentMap.set(Constants.HTTP_REQUEST.SESSION_STATUS.uri, 
	  	    		request.isRequestedSessionIdValid() ? "valid" : "invalid"   );

	  	    String sessionEncoding = null;
	  	    if (request.isRequestedSessionIdFromCookie()) {
	  	    	sessionEncoding =  "cookie";    	
	  	    } else if (request.isRequestedSessionIdFromURL()) {
	  	    	sessionEncoding =  "url";    	
	  	    }

	  	    if (request.getContentLength() > -1) {
	  	    	environmentMap.set(Constants.HTTP_REQUEST.CONTENT_LENGTH.uri, "" + request.getContentLength());    	
	  	    }
	  	    if (request.getLocalPort() > -1) {  			
	  	    	environmentMap.set(Constants.HTTP_REQUEST.SERVER_PORT.uri, "" + request.getLocalPort());
	  	    }

	  	    if (request.getProtocol() != null) {  			
	  	    	environmentMap.set(Constants.HTTP_REQUEST.PROTOCOL.uri, request.getProtocol());
	  	    }
	  	    if (request.getScheme() != null) {  			
	  	    	environmentMap.set(Constants.HTTP_REQUEST.SCHEME.uri, request.getScheme());
	  	    }
	  	    if (request.getAuthType() != null) {
	  	    	environmentMap.set(Constants.HTTP_REQUEST.AUTHTYPE.uri, request.getAuthType());  
	  	    }
	  	    if (request.getMethod() != null) {  			
	  	    	environmentMap.set(Constants.HTTP_REQUEST.METHOD.uri, request.getMethod());	
	  	    }
	  	    if (sessionEncoding != null) {
	  	    	environmentMap.set(Constants.HTTP_REQUEST.SESSION_ENCODING.uri, sessionEncoding);    		  	    	
	  	    }
	  	    if (request.getContentType() != null) {
	  	    	environmentMap.set(Constants.HTTP_REQUEST.CONTENT_TYPE.uri, request.getContentType());
	  	    }
	  	    if (request.getLocalAddr() != null) {  			
	  	    	environmentMap.set(Constants.HTTP_REQUEST.SERVER_IP_ADDRESS.uri, request.getLocalAddr());
	  	    }
	  	    if (request.getRemoteAddr() != null) {  			
	  	    	environmentMap.set(Constants.HTTP_REQUEST.CLIENT_IP_ADDRESS.uri, request.getRemoteAddr());
	  	    }

	  	    if (request.getRemoteHost() != null) {  			
	  	    	if (! request.getRemoteHost().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {	    
	  	    		environmentMap.set(Constants.HTTP_REQUEST.CLIENT_FQDN.uri, request.getRemoteHost().toLowerCase());        
	  	    	}
	  	    }
	  	    if (request.getLocalName() != null) {  			
	  	    	if (! request.getLocalName().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
	  	    		environmentMap.set(Constants.HTTP_REQUEST.SERVER_FQDN.uri, request.getLocalName().toLowerCase());
	  	    	}
	  	    }
	  	} catch (Exception e) {
	  	} finally {
	  		environmentMap.lock();
	  	}
	  	
  	  	String subjectId = request.getRemoteUser();
  	  	String password = null;
  	  	if (request.getUserPrincipal() == null) {
  	  		slog("in context, no principal to grok password from!!");				
  	  	} else {
			if (! (request.getUserPrincipal() instanceof GenericPrincipal)) {
				slog("in context, principal is -not- GenericPrincipal, so I'm not groking password from it!!");
			} else {
				slog("in context, principal is GenericPrincipal, so I can grok password from it!!");
  	  	  	  	if (((GenericPrincipal) request.getUserPrincipal()).getPassword() != null ) {
  	  	  	  		if (password == null) {
  	  	  	  			password = ((GenericPrincipal) request.getUserPrincipal()).getPassword();
  	  	  	  		}
  	  	  	  		if (password == null) {
  	  	  	  			password = "";
  	  	  	  		}
  	  	  	  	}
			}
  	  	}
  	  	if (subjectId == null) {
  	  		subjectId = "";
  	  	}
  	  	if (password == null) {
  	  		password = "";
  	  	}
  	  	if (overrideRoles == null) {
  	  		overrideRoles = new String[0];
  	  	}
  	  	
  	boolean noOp = true; //safest approach 
  	try {
  		noOp = (new Boolean(request.getParameter(NOOP_PARAMETER_NAME))).booleanValue();
  		slog("NOOP_PARAMETER_NAME="+NOOP_PARAMETER_NAME);
  		slog("request.getParameter(NOOP_PARAMETER_NAME)="+ request.getParameter(NOOP_PARAMETER_NAME));
  		slog("noOp="+ noOp);
 
  	} catch (Exception e) {
  	}
  	Map auxSubjectRoles = null;
  	Object testFedoraAuxSubjectAttributes = request.getAttribute(FEDORA_AUX_SUBJECT_ATTRIBUTES);
  	if ((testFedoraAuxSubjectAttributes != null) && (testFedoraAuxSubjectAttributes instanceof Map)) {
  		auxSubjectRoles = (Map) testFedoraAuxSubjectAttributes;
  	}
  	return getContext(environmentMap, subjectId, password, overrideRoles, auxSubjectRoles, noOp);
    }

    /*
     * Gets a Context appropriate for the request, and whether it is ok
     * to use the dissemination cache or not.
     */
    //form context from optional servlet request, overriding request for added parms    
    public static final ReadOnlyContext getContext(String messageProtocol, HttpServletRequest request) {
  	  	String[] roles = null;
  	  	if (request.getUserPrincipal() == null) {
  	  		slog("in context, no principal to grok roles from!!");				
  	  	} else {
			if (! (request.getUserPrincipal() instanceof GenericPrincipal)) {
				slog("in context, principal is -not- GenericPrincipal, so I'm not groking roles from it!!");
			} else {
				slog("in context, principal is GenericPrincipal, so I can grok roles from it!!");
  	  	  		roles = ((GenericPrincipal) request.getUserPrincipal()).getRoles();
			}
  	  	}
  	  	if (roles == null) {
  	  		roles = new String[0];
  	  	}
  	return getContext(messageProtocol, request, roles);
    }
    
  	public static final String[] parseRole (String role) {
  		String[] parts = null;
  		if ((role == null) || (role.length() == 0)) {
  		} else {
  			int i = role.indexOf('=');
  			if (i == 0) {
  			} else {
  				parts = new String[2];	
  				if (i < 0) {
  					parts[0] = role;
  					parts[1] = ""; //Boolean.toString(true);
  				} else {
  					parts[0] = role.substring(0,i);
  					if (i == (role.length()-1)) {
  						parts[1] = ""; //Boolean.toString(true);
  					} else {
  						parts[1] = role.substring(i+1);
  					}
  				}
  			}
  		}
  		return parts; 
  	}
  	
    public String getPassword() {
    	return password;
    }
    
    public boolean getNoOp() {
    	return noOp;
    }
  	
	public static boolean log = false; 
	
	protected final void log(String msg) {
		if (! log) return;
		System.err.println(msg);
	}
	
	public static boolean slog = false; 
	
	protected static final void slog(String msg) {
		if (! slog) return;
		System.err.println(msg);
	}
    
}