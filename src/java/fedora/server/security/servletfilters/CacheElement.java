package fedora.server.security.servletfilters;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Calendar;


/** 
 *  @author Bill Niebel (niebel@virginia.edu)
 */
public class CacheElement {
	
    private Log log = LogFactory.getLog(CacheElement.class);
	
	private final String userid;
	
	//private final String password;

	private Boolean authenticated = null;
	private Set roles = null;
	private Map namedValues = null;
	
	private String errorMessage = null;
	
	public final void audit() {
		System.err.println("userid==" + userid);
		//System.err.println("password==" + password);
		System.err.println("authenticated==" + authenticated);
		System.err.print("roles==");
		for (Iterator it = roles.iterator(); it.hasNext(); ) {
			Object temp = it.next();
			if (! (temp instanceof String)) {
				System.err.print(">>>>> ROLE IS NOT STRING <<<<<");				
			} else {
				String role = (String) temp;
				System.err.print(role + ",");
			}
		}
		System.err.println();
		System.err.println("namedAttributes==");
		for (Iterator it1 = namedValues.keySet().iterator(); it1.hasNext(); ) {
			Object temp = it1.next();
			if (! (temp instanceof String)) {
				System.err.print(">>>>> NAME IS NOT STRING <<<<<");				
			} else {
				String name = (String) temp;
				System.err.print(name + "==");
				temp = namedValues.get(name);
				if (temp instanceof String) {
					System.err.print(temp);				
				} else if (temp instanceof Set) {
					Set values = (Set) temp;
					System.err.println("in audit set n==" + values.size());
					for (Iterator it2 = values.iterator(); it2.hasNext(); ) {
						temp = it2.next();
						if (! (temp instanceof String)) {
							System.err.print(">>>>> VALUE IS NOT STRING <<<<<");				
						} else {
							String value = (String) temp;
							System.err.print(value + ",");
						}
					}
				} else {
					System.err.print(">>>>> VALUES IS NEITHER STRING NOR SET <<<<<");
				}
			}
		}
		System.err.println();
	}
	
	public final HashSet NULL_SET =  new HashSet(); //<<<<<<<<<<<<<<new ImmutableHashSet();
	public final Hashtable EMPTY_MAP = new Hashtable(); //<<<<<<<<<<<<<<
	
	public final void populate(Boolean authenticated, Set predicates, Map namedValues, String errorMessage) {
		this.errorMessage = errorMessage;
		System.err.println("in ce.pop");
		if (errorMessage == null) {
			this.authenticated = authenticated;
			if (predicates == null) {
				this.roles = NULL_SET;
			} else {
				this.roles = predicates;
			}
			if (namedValues == null) {
				System.err.println("in ce.pop(), namedValues is null");
				this.namedValues = EMPTY_MAP;
			} else {
				System.err.println("in ce.pop(), namedValues is not null " + namedValues);
				this.namedValues = namedValues;
			}
			audit();
		} else {
			System.err.println(errorMessage);
		}
	}
	
	private Calendar expiration = null;	
	
	public CacheElement(String userid, /*String password,*/ Cache cache) {
		this.userid = userid;
		//this.password = password;
		/*
		this.cache = cache;
		try {
			log.fatal("AUTHENTICATOR_IMPLEMENTATION==" + authentication);
			Class authenticatorClass = Class.forName(cache.getAuthenticatorImplementation());			
			log.fatal("authenticatorClass==" + authenticatorClass);
			authenticator = (Authenticator) authenticatorClass.newInstance();
			authenticator.setProperties(properties);
			log.fatal("authenticator==" + authenticator);
		} catch (Throwable t) {
			log.fatal(this.getClass().getName() + ".NetvouchCacheElement() " + "couldn't construct authenticator instance "
					+ t.getMessage() + ((t.getCause() == null) ? "" : t.getCause().getMessage()) 
					);			
		}
		*/
	}
	
	public String getUserid() {
		return userid;
	}
	/*
	public String getPassword() {
		return password;
	}
	*/
	private static final Calendar getExpiration(int duration, String unit) {
		Calendar expiration = Calendar.getInstance();
		if ("second".equalsIgnoreCase(unit) || "seconds".equalsIgnoreCase(unit)) {
			expiration.add(Calendar.SECOND, duration);
		} else if ("minute".equalsIgnoreCase(unit) || "minutes".equalsIgnoreCase(unit)) {
			expiration.add(Calendar.MINUTE, duration);			
		} else if ("hour".equalsIgnoreCase(unit) || "hours".equalsIgnoreCase(unit)) {
			expiration.add(Calendar.HOUR, duration);			
		} else {
			//properties read failure
			LogFactory.getLog(CacheElement.class).fatal(CacheElement.class.getName() + ".getExpiration() " + "bad expiration properties");			
			expiration.add(Calendar.YEAR, -666);			
		}
		return expiration;
	}

	/* synchronize so evaluation of cache item state will be sequential, non-interlaced
	(protect against overlapping calls resulting in redundant authenticator calls)
	*/
	public final synchronized Boolean authenticate(Cache cache, String password) {
		CacheElementPopulator cacheElementPopulator = cache.getCacheElementPopulator();
		Calendar now = Calendar.getInstance();
		log.fatal(this.getClass().getName() + ".authenticate");
		if ((expiration != null) && now.before(expiration)) {
			log.fatal(this.getClass().getName() + " cache valid");
			//previous authentication is still set and valid 
		} else { //previous authentication has timed out (or no previous authentication)
			log.fatal(this.getClass().getName() + " new authentication needed");
			cacheElementPopulator.populateCacheElement(this, password);
			if (authenticated == null) {
				log.fatal(this.getClass().getName() + " couldn't authenticate");
				expiration = getExpiration(cache.getAuthExceptionTimeoutDuration(), cache.getAuthExceptionTimeoutUnit());				
			} else if (((Boolean)authenticated).booleanValue()) {
				log.fatal(this.getClass().getName() + " authentication succeeded");
				expiration = getExpiration(cache.getAuthSuccessTimeoutDuration(), cache.getAuthSuccessTimeoutUnit());				
			} else {
				log.fatal(this.getClass().getName() + " authentication failed");
				expiration = getExpiration(cache.getAuthFailureTimeoutDuration(), cache.getAuthFailureTimeoutUnit());				
			}
		}
		return authenticated;
	}
	
	/* synchronize so evaluation of cache item state will be sequential, non-interlaced
	(protect against overlapping calls resulting in redundant authenticator calls)
	*/
	public final synchronized Set getPredicates(Cache cache, String password) {
		CacheElementPopulator cacheElementPopulator = cache.getCacheElementPopulator();
		Calendar now = Calendar.getInstance();
		log.fatal(this.getClass().getName() + ".getPredicates()");
		if ((expiration != null) && now.before(expiration)) {
			log.fatal(this.getClass().getName() + " cache valid"); 
		} else { //previous X has timed out (or no previous X)
			log.fatal(this.getClass().getName() + " new X needed");
			roles = null;
			try {
				cacheElementPopulator.populateCacheElement(this, password);
			} catch (Throwable t) {
			}
		}
		return roles;
	}	
	
	
	/* synchronize so evaluation of cache item state will be sequential, non-interlaced
	(protect against overlapping calls resulting in redundant authenticator calls)
	*/
	public final synchronized Map getNamedValues(Cache cache, String password) {
		CacheElementPopulator cacheElementPopulator = cache.getCacheElementPopulator();
		Calendar now = Calendar.getInstance();
		log.fatal(this.getClass().getName() + ".getNamedValues()");
		if ((expiration != null) && now.before(expiration)) {
			log.fatal(this.getClass().getName() + " cache valid"); 
		} else { //previous X has timed out (or no previous X)
			log.fatal(this.getClass().getName() + " new X needed");
			namedValues = null;
			try {
				cacheElementPopulator.populateCacheElement(this, password);
				if (namedValues == null) {
					namedValues = EMPTY_MAP;
				}
			} catch (Throwable t) {
			}
		}
		return namedValues;
	}		

}
