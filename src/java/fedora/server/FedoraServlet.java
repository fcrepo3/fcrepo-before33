package fedora.server;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.realm.GenericPrincipal;

import java.util.HashMap;
import java.util.Iterator;

import fedora.server.Context;
import fedora.server.ReadOnlyContext;
import fedora.server.security.Authorization;

public class FedoraServlet extends HttpServlet {
	
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
	    environmentMap.set(Authorization.ENVIRONMENT_REQUEST_SOAP_OR_REST_URI_STRING, soapOrRest);
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
		System.err.println("1");
		subjectMap.set(Authorization.SUBJECT_ID_URI_STRING, subjectId);
		System.err.println("2");
		for (int i = 0; (roles != null) && (i < roles.length); i++) {
			String[] parts = parseRole(roles[i]);
			System.err.println(parts[0] + "][" + parts[1]);
			subjectMap.set(parts[0],parts[1]); //todo:  handle multiple values (ldap)
		}
		System.err.println("3");
	} catch (Exception e) {	
		System.err.println("caught exception building subjectMap");
	} finally {
		subjectMap.lock();
	}
	System.err.println("4");

	HashMap commonParams = new HashMap();
	commonParams.put("useCachedObject", "" + useCachedObject);    
	commonParams.put("userId", subjectMap.getString(Authorization.SUBJECT_ID_URI_STRING)); //to do: change referring code to access Authorization.SUBJECT_ID, then delete this line   
	commonParams.put("host", environmentMap.getString(Authorization.ENVIRONMENT_CLIENT_IP_URI_STRING)); //to do:  as above, vis-a-vis Authorization.ENVIRONMENT_CLIENT_IP
    ReadOnlyContext temp = new ReadOnlyContext(commonParams, environmentMap, subjectMap);
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
//System.out.println("Boolean.toString(true)="+parts[1]);
				} else {
					parts[0] = role.substring(0,i);
					//System.out.println("parts[0]="+parts[0]);
					if (i == (role.length()-1)) {
						parts[1] = ""; //Boolean.toString(true);
//System.out.println("Boolean.toString(true)="+parts[1]);
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
  
}
