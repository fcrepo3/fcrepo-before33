/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.security.servletfilters;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Hashtable;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.security.Principal;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.reflect.Method;
import java.lang.NoSuchMethodException;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;

import fedora.server.security.servletfilters.pubcookie.FilterPubcookie;


/** 
 *  @author Bill Niebel (niebel@virginia.edu)
 */
public abstract class BaseContributing extends FilterSetup {
    protected static Log log = LogFactory.getLog(BaseContributing.class);
        
	public static final HashSet NULL_SET =  new HashSet();
	public static final Hashtable EMPTY_MAP = new Hashtable();

	public static final String[] EMPTY_ARRAY = new String[] {};

	//defaults
    private static boolean AUTHENTICATE_DEFAULT = true;
    private static Collection FILTERS_CONTRIBUTING_SPONSORED_ATTRIBUTES_DEFAULT = NULL_SET;
    
    
    protected boolean AUTHENTICATE = AUTHENTICATE_DEFAULT;
    protected Collection FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES = NULL_SET;
    protected Collection FILTERS_CONTRIBUTING_SPONSORED_ATTRIBUTES = FILTERS_CONTRIBUTING_SPONSORED_ATTRIBUTES_DEFAULT;
    
    public static final String SURROGATE_ROLE_KEY = "surrogate-role";
    private static String SURROGATE_ROLE_DEFAULT = null;   
    protected String SURROGATE_ROLE = SURROGATE_ROLE_DEFAULT; 
    
    public static final String SURROGATE_ATTRIBUTE_KEY = "surrogate-attribute";
    private static String SURROGATE_ATTRIBUTE_DEFAULT = null;   
    protected String SURROGATE_ATTRIBUTE = SURROGATE_ATTRIBUTE_DEFAULT;
    
    public void init(FilterConfig filterConfig) {
    	String method = "init() "; if (log.isDebugEnabled()) log.debug(enter(method));
    	super.init(filterConfig);
    	inited = false;
    	if (! initErrors) {
    		if (FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES.isEmpty()) {
        		if ((FILTER_NAME == null) || "".equals(FILTER_NAME)) {
        			initErrors = true;
    				if (log.isErrorEnabled()) log.error(format(method, "FILTER_NAME not set"));    			
        		} else {
            		FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES = new Vector(1);        			
        			FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES.add(FILTER_NAME);
        		}    			
    		}
    	}
    	if (initErrors) {
			if (log.isErrorEnabled()) log.error(format(method, "FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES not set; see previous error"));
    	}
    	inited = true;
		if (log.isDebugEnabled()) log.debug(exit(method)); 
    }

    
	public void destroy() {
		String method = "destroy()"; if (log.isDebugEnabled()) log.debug(enter(method));
		super.destroy();
		if (log.isDebugEnabled()) log.debug(exit(method));
    }

    
    protected void initThisSubclass(String key, String value) {
    	String method = "initThisSubclass() "; if (log.isDebugEnabled()) log.debug(enter(method));
		boolean setLocally = false;
    	if (SURROGATE_ROLE_KEY.equals(key)) {
    		SURROGATE_ROLE = value;
    		setLocally = true;
    	} else if (SURROGATE_ATTRIBUTE_KEY.equals(key)) {
        		SURROGATE_ATTRIBUTE = value;
        		setLocally = true;    		
    	} else {
    		super.initThisSubclass(key, value);    		
    	} 
    	if (setLocally) {
        	if (log.isDebugEnabled()) log.debug(format(method, "deferring to super"));
    		if (log.isInfoEnabled()) log.info(format(method, "known parameter", key, value));		
    	}
    	if (log.isDebugEnabled()) log.debug(exit(method)); 
	}
    
	public void doThisSubclass(ExtendedHttpServletRequest extendedHttpServletRequest, HttpServletResponse response) throws Throwable {
		String method = "doThisSubclass() "; if (log.isDebugEnabled()) log.debug(enter(method));
		super.doThisSubclass(extendedHttpServletRequest, response);
		boolean alreadyAuthenticated = (extendedHttpServletRequest.getUserPrincipal() != null);
		
		if (log.isDebugEnabled()) {
			log.debug(format(method, null, "alreadyAuthenticated") + alreadyAuthenticated);
			log.debug(format(method, null, "AUTHENTICATE") + AUTHENTICATE);
		}
		
		if (authenticate(alreadyAuthenticated)) {
			if (log.isDebugEnabled()) log.debug(format(method, "calling authenticate() . . ."));				
			authenticate(extendedHttpServletRequest); //"authenticate" is really a conditional cache refresh . . . refactor name?
		} else {
			if (log.isDebugEnabled()) log.debug(format(method, "not calling authenticate()"));				
		}

		String authority = extendedHttpServletRequest.getAuthority();
		log.debug(format(method, null, "authority", authority));
		if ((authority != null) && ! "".equals(authority)) {
			
			if (extendedHttpServletRequest.isUserSponsored()) {
				log.debug(format(method, "user already sponsored")); //so neither get normal user attribute nor check for sponsored user
			} else {
				log.debug(format(method, "user not already sponsored"));
				if (! FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES.contains(authority)) {
					if (log.isDebugEnabled()) log.debug(format(method, "not calling gatherAuthenticatedAttributes()"));								
				} else {
					if (log.isDebugEnabled()) log.debug(format(method, "calling gatherAuthenticatedAttributes() . . ."));								
					contributeAuthenticatedAttributes(extendedHttpServletRequest);					
					
					//these newly-collect attributes could allow surrogate feature, so check:
					boolean surrogateTurnedOnHere = false;
					if ((SURROGATE_ROLE == null) || "".equals(SURROGATE_ROLE)) {
						log.debug(format(method, "no surrogate role configured"));
					} else {
						log.debug(format(method, "surrogate role configured", SURROGATE_ROLE_KEY, SURROGATE_ROLE));
						if (extendedHttpServletRequest.isUserInRole(SURROGATE_ROLE)) {
							log.debug(format(method, "authenticated user has surrogate role"));						
							surrogateTurnedOnHere = true;
						} else {
							log.debug(format(method, "authenticated user doesn't have surrogate role"));						
						}
					}
					if ((SURROGATE_ATTRIBUTE == null) || "".equals(SURROGATE_ATTRIBUTE)) {
						log.debug(format(method, "no surrogate attribute configured"));
					} else {
						log.debug(format(method, "surrogate attribute configured", SURROGATE_ATTRIBUTE_KEY, SURROGATE_ATTRIBUTE));
						if (extendedHttpServletRequest.isAttributeDefined(SURROGATE_ATTRIBUTE)) {
							log.debug(format(method, "authenticated user has surrogate attribute"));						
							surrogateTurnedOnHere = true;
						} else {
							log.debug(format(method, "authenticated user doesn't have surrogate attribute"));						
						}
					}
					if (surrogateTurnedOnHere) {				
						log.debug(format(method, "setting user to sponsored"));						
						extendedHttpServletRequest.setSponsoredUser();
						if (extendedHttpServletRequest.isUserSponsored()) {
							log.debug(format(method, "verified that user is sponsored"));						
						} else {
							log.error(format(method, "user is not correctly sponsored"));												
						}
					}
				}
			}

			if (extendedHttpServletRequest.isUserSponsored()) { //either from earlier filter or in this filter's code directly above
				if (! FILTERS_CONTRIBUTING_SPONSORED_ATTRIBUTES.contains(authority)) {
					if (log.isDebugEnabled()) log.debug(format(method, "not calling gatherSponsoredAttributes()"));												
				} else {
					if (log.isDebugEnabled()) log.debug(format(method, "calling gatherSponsoredAttributes() . . ."));								
					contributeSponsoredAttributes(extendedHttpServletRequest);					
				}	
			}

		}
		
	}
	
	// NO CACHING AT THIS SUBCLASSING
	abstract protected void authenticate(ExtendedHttpServletRequest extendedHttpServletRequest) throws Exception;
	
	abstract protected void contributeAuthenticatedAttributes(ExtendedHttpServletRequest extendedHttpServletRequest) throws Exception;
	
	abstract protected void contributeSponsoredAttributes(ExtendedHttpServletRequest extendedHttpServletRequest) throws Exception;
	
	abstract protected boolean authenticate(boolean alreadyAuthenticated);


    
}
