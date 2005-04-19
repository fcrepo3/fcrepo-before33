package fedora.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.catalina.realm.GenericPrincipal;

import com.sun.xacml.attr.DateAttribute;
import com.sun.xacml.attr.DateTimeAttribute;
import com.sun.xacml.attr.TimeAttribute;

import fedora.common.Constants;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.NotAuthorizedException;
import fedora.server.security.Authorization;
import fedora.server.utilities.DateUtility;

/**
 *
 * <p><b>Title:</b> ReadOnlyContext.java</p>
 * <p><b>Description:</b> Context that is read-only.</p>
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
    
    private final boolean noOp = false; 
    
    public static Context getCachedContext() {
        HttpServletRequest req=(HttpServletRequest) MessageContext.
                getCurrentContext().getProperty(
                HTTPConstants.MC_HTTP_SERVLETREQUEST);
      return ReadOnlyContext.getContext(Constants.HTTP_REQUEST.SOAP.uri, req, true);
    }

    public static Context getUncachedContext() {
        HttpServletRequest req=(HttpServletRequest) MessageContext.
                getCurrentContext().getProperty(
                HTTPConstants.MC_HTTP_SERVLETREQUEST);
        return ReadOnlyContext.getContext(Constants.HTTP_REQUEST.SOAP.uri, req, false);
    }

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
        if (password == null) {
        	password = "";
        }
        this.password = password;
    }
    
    private ReadOnlyContext(Map parameters) {
        this(parameters, null, null, "");
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

    
    public static final ReadOnlyContext getContext(String soapOrRest, HttpServletRequest request, boolean useCachedObject) {
    	return getContext(soapOrRest, request, useCachedObject, null, null, null);
    }
    
    /*
     * Gets a Context appropriate for the request, and whether it is ok
     * to use the dissemination cache or not.
     */
    public static final ReadOnlyContext getContext(String soapOrRest, HttpServletRequest request, boolean useCachedObject,
    		String subjectId, String password, String[] roles) {
System.err.println("in context, handling roles parm =" + roles);	
if (roles != null) {
	System.err.println("in context, role parm length=" + roles.length);	
}	
if (request != null) {
	System.err.println(request.getMethod() + request.getRequestURI());
}
      
  	MultiValueMap environmentMap = new MultiValueMap();
  	try {
  	  	//h.put(Authorization.ENVIRONMENT_CURRENT_DATETIME_URI_STRING, "2005-01-26T16:42:00Z");  //does xacml engine provide this?
  		Date now = new Date();
		/*DateTimeAttribute tempDateTimeAttribute = new DateTimeAttribute(now, 0, 0, 0);  		
		DateAttribute tempDateAttribute = new DateAttribute(now, 0, 0);  		
		TimeAttribute tempTimeAttribute = new TimeAttribute(now, 0, 0, 0);
		*/
  		environmentMap.set(Constants.ENVIRONMENT.CURRENT_DATE_TIME.uri, DateUtility.convertDateToString(now));
  		environmentMap.set(Constants.ENVIRONMENT.CURRENT_DATE.uri, DateUtility.convertDateToDateString(now));
  		environmentMap.set(Constants.ENVIRONMENT.CURRENT_TIME.uri, DateUtility.convertDateToTimeString(now));
  		
  		environmentMap.set(Constants.HTTP_REQUEST.PROTOCOL.uri, request.getProtocol());
  		environmentMap.set(Constants.HTTP_REQUEST.SCHEME.uri, request.getScheme());
  		environmentMap.set(Constants.HTTP_REQUEST.SECURITY.uri, 
  				(request.isSecure()) ? Constants.HTTP_REQUEST.SECURE.uri : Constants.HTTP_REQUEST.INSECURE.uri);
  	
  	    if (request.getAuthType() != null) {
  	    	environmentMap.set(Constants.HTTP_REQUEST.AUTHTYPE.uri, request.getAuthType());  
  	    }
  	
  	    environmentMap.set(Constants.HTTP_REQUEST.METHOD.uri, request.getMethod());	
  	    if (request.isRequestedSessionIdFromCookie()) {
  	    	environmentMap.set(Constants.HTTP_REQUEST.SESSION_ENCODING.uri, "cookie");    	
  	    } else if (request.isRequestedSessionIdFromURL()) {
  	    	environmentMap.set(Constants.HTTP_REQUEST.SESSION_ENCODING.uri, "url");    	
  	    }
  	    environmentMap.set(Constants.HTTP_REQUEST.SESSION_STATUS.uri, request.isRequestedSessionIdValid() ? "valid" : "invalid"   );
  	    if (request.getContentLength() > -1) {
  	    	environmentMap.set(Constants.HTTP_REQUEST.CONTENT_LENGTH.uri, "" + request.getContentLength());    	
  	    }
  	    if (request.getContentType() != null) {
  	    	environmentMap.set(Constants.HTTP_REQUEST.CONTENT_TYPE.uri, request.getContentType());
  	    }
  	    environmentMap.set(Constants.HTTP_REQUEST.MESSAGE_PROTOCOL.uri, soapOrRest);
  	    if (! request.getRemoteHost().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {	    
  	    	environmentMap.set(Constants.HTTP_REQUEST.CLIENT_FQDN.uri, request.getRemoteHost().toLowerCase());        
  	    }
  	    environmentMap.set(Constants.HTTP_REQUEST.CLIENT_IP_ADDRESS.uri, request.getRemoteAddr());
  	    if (! request.getLocalName().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
  	    	environmentMap.set(Constants.HTTP_REQUEST.SERVER_FQDN.uri, request.getLocalName().toLowerCase());
  	    }
  	    environmentMap.set(Constants.HTTP_REQUEST.SERVER_IP_ADDRESS.uri, request.getLocalAddr());
  	    environmentMap.set(Constants.HTTP_REQUEST.SERVER_PORT.uri, "" + request.getLocalPort());
  	} catch (Exception e) {
  	} finally {
  		environmentMap.lock();
  	}

  	if (subjectId == null) {
  		subjectId = request.getRemoteUser();
  	}

  	//roles are available through xacml "attribute finder" callback and so are not stored here 
  	//as subject attrs
  	
  	MultiValueMap subjectMap = new MultiValueMap();
  	//authn might not have been required by web.xml
  	if (subjectId == null) {
  		subjectId = "";
  	}
  	if (request.getUserPrincipal() != null) {
  	  	log("request.getUserPrincipal().getName()=" + request.getUserPrincipal().getName());
  	}
  	
  	if (request.getUserPrincipal() == null) {
System.err.println("in context, no principal to grok roles from!!");				
  	} else {
			if (request.getUserPrincipal() instanceof GenericPrincipal) {
				System.err.println("in context, principal is GenericPrincipal, so I can grok roles from it!!");
			} else {
				System.err.println("in context, principal is -not- GenericPrincipal, so I'm not groking roles from it!!");
			}
  	}
  	
  	if ((request.getUserPrincipal() != null) && (request.getUserPrincipal() instanceof GenericPrincipal)) {		
  	  	if (((GenericPrincipal) request.getUserPrincipal()).getPassword() != null ) {
  	  		if (password == null) {
  	  			password = ((GenericPrincipal) request.getUserPrincipal()).getPassword();
  	  		}
  	  		if (password == null) {
  	  			password = "";
  	  		}
  	  	}
  	  	if (roles == null) {
  	  		roles = ((GenericPrincipal) request.getUserPrincipal()).getRoles();
  	  	}
  	}
  	try {		
  		subjectMap.set(Constants.SUBJECT.LOGIN_ID.uri, subjectId);
  		for (int i = 0; (roles != null) && (i < roles.length); i++) {
  			String[] parts = parseRole(roles[i]);
 			if ((parts != null) && parts.length == 2) {
				subjectMap.set(parts[0],parts[1]); //todo:  handle multiple values (ldap)
System.err.println("in context, adding subject attr " + parts[0] + "=" + parts[1]);				
 			}
  		}
  	} catch (Exception e) {	
  		log("caught exception building subjectMap " + e.getMessage());
  		if (e.getCause() != null) {
  			log(e.getCause().getMessage());
  		}
  	} finally {
  		subjectMap.lock();
  	}

  	HashMap commonParams = new HashMap();
  	commonParams.put("useCachedObject", "" + useCachedObject);    
  	commonParams.put("userId", subjectMap.getString(Constants.SUBJECT.LOGIN_ID.uri)); //to do: change referring code to access Authorization.SUBJECT_ID, then delete this line   
  	commonParams.put("host", environmentMap.getString(Constants.HTTP_REQUEST.CLIENT_IP_ADDRESS.uri)); //to do:  as above, vis-a-vis Authorization.ENVIRONMENT_CLIENT_IP
      ReadOnlyContext temp = new ReadOnlyContext(commonParams, environmentMap, subjectMap, password);

      /*
    String fromHeader = request.getHeader("From");
	if ((fromHeader != null) && ("".equals(fromHeader))) {
	    if (authorizationModule == null) {
			throw new NotAuthorizedException("no authorizationModule");	
	    }
	    authorizationModule.enforceGetDissemination(temp, PID, bDefPID, methodName, asOfDateTime);
		subjectId = fromHeader;
	}	
			subjectMap.lock();

*/

      return temp;
    }
    
    public final void setUseCachedObject(boolean useCachedObject) {
    	setParameter("useCachedObject", "" + useCachedObject);  
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
	
	protected static final void log(String msg) {
		if (! log) return;
		System.err.println(msg);
	}
    
}