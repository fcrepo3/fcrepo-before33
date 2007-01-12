package fedora.server.security.servletfilters;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fedora.server.errors.authorization.AuthzOperationalException;


import javax.servlet.http.HttpServletRequest;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Hashtable;
import java.security.Principal;
import javax.servlet.http.HttpServletRequestWrapper;
/** 
 *  @author Bill Niebel (niebel@virginia.edu)
 */
public interface ExtendedHttpServletRequest 
	extends HttpServletRequest {
	
	public static final String SUCCEEDED = "succeeded";
	public static final String FAILED = "failed";
	
	public static final ImmutableHashSet IMMUTABLE_NULL_SET = new ImmutableHashSet();

	/*
	private static final int INITIAL = 0;
	private static final int UNAUTHENTICATED = 1;
	private static final int AUTHENTICATED = 2;
	private static final int SPONSORED_REQUEST = 3;
	private static final int SPONSORING = 4;
	private int state = INITIAL;
	*/
	
	public void audit();
	
	public void lockWrapper() throws Exception;
	
    //public void setSponsoredUser(String sponsoredUser) throws Exception;

    public void setSponsoredUser() throws Exception;
    
    public void lockSponsoredUser() throws Exception;
    
    //public boolean isSponsoredUserRequested();
    
    public void setAuthenticated(Principal userPrincipal, String authority) throws Exception;
    
    public boolean isUserSponsored();
    
    public boolean isAuthenticated();
    
    public Set getAttributeValues(String key) throws AuthzOperationalException;
    
    public boolean hasAttributeValues(String key) throws AuthzOperationalException;
    
    public boolean isAttributeDefined(String key) throws AuthzOperationalException;

    public void addRoles(String authority, Set roles) throws Exception;

    public void addAttributes(String authority, Map attributes) throws Exception;
    
    public String getUser() throws Exception;

    public String getPassword() throws Exception;
    
    public Map getAllAttributes() throws Exception;
    
    public String getAuthority();
    
    public String getFromHeader();
    
}
