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
    /*
    static {
    	System.err.print(ContributingAuthFilter.class.getName() + " logging includes ");
    	if (log.isFatalEnabled()) System.err.print("FATAL,");
    	if (log.isErrorEnabled()) System.err.print("ERROR,");
    	if (log.isWarnEnabled()) System.err.print("WARN,");
    	if (log.isInfoEnabled()) System.err.print("INFO,");
    	if (log.isDebugEnabled()) System.err.print("DEBUG,");
    	System.err.println();
    }
    */
    
    /*
    protected String getClassName() {
    	return this.getClass().getName();
    }
    */
    
    
	public static final HashSet NULL_SET =  new HashSet();
	public static final Hashtable EMPTY_MAP = new Hashtable();

	public static final String[] EMPTY_ARRAY = new String[] {};
    //defaults

    private static boolean AUTHENTICATE_DEFAULT = true;
    private static Collection FILTERS_CONTRIBUTING_SPONSORED_ATTRIBUTES_DEFAULT = NULL_SET;
    
    
    //variables

   
    protected boolean AUTHENTICATE = AUTHENTICATE_DEFAULT;
    protected Collection FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES = NULL_SET;
    protected Collection FILTERS_CONTRIBUTING_SPONSORED_ATTRIBUTES = FILTERS_CONTRIBUTING_SPONSORED_ATTRIBUTES_DEFAULT;
    
    public static final String SURROGATE_ROLE_KEY = "surrogate-role";
    private static String SURROGATE_ROLE_DEFAULT = null;   
    protected String SURROGATE_ROLE = SURROGATE_ROLE_DEFAULT; 
    
    public void init(FilterConfig filterConfig) {
    	String method = "init() "; if (log.isDebugEnabled()) log.debug(enter(method));
    	super.init(filterConfig);
    	if (! initErrors) {
    		FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES = new Vector(1);
    		if ((FILTER_NAME == null) || "".equals(FILTER_NAME)) {
    			initErrors = true;
				if (log.isErrorEnabled()) log.error(format(method, "FILTER_NAME not set"));    			
    		} else {
    			FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES.add(FILTER_NAME);
    		}
    	}
    	if (initErrors) {
			if (log.isErrorEnabled()) log.error(format(method, "FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES not set; see previous error"));
    	}
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
    	} else {
    		super.initThisSubclass(key, value);    		
    	} 
    	if (setLocally) {
        	if (log.isErrorEnabled()) log.error(format(method, "deferring to super"));
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
			/*
			log.debug(format(method, null, "AUTHENTICATE_ALWAYS", AUTHENTICATE_ALWAYS));
			log.debug(format(method, null, "AUTHENTICATE_IFF_NOT_ALREADY_AUTHENTICATED", AUTHENTICATE_IFF_NOT_ALREADY_AUTHENTICATED));
			*/
			log.debug(format(method, null, "AUTHENTICATE") + AUTHENTICATE);
		}
		
		if (authenticate(alreadyAuthenticated)) {
			if (log.isDebugEnabled()) log.debug(format(method, "calling authenticate() . . ."));				
			authenticate(extendedHttpServletRequest);
		} else {
			if (log.isDebugEnabled()) log.debug(format(method, "not calling authenticate()"));				
		}

		//boolean authenticatedHere = ((extendedHttpServletRequest.getUserPrincipal() != null) && ! alreadyAuthenticated); 
		
		if (log.isDebugEnabled()) {
			//log.debug(format(method, null, "authenticatedHere") + authenticatedHere);
			//log.debug(format(method, null, "GATHER_ATTRIBUTES_IFF_ALREADY_AUTHENTICATED", GATHER_ATTRIBUTES_IFF_ALREADY_AUTHENTICATED));
			//log.debug(format(method, null, "GATHER_ATTRIBUTES_IFF_AUTHENTICATED_HERE", GATHER_ATTRIBUTES_IFF_AUTHENTICATED_HERE));
			//log.debug(format(method, null, "FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES", FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES));
			//log.debug(format(method, null, "GATHER_SPONSORED_ATTRIBUTES", GATHER_SPONSORED_ATTRIBUTES));
		}

		String authority = extendedHttpServletRequest.getAuthority();
		if ((authority != null) && ! "".equals(authority)) {
			
			if (! extendedHttpServletRequest.isUserSponsored()) {
				if (FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES.contains(authority)) {
					if (log.isDebugEnabled()) log.debug(format(method, "calling gatherAuthenticatedAttributes() . . ."));								
					contributeAuthenticatedAttributes(extendedHttpServletRequest);					
				} else {
					if (log.isDebugEnabled()) log.debug(format(method, "not calling gatherAuthenticatedAttributes()"));								
				}
			}
			
			if (extendedHttpServletRequest.isUserInRole(SURROGATE_ROLE)) {
				extendedHttpServletRequest.setSponsoredUser();
			}

			if (extendedHttpServletRequest.isUserSponsored()) {
				if (FILTERS_CONTRIBUTING_SPONSORED_ATTRIBUTES.contains(authority)) {
					if (log.isDebugEnabled()) log.debug(format(method, "calling gatherSponsoredAttributes() . . ."));								
					contributeSponsoredAttributes(extendedHttpServletRequest);					
				} else {
					if (log.isDebugEnabled()) log.debug(format(method, "not calling gatherSponsoredAttributes()"));												
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
