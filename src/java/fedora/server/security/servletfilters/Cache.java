package fedora.server.security.servletfilters;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.util.Map;
import java.util.Hashtable;
import java.util.Set;


/** 
 *  @author Bill Niebel (niebel@virginia.edu)
 */
public class Cache {

    private Log log = LogFactory.getLog(Cache.class);
    
    private final String cacheId;
    
    private final String CACHE_KEY_SEPARATOR;
    private final String AUTH_SUCCESS_TIMEOUT_UNIT;
    private final int AUTH_SUCCESS_TIMEOUT_DURATION;
    private final String AUTH_FAILURE_TIMEOUT_UNIT;
    private final int AUTH_FAILURE_TIMEOUT_DURATION;  
    private final String AUTH_EXCEPTION_TIMEOUT_UNIT;
    private final int AUTH_EXCEPTION_TIMEOUT_DURATION;  
    
    private final CacheElementPopulator cacheElementPopulator;

    public final String getCacheId() {
    	return cacheId;
    }
    
    public final String getCacheKeySeparator() { 
    	return CACHE_KEY_SEPARATOR;
    }
    public final String getAuthSuccessTimeoutUnit() {
    	return AUTH_SUCCESS_TIMEOUT_UNIT;
    }
    public final int getAuthSuccessTimeoutDuration() {
    	return AUTH_SUCCESS_TIMEOUT_DURATION;
    }
    public final String getAuthFailureTimeoutUnit() {
    	return AUTH_FAILURE_TIMEOUT_UNIT;
    }
    public final int getAuthFailureTimeoutDuration() {
    	return AUTH_FAILURE_TIMEOUT_DURATION;  
    }
    public final String getAuthExceptionTimeoutUnit() {
    	return AUTH_EXCEPTION_TIMEOUT_UNIT;
    }
    public final int getAuthExceptionTimeoutDuration() {
    	return AUTH_EXCEPTION_TIMEOUT_DURATION;    
    }
    
    public final CacheElementPopulator getCacheElementPopulator() {
    	return cacheElementPopulator;
    }
    
    /*
    private final Properties properties;
    
    / *package* / final String getProperty(String key) {
    	return (String) properties.get(key);
    }
    */
    
    
    public Cache(String cacheId, 
String CACHE_KEY_SEPARATOR,
String AUTH_SUCCESS_TIMEOUT_UNIT, int AUTH_SUCCESS_TIMEOUT_DURATION,
String AUTH_FAILURE_TIMEOUT_UNIT, int AUTH_FAILURE_TIMEOUT_DURATION,  
String AUTH_EXCEPTION_TIMEOUT_UNIT, int AUTH_EXCEPTION_TIMEOUT_DURATION,    
CacheElementPopulator cacheElementPopulator
//, Attributor attributor
//, Properties properties
) {
    	this.cacheId = cacheId;
    	this.CACHE_KEY_SEPARATOR = CACHE_KEY_SEPARATOR;
    	this.AUTH_SUCCESS_TIMEOUT_UNIT = AUTH_SUCCESS_TIMEOUT_UNIT; 
    	this.AUTH_SUCCESS_TIMEOUT_DURATION = AUTH_SUCCESS_TIMEOUT_DURATION;
    	this.AUTH_FAILURE_TIMEOUT_UNIT = AUTH_FAILURE_TIMEOUT_UNIT; 
    	this.AUTH_FAILURE_TIMEOUT_DURATION = AUTH_FAILURE_TIMEOUT_DURATION;  
    	this.AUTH_EXCEPTION_TIMEOUT_UNIT = AUTH_EXCEPTION_TIMEOUT_UNIT; 
    	this.AUTH_EXCEPTION_TIMEOUT_DURATION = AUTH_EXCEPTION_TIMEOUT_DURATION;    
    	this.cacheElementPopulator = cacheElementPopulator;
    	//this.attributor = attributor;
    	//this.properties = properties;
    }

	private final Map cache = new Hashtable();	
	
	public final void audit(String userid) {
		String key = getKey(userid/*, password, getCacheKeySeparator()*/);
		CacheElement cacheElement  = getCacheElement(userid);
		if (cacheElement == null) {
			log.debug("cache element is null for " + userid);
		} else {
			cacheElement.audit();
		}
	}
	
	private static final String getKey(String userid /*, String password, String cacheKeySeparator*/) {
		return userid /*+ cacheKeySeparator + password*/;
	}

	/* synchronize so that each access gets the same item instance 
	(protect against overlapping calls)
	note that expiration logic of cache element changes the element's state -- elements are never removed from cache or replaced
	*/
	private final synchronized CacheElement getCacheElement(String userid /*, String password*/)  {
		CacheElement cacheElement = null;
		String keytemp = getKey(userid/*,password,CACHE_KEY_SEPARATOR*/);
		Integer key = new Integer(keytemp.hashCode());
		if (! cache.containsKey(key)) {
			log.debug("CREATING CACHE ELEMENT FOR KEY==" + key);
			CacheElement itemtemp = new CacheElement(userid, /*password,*/ this);
			cache.put(key,itemtemp);		
		}	
		cacheElement = (CacheElement) cache.get(key);
		return cacheElement;
	}
	
	public final Boolean authenticate(CacheElementPopulator authenticator, String userid, String password) throws Throwable{
		log.debug("cache.authenticate() called");
		CacheElement cacheElement = getCacheElement(userid /*, password*/);
		log.debug(this.getClass().getName() + ".authenticate() cacheElement==" + cacheElement);
		
		Boolean authenticated = null;
		try {
			authenticated = cacheElement.authenticate(this, password);
		} catch (Throwable t) {
			log.fatal(this.getClass().getName() + ".authenticate() catch");
			log.fatal(this.getClass().getName() + t.getMessage());
			log.fatal(this.getClass().getName() + ((t.getCause() == null) ? "" : t.getCause().getMessage()));

			throw t;
		}
		log.debug(this.getClass().getName() + ".authenticated() returning==" + authenticated);

		return authenticated;
	}
	
	public final Set getPredicates(CacheElementPopulator authenticator, String userid, String password) throws Throwable{
		log.debug("cache.getPredicates() called");
		CacheElement cacheElement = getCacheElement(userid /*, password*/);
		log.debug(this.getClass().getName() + ".cacheElement==" + cacheElement);
		Set predicates = null;
		try {
			predicates = cacheElement.getPredicates(this, password);
		} catch (Throwable t) {
			log.fatal(this.getClass().getName() + ".authenticate");
			log.fatal(this.getClass().getName() + t.getMessage());
			log.fatal(this.getClass().getName() + ((t.getCause() == null) ? "" : t.getCause().getMessage()));

			throw t;
		}
		log.debug(this.getClass().getName() + ".authenticated==" + predicates);

		return predicates;
	}	

	public final Map getNamedValues(CacheElementPopulator authenticator, String userid, String password) throws Throwable{
		log.debug("cache.getNamedValues() called");
		CacheElement cacheElement = getCacheElement(userid /*, password*/);
		log.debug(this.getClass().getName() + ".cacheElement==" + cacheElement);
		Map namedValues = null;
		try {
			namedValues = cacheElement.getNamedValues(this, password);
		} catch (Throwable t) {
			log.fatal(this.getClass().getName() + ".authenticate");
			log.fatal(this.getClass().getName() + t.getMessage());
			log.fatal(this.getClass().getName() + ((t.getCause() == null) ? "" : t.getCause().getMessage()));

			throw t;
		}
		log.debug(this.getClass().getName() + ".authenticated==" + namedValues);

		return namedValues;
	}	
	
	
}
