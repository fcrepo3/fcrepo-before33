package fedora.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.realm.GenericPrincipal;

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

	
    public static ReadOnlyContext EMPTY=new ReadOnlyContext(null, null, null);
    static {
    	EMPTY.setActionAttributes(null);
    	EMPTY.setResourceAttributes(null);
    }
    
    private final Date now = new Date();
    
    private MultiValueMap m_environmentAttributes;

    private MultiValueMap m_subjectAttributes;
    
    private MultiValueMap m_actionAttributes;
    
    private MultiValueMap m_resourceAttributes;

    /**
     * Creates and initializes the <code>Context</code>.
     *
     * @param parameters A pre-loaded Map of name-value pairs
     *        comprising the context.
     */
    public ReadOnlyContext(Map parameters, MultiValueMap environmentAttributes, MultiValueMap subjectAttributes) {
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
    }
    
    public ReadOnlyContext(Map parameters) {
        this(parameters, null, null);
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
        ReadOnlyContext temp = new ReadOnlyContext(params, null, null);
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
                ReadOnlyContext temp = new ReadOnlyContext(params, null, null);
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
    
    /*
     * Gets a Context appropriate for the request, and whether it is ok
     * to use the dissemination cache or not.
     */
    public static final Context getContext(String soapOrRest, HttpServletRequest request, boolean useCachedObject) {
      
  	MultiValueMap environmentMap = new MultiValueMap();
  	//h.put(Authorization.ENVIRONMENT_CURRENT_DATETIME_URI_STRING, "2005-01-26T16:42:00Z");  //does xacml engine provide this?
  	try {
  		environmentMap.set(Authorization.ENVIRONMENT_REQUEST_PROTOCOL_URI_STRING, request.getProtocol());
  		environmentMap.set(Authorization.ENVIRONMENT_REQUEST_SCHEME_URI_STRING, request.getScheme());
  		environmentMap.set(Authorization.ENVIRONMENT_REQUEST_SECURITY_URI_STRING, (request.isSecure()) ? "secure" : "insecure");
  	
  	    if (request.getAuthType() != null) {
  	    	environmentMap.set(Authorization.ENVIRONMENT_REQUEST_AUTHTYPE_URI_STRING, request.getAuthType());  
  	    }
  	
  	    environmentMap.set(Authorization.ENVIRONMENT_REQUEST_METHOD_URI_STRING, request.getMethod());	
  	    if (request.isRequestedSessionIdFromCookie()) {
  	    	environmentMap.set(Authorization.ENVIRONMENT_REQUEST_SESSION_ENCODING_URI_STRING, "cookie");    	
  	    } else if (request.isRequestedSessionIdFromURL()) {
  	    	environmentMap.set(Authorization.ENVIRONMENT_REQUEST_SESSION_ENCODING_URI_STRING, "url");    	
  	    }
  	    environmentMap.set(Authorization.ENVIRONMENT_REQUEST_SESSION_STATUS_URI_STRING, request.isRequestedSessionIdValid() ? "valid" : "invalid"   );
  	    if (request.getContentLength() > -1) {
  	    	environmentMap.set(Authorization.ENVIRONMENT_REQUEST_CONTENT_LENGTH_URI_STRING, "" + request.getContentLength());    	
  	    }
  	    if (request.getContentType() != null) {
  	    	environmentMap.set(Authorization.ENVIRONMENT_REQUEST_CONTENT_TYPE_URI_STRING, request.getContentType());
  	    }
  	    environmentMap.set(Authorization.ENVIRONMENT_REQUEST_MESSAGE_PROTOCOL_URI_STRING, soapOrRest);
  	    if (! request.getRemoteHost().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {	    
  	    	environmentMap.set(Authorization.ENVIRONMENT_CLIENT_FQDN_URI_STRING, request.getRemoteHost().toLowerCase());        
  	    }
  	    environmentMap.set(Authorization.ENVIRONMENT_CLIENT_IP_URI_STRING, request.getRemoteAddr());
  	    if (! request.getLocalName().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
  	    	environmentMap.set(Authorization.ENVIRONMENT_SERVER_FQDN_URI_STRING, request.getLocalName().toLowerCase());
  	    }
  	    environmentMap.set(Authorization.ENVIRONMENT_SERVER_IP_URI_STRING, request.getLocalAddr());
  	    environmentMap.set(Authorization.ENVIRONMENT_SERVER_PORT_URI_STRING, "" + request.getLocalPort());
  	} catch (Exception e) {
  	} finally {
  		environmentMap.lock();
  	}

  	String subjectId = request.getRemoteUser();

  	//roles are available through xacml "attribute finder" callback and so are not stored here
  	
  	MultiValueMap subjectMap = new MultiValueMap();
  	//authn might not have been required by web.xml
  	if (subjectId == null) {
  		subjectId = "";
  	}
  	String roles[] = null;
  	if ((request.getUserPrincipal() != null)
  	&&  (request.getUserPrincipal() instanceof GenericPrincipal)) {		
  		roles = ((GenericPrincipal) request.getUserPrincipal()).getRoles();
  	}

  	try {		
  		log("1");
  		subjectMap.set(Authorization.SUBJECT_ID_URI_STRING, subjectId);
  		log("2");
  		for (int i = 0; (roles != null) && (i < roles.length); i++) {
  			String[] parts = parseRole(roles[i]);
  			log(parts[0] + "][" + parts[1]);
  			subjectMap.set(parts[0],parts[1]); //todo:  handle multiple values (ldap)
  		}
  		log("3");
  	} catch (Exception e) {	
  		log("caught exception building subjectMap");
  	} finally {
  		subjectMap.lock();
  	}
  	log("4");

  	HashMap commonParams = new HashMap();
  	commonParams.put("useCachedObject", "" + useCachedObject);    
  	commonParams.put("userId", subjectMap.getString(Authorization.SUBJECT_ID_URI_STRING)); //to do: change referring code to access Authorization.SUBJECT_ID, then delete this line   
  	commonParams.put("host", environmentMap.getString(Authorization.ENVIRONMENT_CLIENT_IP_URI_STRING)); //to do:  as above, vis-a-vis Authorization.ENVIRONMENT_CLIENT_IP
      ReadOnlyContext temp = new ReadOnlyContext(commonParams, environmentMap, subjectMap);
      log("5 returning null? " + (temp == null));
      return temp;
    }

  	public static final String[] parseRole (String role) {
  		//System.out.println("parseRole() "+role);
  		String[] parts = null;
  		if ((role == null) || (role.length() == 0)) {
  			//System.out.println("(role == null) || (role.length() == 0)");
  		} else {
  			int i = role.indexOf('=');
  			if (i == 0) {
  				//System.out.println("i==0");
  			} else {
  				parts = new String[2];	
  				if (i < 0) {
  					parts[0] = role;
  					parts[1] = ""; //Boolean.toString(true);
//  System.out.println("Boolean.toString(true)="+parts[1]);
  				} else {
  					parts[0] = role.substring(0,i);
  					//System.out.println("parts[0]="+parts[0]);
  					if (i == (role.length()-1)) {
  						parts[1] = ""; //Boolean.toString(true);
//  System.out.println("Boolean.toString(true)="+parts[1]);
  					} else {
  						parts[1] = role.substring(i+1);
  						//System.out.println("parts[1]="+parts[1]);
  					}
  				}
  				//System.out.println("parts[0]="+parts[0]+" parts[1]="+parts[1]);
  			}
  		}
  		return parts; 
  	}
  	
	public static boolean log = false; 
	
	protected static final void log(String msg) {
		if (! log) return;
		System.err.println(msg);
	}
    
}