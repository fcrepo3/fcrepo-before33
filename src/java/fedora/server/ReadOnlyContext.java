package fedora.server;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.realm.GenericPrincipal;

import fedora.common.Constants;
import fedora.server.security.Authorization;

/**
 *
 * <p><b>Title:</b> ReadOnlyContext.java</p>
 * <p><b>Description:</b> Context that is read-only.</p>
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
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class ReadOnlyContext
        extends Parameterized implements Context {

	public static final boolean USE_CACHED_OBJECT = true;

	public static final boolean DO_NOT_USE_CACHED_OBJECT = false;

	
    public static ReadOnlyContext EMPTY=new ReadOnlyContext(null, null, null, "");
    static {
    	EMPTY.setActionAttributes(null);
    	EMPTY.setResourceAttributes(null);
    }
    
    private final Date now = new Date();
    
    private MultiValueMap m_environmentAttributes;

    private MultiValueMap m_subjectAttributes;
    
    private MultiValueMap m_actionAttributes;
    
    private MultiValueMap m_resourceAttributes;
    
    private String password;

    /**
     * Creates and initializes the <code>Context</code>.
     *
     * @param parameters A pre-loaded Map of name-value pairs
     *        comprising the context.
     */
    private ReadOnlyContext(Map parameters, MultiValueMap environmentAttributes, MultiValueMap subjectAttributes, String password) {
        super(parameters);
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
        this.password = password;
    }
    
    private ReadOnlyContext(Map parameters) {
        this(parameters, null, null, null);
    }

    public static ReadOnlyContext getCopy(Context source) {
        HashMap params=new HashMap();
        Iterator iter;
        iter=source.names();
        while (iter.hasNext()) {
            String k=(String) iter.next();
            params.put(k, source.get(k));
        }
        //vvvvv this fixup to allow compilation; needs extension for new fields vvvvv
        ReadOnlyContext temp = new ReadOnlyContext(params, null, null, source.getPassword());
        temp.setActionAttributes(null);
        temp.setResourceAttributes(null);
        return temp;
        //^^^^^ this fixup to allow compilation; needs extension for new fields ^^^^^
    }

    public static ReadOnlyContext getUnion(Context a, Context b) {
        if (a==null) {
            if (b==null) {
                return EMPTY;
            } else {
                if (b.getClass().getName().equals("fedora.server.ReadOnlyContext")) {
                    return (ReadOnlyContext) b;
                } else {
                    return getCopy(b);
                }
            }
        } else {
            if (b==null) {
                if (a.getClass().getName().equals("fedora.server.ReadOnlyContext")) {
                    return (ReadOnlyContext) a;
                } else {
                    return getCopy(a);
                }
            } else {
                // read from a, then b, then create and return new ReadOnlyContext
                HashMap params=new HashMap();
                Iterator iter;
                iter=a.names();
                while (iter.hasNext()) {
                    String k=(String) iter.next();
                    params.put(k, a.get(k));
                }
                iter=b.names();
                while (iter.hasNext()) {
                    String k=(String) iter.next();
                    params.put(k, b.get(k));
                }
                //vvvvv this fixup to allow compilation; needs extension for new fields vvvvv
                ReadOnlyContext temp = new ReadOnlyContext(params, null, null, a.getPassword());
                temp.setActionAttributes(null);
                temp.setResourceAttributes(null);
                return temp;
                //^^^^^ this fixup to allow compilation; needs extension for new fields ^^^^^
            }
        }
    }

    public String get(String name) {
        return getParameter(name);
    }

    public Iterator names() {
        return parameterNames();
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
    
    public static final ReadOnlyContext getContext(Map parameters) {
    	if (parameters == null) {
    		parameters = new Hashtable();
    	}
        return new ReadOnlyContext(parameters);
    }
    
    /*
     * Gets a Context appropriate for the request, and whether it is ok
     * to use the dissemination cache or not.
     */
    public static final ReadOnlyContext getContext(String soapOrRest, HttpServletRequest request, boolean useCachedObject) {
      
  	MultiValueMap environmentMap = new MultiValueMap();
  	//h.put(Authorization.ENVIRONMENT_CURRENT_DATETIME_URI_STRING, "2005-01-26T16:42:00Z");  //does xacml engine provide this?
  	try {
  		environmentMap.set(Constants.POLICY_ENVIRONMENT.REQUEST_PROTOCOL.uri, request.getProtocol());
  		environmentMap.set(Constants.POLICY_ENVIRONMENT.REQUEST_SCHEME.uri, request.getScheme());
  		environmentMap.set(Constants.POLICY_ENVIRONMENT.REQUEST_SECURITY.uri, (request.isSecure()) ? "secure" : "insecure");
  	
  	    if (request.getAuthType() != null) {
  	    	environmentMap.set(Constants.POLICY_ENVIRONMENT.REQUEST_AUTHTYPE.uri, request.getAuthType());  
  	    }
  	
  	    environmentMap.set(Constants.POLICY_ENVIRONMENT.REQUEST_METHOD.uri, request.getMethod());	
  	    if (request.isRequestedSessionIdFromCookie()) {
  	    	environmentMap.set(Constants.POLICY_ENVIRONMENT.REQUEST_SESSION_ENCODING.uri, "cookie");    	
  	    } else if (request.isRequestedSessionIdFromURL()) {
  	    	environmentMap.set(Constants.POLICY_ENVIRONMENT.REQUEST_SESSION_ENCODING.uri, "url");    	
  	    }
  	    environmentMap.set(Constants.POLICY_ENVIRONMENT.REQUEST_SESSION_STATUS.uri, request.isRequestedSessionIdValid() ? "valid" : "invalid"   );
  	    if (request.getContentLength() > -1) {
  	    	environmentMap.set(Constants.POLICY_ENVIRONMENT.REQUEST_CONTENT_LENGTH.uri, "" + request.getContentLength());    	
  	    }
  	    if (request.getContentType() != null) {
  	    	environmentMap.set(Constants.POLICY_ENVIRONMENT.REQUEST_CONTENT_TYPE.uri, request.getContentType());
  	    }
  	    environmentMap.set(Constants.POLICY_ENVIRONMENT.REQUEST_MESSAGE_PROTOCOL.uri, soapOrRest);
  	    if (! request.getRemoteHost().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {	    
  	    	environmentMap.set(Constants.POLICY_ENVIRONMENT.REQUEST_CLIENT_FQDN.uri, request.getRemoteHost().toLowerCase());        
  	    }
  	    environmentMap.set(Constants.POLICY_ENVIRONMENT.REQUEST_CLIENT_IP_ADDRESS.uri, request.getRemoteAddr());
  	    if (! request.getLocalName().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
  	    	environmentMap.set(Constants.POLICY_ENVIRONMENT.REQUEST_SERVER_FQDN.uri, request.getLocalName().toLowerCase());
  	    }
  	    environmentMap.set(Constants.POLICY_ENVIRONMENT.REQUEST_SERVER_IP_ADDRESS.uri, request.getLocalAddr());
  	    environmentMap.set(Constants.POLICY_ENVIRONMENT.REQUEST_SERVER_PORT.uri, "" + request.getLocalPort());
  	} catch (Exception e) {
  	} finally {
  		environmentMap.lock();
  	}

  	String subjectId = request.getRemoteUser();

  	//roles are available through xacml "attribute finder" callback and so are not stored here 
  	//as subject attrs
  	
  	MultiValueMap subjectMap = new MultiValueMap();
  	//authn might not have been required by web.xml
  	if (subjectId == null) {
  		subjectId = "";
  	}
  	String roles[] = null;
  	if ((request.getUserPrincipal() != null) && (request.getUserPrincipal() instanceof GenericPrincipal)) {		
  		roles = ((GenericPrincipal) request.getUserPrincipal()).getRoles();
  		log("request=" + request); 
  		log("request.getUserPrincipal()=" + request.getUserPrincipal());  		
  		log("((GenericPrincipal) request.getUserPrincipal())=" + ((GenericPrincipal) request.getUserPrincipal()));  		
  		log("((GenericPrincipal) request.getUserPrincipal()).getPassword()=" + ((GenericPrincipal) request.getUserPrincipal()).getPassword());  		
  	}
	String password = ((GenericPrincipal) request.getUserPrincipal()).getPassword();
  	try {		
  		log("1");
  		subjectMap.set(Authorization.SUBJECT_ID_URI_STRING, subjectId);
  		//subjectMap.set(Constants.POLICY_SUBJECT.PASSWORD.uri, password);	  		
  		log("2");
  		for (int i = 0; (roles != null) && (i < roles.length); i++) {
  			log("2a");
  			log("2b roles["+i+"].length=" + roles[i].length());
  			String[] parts = parseRole(roles[i]);
 			log("2c");
 	  		for (int j = 0; (parts != null) && (j < parts.length); j++) {
 	  			log("parts[" + j + "]=" + parts[j]);
 	  		}
 			log("2d");
 			if ((parts != null) && parts.length == 2) {
 				if ("password".equals(parts[0])) {
 		 	  		//subjectMap.set(Constants.POLICY_SUBJECT.PASSWORD.uri, parts[1]);
 		 			//log("password from parts[1]=" + password);
 				} else {
 					subjectMap.set(parts[0],parts[1]); //todo:  handle multiple values (ldap)
 				}
 			}
 			log("2e");
  		}
  		log("3");
  	} catch (Exception e) {	
  		log("caught exception building subjectMap " + e.getMessage());
  		if (e.getCause() != null) {
  			log(e.getCause().getMessage());
  		}
  	} finally {
  		subjectMap.lock();
  	}
  	log("4");
	log("password=" + password);

  	HashMap commonParams = new HashMap();
  	commonParams.put("useCachedObject", "" + useCachedObject);    
  	commonParams.put("userId", subjectMap.getString(Authorization.SUBJECT_ID_URI_STRING)); //to do: change referring code to access Authorization.SUBJECT_ID, then delete this line   
  	commonParams.put("host", environmentMap.getString(Constants.POLICY_ENVIRONMENT.REQUEST_CLIENT_IP_ADDRESS.uri)); //to do:  as above, vis-a-vis Authorization.ENVIRONMENT_CLIENT_IP
      ReadOnlyContext temp = new ReadOnlyContext(commonParams, environmentMap, subjectMap, password);
      log("5 returning null? " + (temp == null));
      return temp;
    }

  	public static final String[] parseRole (String role) {
  		log("parseRole() " + role);
  		String[] parts = null;
  		if ((role == null) || (role.length() == 0)) {
  			log("parseRole (role == null) || (role.length() == 0)");
  		} else {
  			int i = role.indexOf('=');
  			if (i == 0) {
  				log("parseRole i==0");
  			} else {
  				log("parseRole i==" + i);
  				parts = new String[2];	
  				if (i < 0) {
  					parts[0] = role;
  					parts[1] = ""; //Boolean.toString(true);
					log("parseRole i<0 ==" + i);	 
  				} else {
					log("parseRole i>=0 ==" + i);
  					parts[0] = role.substring(0,i);
  					log("parseRole parts[0]="+parts[0]);
  					if (i == (role.length()-1)) {
  						parts[1] = ""; //Boolean.toString(true);
  					} else {
  						parts[1] = role.substring(i+1);
  					}
					log("parts[1]="+parts[1]);	 
  				}
  			}
  		}
  		return parts; 
  	}
  	
    public String getPassword() {
    	return password;
    }
  	
	public static boolean log = false; 
	
	protected static final void log(String msg) {
		if (! log) return;
		System.err.println(msg);
	}
    
}