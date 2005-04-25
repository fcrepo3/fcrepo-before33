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

	
    public static ReadOnlyContext EMPTY=new ReadOnlyContext(null, null, "");
    static {
    	EMPTY.setActionAttributes(null);
    	EMPTY.setResourceAttributes(null);
    }
    
    /*
    public final boolean useCachedObject() {
    	//announce a call which you missed replacing
    		try {
				throw new Exception("REMNANT CALL TO CONTEXT.USECACHEDOBJECT()");
			} catch (Exception e) {
				e.printStackTrace();
			}    	
    	return "true".equalsIgnoreCase(this.get("useCachedObject"));
    }
*/
    
    
    private final Date now = new Date();
    
    private MultiValueMap m_environmentAttributes;
    public final MultiValueMap getEnvironmentAttributes() {
    	return m_environmentAttributes;
    }

    private MultiValueMap m_subjectAttributes;
    
    private MultiValueMap m_actionAttributes;
    
    private MultiValueMap m_resourceAttributes;
    
    private String password;
    
    private final boolean noOp = false; 
    

    /*
    public static Context getUncachedContext() {
        HttpServletRequest req=(HttpServletRequest) MessageContext.
                getCurrentContext().getProperty(
                HTTPConstants.MC_HTTP_SERVLETREQUEST);
        return ReadOnlyContext.getContext(Constants.HTTP_REQUEST.SOAP.uri, req, false);
    }
    */

    /**
     * Creates and initializes the <code>Context</code>.
     *
     * @param parameters A pre-loaded Map of name-value pairs
     *        comprising the context.
     */
    private ReadOnlyContext(MultiValueMap environmentAttributes, MultiValueMap subjectAttributes, String password) {
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
        if (password == null) {
        	password = "";
        }
        this.password = password;
    }
    
    /*
    private ReadOnlyContext(Map parameters) {
        this(parameters, null, null, "");    	
    	//announce a call which you missed replacing
		try {
			throw new Exception("REMNANT CALL TO CONTEXT.USECACHEDOBJECT()");
		} catch (Exception e) {
			e.printStackTrace();
		}    	
    }
    */

    public static ReadOnlyContext getCopy(Context source) {
        HashMap params=new HashMap();
        Iterator iter;
        iter=source.names();
        while (iter.hasNext()) {
            String k=(String) iter.next();
            params.put(k, source.get(k));
        }
        //vvvvv this fixup to allow compilation; needs extension for new fields vvvvv
        ReadOnlyContext temp = new ReadOnlyContext(null, null, source.getPassword());
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
                ReadOnlyContext temp = new ReadOnlyContext(null, null, a.getPassword());
                temp.setActionAttributes(null);
                temp.setResourceAttributes(null);
                return temp;
                //^^^^^ this fixup to allow compilation; needs extension for new fields ^^^^^
            }
        }
    }
 
    public String get(String name) {
    	//if (("useCachedObject".equals(name)) ||  ("userId".equals(name)) || ("application".equals(name))) {    		
    	//announce a call which you missed replacing
    		try {
				throw new Exception("REMNANT CALL TO CONTEXT.GET(" + name +")");
			} catch (Exception e) {
				e.printStackTrace();
			}
    	//}
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
    public static final ReadOnlyContext getContext(Map parameters) {
    	//announce a call which you missed replacing
		try {
			throw new Exception("REMNANT CALL TO CONTEXT.GETCONTEXT(MAP)");
		} catch (Exception e) {
			e.printStackTrace();
		}       	
    	if (parameters == null) {
    		parameters = new Hashtable();
    	}
        return new ReadOnlyContext(parameters);
    }
    */

    /*
    public static final ReadOnlyContext getContext(String soapOrRest, HttpServletRequest request, boolean useCachedObject) {
    	//announce a call which you missed replacing
		try {
			throw new Exception("REMNANT CALL TO CONTEXT.GETCONTEXT(String soapOrRest, HttpServletRequest request, boolean useCachedObject)");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return getContext(soapOrRest, request, useCachedObject, null, null, null);
    }
    */

    /* was so.  delete after refactoring
    public static final ReadOnlyContext getContext(String soapOrRest, HttpServletRequest request, 
    		String subjectId, String password, String[] roles) {
    	return getContext(soapOrRest, request, false, subjectId, password, roles);
    }
    */
    
    /*
    public static final ReadOnlyContext getContext(String soapOrRest, HttpServletRequest request, boolean temp,
    		String subjectId, String password, String[] roles) {
    	//announce a call which you missed replacing
		try {
			throw new Exception("REMNANT CALL TO CONTEXT.GETCONTEXT(6)");
		} catch (Exception e) {
			e.printStackTrace();
		}     	
    	return getContext(soapOrRest, request, subjectId, password, roles);
    }
    */
    
    private static final MultiValueMap beginEnvironmentMap(String messageProtocol) throws Exception {
    	MultiValueMap environmentMap = new MultiValueMap();
  	    environmentMap.set(Constants.HTTP_REQUEST.MESSAGE_PROTOCOL.uri, messageProtocol);
		Date now = new Date();
		environmentMap.set(Constants.ENVIRONMENT.CURRENT_DATE_TIME.uri, DateUtility.convertDateToString(now));
		environmentMap.set(Constants.ENVIRONMENT.CURRENT_DATE.uri, DateUtility.convertDateToDateString(now));
		environmentMap.set(Constants.ENVIRONMENT.CURRENT_TIME.uri, DateUtility.convertDateToTimeString(now));
		return environmentMap;
    }

    public static Context getSoapContext() {
        HttpServletRequest req=(HttpServletRequest) MessageContext.
                getCurrentContext().getProperty(
                HTTPConstants.MC_HTTP_SERVLETREQUEST);
      return ReadOnlyContext.getContext(Constants.HTTP_REQUEST.SOAP.uri, req);
    }

    
    public static final ReadOnlyContext getContext(Context existingContext, String subjectId, String password, String[] roles) {
  		return getContext(existingContext.getEnvironmentAttributes(), subjectId, password, roles);
    }
    
    private static final ReadOnlyContext getContext(MultiValueMap environmentMap, String subjectId, String password, String[] roles) {
    	MultiValueMap subjectMap = new MultiValueMap(); 
      	try {		
      		subjectMap.set(Constants.SUBJECT.LOGIN_ID.uri, (subjectId == null) ? "" : subjectId);
      		for (int i = 0; (roles != null) && (i < roles.length); i++) {
      			String[] parts = parseRole(roles[i]);
     			if ((parts != null) && parts.length == 2) {
    				subjectMap.set(parts[0],parts[1]); //todo:  handle multiple values (ldap)
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
      	return new ReadOnlyContext(environmentMap, subjectMap, (password == null) ? "" : password);
    }

    // needed for, e.g., rebuild
    public static final ReadOnlyContext getContext(String messageProtocol, String subjectId, String password, String[] roles) throws Exception {
    	MultiValueMap environmentMap = beginEnvironmentMap(messageProtocol);
  		environmentMap.lock(); 
  		return getContext(environmentMap, subjectId, password, roles);
    }


    
    /* needed?
    public static final ReadOnlyContext getContext(String messageProtocol) {
		MultiValueMap environmentMap = null;
	  	try {
	  		environmentMap = beginEnvironmentMap(messageProtocol);  			
	  	} catch (Exception e) {
	  	} finally {
	  		environmentMap.lock();
	  	}
  	  	if (subjectId == null) {
  	  		subjectId = "";
  	  	}
  	  	if (password == null) {
  	  		password = "";
  	  	}
  	  	if (roles == null) {
  	  		roles = new String[0];
  	  	}  	  	
  	  	return getContext(environmentMap, subjectId, password, roles);
    }
    */
        
    /*
     * Gets a Context appropriate for the request, and whether it is ok
     * to use the dissemination cache or not.
     */
    //form context from optional servlet request, overriding request for added parms    
    public static final ReadOnlyContext getContext(String messageProtocol, HttpServletRequest request) {
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
  	  	String[] roles = null;
  	  	if (request.getUserPrincipal() == null) {
  	  		System.err.println("in context, no principal to grok roles from!!");				
  	  	} else {
			if (! (request.getUserPrincipal() instanceof GenericPrincipal)) {
				System.err.println("in context, principal is -not- GenericPrincipal, so I'm not groking roles from it!!");
			} else {
				System.err.println("in context, principal is GenericPrincipal, so I can grok roles from it!!");
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
  	  	}
  	  	if (subjectId == null) {
  	  		subjectId = "";
  	  	}
  	  	if (password == null) {
  	  		password = "";
  	  	}
  	  	if (roles == null) {
  	  		roles = new String[0];
  	  	}
  	  	
  	return getContext(environmentMap, subjectId, password, roles);
    }
    
    /*
    public final void setUseCachedObject(boolean useCachedObject) {
    	setParameter("useCachedObject", "" + useCachedObject);  
    }
    */
    
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