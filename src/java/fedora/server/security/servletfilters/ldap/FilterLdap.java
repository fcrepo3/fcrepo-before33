package fedora.server.security.servletfilters.ldap;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.Cookie;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import fedora.server.security.servletfilters.AuthFilter4Container;
import fedora.server.security.servletfilters.BaseCaching;
import fedora.server.security.servletfilters.ExtendedHttpServletRequestWrapper;
//import fedora.server.security.servletfilters.IAttributePrincipal;
//import fedora.server.security.servletfilters.ICompletePrincipal;

import javax.naming.Context;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.DirContext;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;

import fedora.server.security.servletfilters.CacheElement;
/** 
 *  @author Bill Niebel (niebel@virginia.edu)
 */
public class FilterLdap extends BaseCaching {
    protected static Log log = LogFactory.getLog(FilterLdap.class);
    
    /*
    protected String getClassName() {
    	return this.getClass().getName();
    }
    */

	public static final String URL_KEY = "url";   
	public static final String BASE_KEY = "search-base";   
	public static final String FILTER_KEY = "search-filter";
	public static final String USERID_KEY = "id-attribute";   
	public static final String PASSWORD_KEY = "password-attribute";
	//public static final String ROLES2RETURN_KEY = "directory-attributes-as-roles";
	public static final String ATTRIBUTES2RETURN_KEY = "attributes";
	public static final String GROUPS_NAME_KEY = "attributes-common-name";
	
	public static final String SECURITY_AUTHENTICATION_KEY = "security-authentication";
	public static final String SECURITY_PRINCIPAL_KEY = "security-principal";
	public static final String SECURITY_CREDENTIALS_KEY = "security-credentials";

	private String[] DIRECTORY_ATTRIBUTES_NEEDED = null;
	
	private String URL = "";
	private String BASE = "";
	private String USERID = "";
	private String FILTER = "";
	private String PASSWORD = "";
	//private String[] ROLES2RETURN = null;
	private String[] ATTRIBUTES2RETURN = null;
	private String GROUPS_NAME = null;
	
	public String SECURITY_AUTHENTICATION = "none";
	public String SECURITY_PRINCIPAL = null;
	public String SECURITY_CREDENTIALS = null;
	
	public void init(FilterConfig filterConfig) {
		String method = "init()";
		try {
        	if (log.isDebugEnabled()) log.debug(enter(method));
			super.init(filterConfig);
			Set temp = new HashSet();
			/*
			if (ROLES2RETURN == null) {
				ROLES2RETURN = new String[0];
			} else {
				for (int i = 0; i < ROLES2RETURN.length; i++) {
					temp.add(ROLES2RETURN[i]);
				}				
			}
			*/
			if (ATTRIBUTES2RETURN == null) {
				ATTRIBUTES2RETURN = new String[0];
			} else {
				for (int i = 0; i < ATTRIBUTES2RETURN.length; i++) {
					temp.add(ATTRIBUTES2RETURN[i]);
				}				
			}
    		if (AUTHENTICATE && (PASSWORD != null) && ! "".equals(PASSWORD)) {
				temp.add(PASSWORD);
    		}
			/*
			if (GROUPS2RETURN == null) {
				GROUPS2RETURN = new String[0];
			} else {
				for (int i = 0; i < GROUPS2RETURN.length; i++) {
					temp.add(GROUPS2RETURN[i]);
				}							
			}
			*/
			DIRECTORY_ATTRIBUTES_NEEDED = (String[]) temp.toArray(StringArrayPrototype);
		} finally {
			if (log.isDebugEnabled()) log.debug(exit(method));
		}
	}
	
	public void destroy() {
		String method = "destroy()";
		try {
			if (log.isDebugEnabled()) log.debug(enter(method));
			super.destroy();
		} finally {
			if (log.isDebugEnabled()) log.debug(exit(method));
		}
    }

	protected void initThisSubclass(String key, String value) {
    	String method = "initThisSubclass()";
    	try {
        	if (log.isDebugEnabled()) log.debug(enter(method));
        	if (log.isDebugEnabled()) log.debug(format(method, key + "==" + value));
    		boolean setLocally = false;
    		if (URL_KEY.equals(key)) {
    			URL = value;
    			setLocally = true;
    		} else if (BASE_KEY.equals(key)) {
    			BASE = value;
    			setLocally = true;
    		} else if (USERID_KEY.equals(key)) {
    			USERID = value;
    			setLocally = true;
    			/*
    		} else if (ROLES2RETURN_KEY.equals(key)) {			
    			if (value.indexOf(",") < 0) {
    				if ("".equals(value)) {
    					ROLES2RETURN = null;
    				} else {
    					ROLES2RETURN = new String[1];
    					ROLES2RETURN[0] = value;
    				}
    			} else {
    				ROLES2RETURN = value.split(",");  							
    			}
    			setLocally = true;
    			*/
    		} else if (ATTRIBUTES2RETURN_KEY.equals(key)) {			
    			if (value.indexOf(",") < 0) {
    				if ("".equals(value)) {
    					ATTRIBUTES2RETURN = null;
    				} else {
    					ATTRIBUTES2RETURN = new String[1];
    					ATTRIBUTES2RETURN[0] = value;
    				}
    			} else {
    				ATTRIBUTES2RETURN = value.split(",");  							
    			}			
    			setLocally = true;
    		} else if (GROUPS_NAME_KEY.equals(key)) {			
    			GROUPS_NAME = value;
    			setLocally = true;    			
    		} else if (FILTER_KEY.equals(key)) {
    			FILTER = value;
    			setLocally = true;
    		} else if (PASSWORD_KEY.equals(key)) {
    			PASSWORD = value;
    			setLocally = true;				
    		} else if (SECURITY_AUTHENTICATION_KEY.equals(key)) {
    			SECURITY_AUTHENTICATION = value;
    			setLocally = true;				
    		} else if (SECURITY_PRINCIPAL_KEY.equals(key)) {
    			SECURITY_PRINCIPAL = value;
    			setLocally = true;				
    		} else if (SECURITY_CREDENTIALS_KEY.equals(key)) {
    			SECURITY_CREDENTIALS = value;
    			setLocally = true;				
        	} else {
            	if (log.isErrorEnabled()) log.error(format(method, "deferring to super"));
        		super.initThisSubclass(key, value);
        	}
    		if (setLocally) {
    			if (log.isInfoEnabled()) log.info(method + "known parameter " + key + "==" + value);										
    		}
    	} finally {
    		if (log.isDebugEnabled()) log.debug(exit(method));    		
    	}
	}
	
	public void populateCacheElement(CacheElement cacheElement, String password) {
    	String method = "populateCacheElement()"; 
    	try {
        	if (log.isDebugEnabled()) log.debug(enter(method));
    		Boolean authenticated = null;
    		//String[] roles = null;
    		Set v = new HashSet();
    		Map m = new Hashtable();
    		
    		Hashtable env = new Hashtable();
    		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
    		if (log.isDebugEnabled()) log.debug(format(method, "IN LDAP and the url is . . ." + URL));
    		env.put(Context.PROVIDER_URL, URL);
    		
    		if (SECURITY_AUTHENTICATION.equals("simple")) {
        		if (log.isDebugEnabled()) log.debug(format(method, "will bind with simple"));
        		env.put(Context.SECURITY_AUTHENTICATION, SECURITY_AUTHENTICATION);
        		if (AUTHENTICATE && ((PASSWORD == null) || "".equals(PASSWORD))) {
            		if (log.isDebugEnabled()) log.debug(format(method, "setting up to bind user"));
            		env.put(Context.SECURITY_PRINCIPAL, cacheElement.getUserid());
            		env.put(Context.SECURITY_CREDENTIALS, password);        			
        		} else {
            		if (log.isDebugEnabled()) log.debug(format(method, "setting up to bind system access"));
            		env.put(Context.SECURITY_PRINCIPAL, SECURITY_PRINCIPAL);
            		env.put(Context.SECURITY_CREDENTIALS, SECURITY_CREDENTIALS);        			
        		}
    		} else {
        		if (log.isDebugEnabled()) log.debug(format(method, "will \"bind\" anonymously"));    			
    		}
    		
    		if (log.isDebugEnabled()) log.debug(format(method, "IN LDAP before newing IDC"));
			DirContext ctx = new InitialDirContext(env);
			if (log.isDebugEnabled()) log.debug(format(method, "IN LDAP afgter newing IDC ctx==" + ctx));
			String filter = new String(FILTER);

			//filter = filter.replaceFirst("{0}", USERID_ATTRIBUTE);
			filter = filter.replaceFirst("\\{0}", cacheElement.getUserid());
			if (log.isDebugEnabled()) log.debug(format(method, "IN LDAP filter becomes " + filter));

			Attributes matchingAttributes = new BasicAttributes();
			if (log.isDebugEnabled()) log.debug(format(method, "IN LDAP afgter newing BAs"));
			Attribute attribute = new BasicAttribute(USERID, cacheElement.getUserid());
			if (log.isDebugEnabled()) log.debug(format(method, "IN LDAP afgter newing BA. USERID_ATTRIBUTE==" + USERID + ", id==" + cacheElement.getUserid()));
			matchingAttributes.put(attribute);			
			if (log.isDebugEnabled()) {
				log.debug(format(method, "IN LDAP afgter upt, SEARCH_ROOT==" + BASE + ", ATTRIBUTES2RETURN==" + ATTRIBUTES2RETURN));
				for (int i = 0; i < ATTRIBUTES2RETURN.length; i++) {
					log.fatal(format(method, "IN LDAP  ATTRIBUTE2RETURN==" + ATTRIBUTES2RETURN[i]));				
				}
			}
		
			int nEntries2return = 0;
			int millisecondTimeLimit = 0;
			boolean retobj = true;
			boolean deref = true;
			SearchControls searchControls = new SearchControls(SearchControls.SUBTREE_SCOPE,
					nEntries2return,
					millisecondTimeLimit,
					DIRECTORY_ATTRIBUTES_NEEDED,
                    retobj,
                    deref);
			
			
			
			//NamingEnumeration ne = ctx.search(SEARCH_ROOT, matchingAttributes, ATTRIBUTES2RETURN);

    		if (AUTHENTICATE && SECURITY_AUTHENTICATION.equals("simple") && ((PASSWORD == null) || "".equals(PASSWORD))) {
        		if (log.isDebugEnabled()) log.debug(format(method, "before authenticating search thru bind, tentatively marking unauthenticated"));
    			authenticated = Boolean.FALSE; 
    			//bind has been set up above
    		}
			
			NamingEnumeration ne = ctx.search(BASE, filter, searchControls);
			
    		if (AUTHENTICATE && SECURITY_AUTHENTICATION.equals("simple") && ((PASSWORD == null) || "".equals(PASSWORD))) {
        		if (log.isDebugEnabled()) log.debug(format(method, "after authenticating search thru bind, marking authenticated as no exceptions (correct to do this?)"));
    			authenticated = Boolean.TRUE;    			
    		}
			
			if (log.isDebugEnabled()) log.debug(format(method, "IN LDAP afgter search"));
			while ( ne.hasMoreElements() ) {
				if (log.isDebugEnabled()) log.debug(format(method, "IN LDAP another"));
				Object item = ne.nextElement();
				if (log.isDebugEnabled()) log.debug(format(method, "LDAP ADDING itemclass==" + item.getClass().getName()));
				if (item instanceof SearchResult) {	
					SearchResult s = (SearchResult) item;
					Attributes attributes = s.getAttributes();
					/* if (ATTRIBUTES2RETURN4ATTRIBUTES == null) {
						NamingEnumeration nee = attributes.getAll();
						while ( nee.hasMoreElements() ) {
							log.fatal(this.getClass().getName() + "IN LDAP another inner item");
							Object itemm = nee.nextElement();
							if (itemm instanceof Attribute) {
								Attribute attributee = (Attribute) itemm;
								log.fatal(this.getClass().getName() + "IN LDAP another inner attribute, id=="+attributee.getID()+", value=="+attributee.get());
								m.put(attributee.getID(), attributee.get());
							}
						}
					} else { */
					
			    		if (AUTHENTICATE && (PASSWORD != null) && ! "".equals(PASSWORD)) {
		            		if (log.isDebugEnabled()) log.debug(format(method, "setting up to authenticate user \"manually\""));
							Attribute attributee = attributes.get(PASSWORD);
							int size = attributee.size();
							for (int j = 0; j < size; j++) {
								Object o = attributee.get(j);
								if (log.isDebugEnabled()) log.debug(format(method, "another ldap role==" + o.getClass().getName() + " " + o));
								if (password.equals(o)) {
				            		if (log.isDebugEnabled()) log.debug(format(method, "manually authenticated"));
									authenticated = Boolean.TRUE;
								} else {
				            		if (log.isDebugEnabled()) log.debug(format(method, "manually unauthenticated"));
									authenticated = Boolean.FALSE;									
								}
							}
			    		}
			    		
			    		if (AUTHENTICATE && ! Boolean.TRUE.equals(authenticated)) {
		            		if (log.isDebugEnabled()) log.debug(format(method, "don't get attributes if authentication failed"));			    			
			    		} else {
		            		if (log.isDebugEnabled()) log.debug(format(method, "get roles/attributes (authentication succeeded or wasn't configured)"));
		            		/*
							for (int i = 0; i < ROLES2RETURN.length; i++) {
								Attribute attributee = attributes.get(ROLES2RETURN[i]);
								int size = attributee.size();
								for (int j = 0; j < size; j++) {
									Object o = attributee.get(j);
									if (log.isDebugEnabled()) log.debug(format(method, "another ldap role==" + o.getClass().getName() + " " + o));
									if (! v.contains(o)) {
										v.add(o);
									}
								}
							}
							*/
							for (int i = 0; i < ATTRIBUTES2RETURN.length; i++) {
								String key = ATTRIBUTES2RETURN[i];
								Attribute attributee = attributes.get(key);
								if ((GROUPS_NAME != null) && ! "".equals(GROUPS_NAME)) {
									key = GROUPS_NAME;
								}
								Set x;
								if (m.containsKey(key)) {
									x = (Set) m.get(key);
								} else {
									x = new HashSet();
									m.put(key, x);
								}
								int size = attributee.size();
								for (int j = 0; j < size; j++) {
									Object o = attributee.get(j);
									x.add(o);
						    		if (log.isDebugEnabled()) log.debug(format(method, "another ldap attr==" + o.getClass().getName() + " " + o));
								}
							}			    				
			    		}
					
					//}
				}
				//v.add(item.toString());
				//log.fatal(this.getClass().getName() + "LDAP ADDING item==" + item.toString());
			}
			/*
			roles = new String[v.size()];
			for (int i = 0; i < v.size(); i++) {
				roles[i] = (String) v.get(i);
			}
			*/
    		cacheElement.populate(authenticated, null, m, null);
		} catch (Throwable th) {
			showThrowable(th, log, "ldap filter failure");
    	} finally {
    		if (log.isDebugEnabled()) log.debug(exit(method));
    	}
    	
	}
}
