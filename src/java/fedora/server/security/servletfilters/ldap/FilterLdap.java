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


	public static final String URL4ATTRIBUTES_KEY = "url4attributes";   
	public static final String BASE4ATTRIBUTES_KEY = "base4attributes";   
	public static final String USERID4ATTRIBUTES_KEY = "userid4attributes";   
	public static final String FILTER4ATTRIBUTES_KEY = "filter4attributes";
	public static final String PASSWORD4ATTRIBUTES_KEY = "password4attributes";
	public static final String ROLES2RETURN4ATTRIBUTES_KEY = "roles2return4attributes";
	public static final String ATTRIBUTES2RETURN4ATTRIBUTES_KEY = "attributes2return4attributes";

	public static final String URL4GROUPS_KEY = "url4groups";   
	public static final String BASE4GROUPS_KEY = "base4groups";   
	public static final String USERID4GROUPS_KEY = "userid4groups";   
	public static final String FILTER4GROUPS_KEY = "filter4groups";
	public static final String GROUPS2RETURN4GROUPS_KEY = "groups2return4groups";

	private String[] DIRECTORY_ATTRIBUTES_NEEDED = null;
	
	private String URL4ATTRIBUTES = "";
	private String BASE4ATTRIBUTES = "";
	private String USERID4ATTRIBUTES = "";
	private String FILTER4ATTRIBUTES = "";
	private String PASSWORD4ATTRIBUTES = "";
	private String[] ROLES2RETURN4ATTRIBUTES = null;
	private String[] ATTRIBUTES2RETURN4ATTRIBUTES = null;

	private String URL4GROUPS = "";
	private String BASE4GROUPS = "";
	private String USERID4GROUPS = "";
	private String FILTER4GROUPS = "";
	private String[] GROUPS2RETURN4GROUPS = null;
	
	public void init(FilterConfig filterConfig) {
		String method = "init()";
		try {
        	if (log.isDebugEnabled()) log.debug(enter(method));
			super.init(filterConfig);
			Set temp = new HashSet();
			for (int i = 0; i < ROLES2RETURN4ATTRIBUTES.length; i++) {
				temp.add(ROLES2RETURN4ATTRIBUTES[i]);
			}
			for (int i = 0; i < ATTRIBUTES2RETURN4ATTRIBUTES.length; i++) {
				temp.add(ATTRIBUTES2RETURN4ATTRIBUTES[i]);
			}
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
    		if (URL4ATTRIBUTES_KEY.equals(key)) {
    			URL4ATTRIBUTES = value;
    			setLocally = true;
    		} else if (BASE4ATTRIBUTES_KEY.equals(key)) {
    			BASE4ATTRIBUTES = value;
    			setLocally = true;
    		} else if (USERID4ATTRIBUTES_KEY.equals(key)) {
    			USERID4ATTRIBUTES = value;
    			setLocally = true;
    		} else if (ROLES2RETURN4ATTRIBUTES_KEY.equals(key)) {			
    			if (value.indexOf(",") < 0) {
    				if ("".equals(value)) {
    					ROLES2RETURN4ATTRIBUTES = null;
    				} else {
    					ROLES2RETURN4ATTRIBUTES = new String[1];
    					ROLES2RETURN4ATTRIBUTES[0] = value;
    				}
    			} else {
    				ROLES2RETURN4ATTRIBUTES = value.split(",");  							
    			}
    			setLocally = true;
    		} else if (ATTRIBUTES2RETURN4ATTRIBUTES_KEY.equals(key)) {			
    			if (value.indexOf(",") < 0) {
    				if ("".equals(value)) {
    					ATTRIBUTES2RETURN4ATTRIBUTES = null;
    				} else {
    					ATTRIBUTES2RETURN4ATTRIBUTES = new String[1];
    					ATTRIBUTES2RETURN4ATTRIBUTES[0] = value;
    				}
    			} else {
    				ATTRIBUTES2RETURN4ATTRIBUTES = value.split(",");  							
    			}			
    			setLocally = true;
    		} else if (FILTER4ATTRIBUTES_KEY.equals(key)) {
    			FILTER4ATTRIBUTES = value;
    			setLocally = true;
    		} else if (PASSWORD4ATTRIBUTES_KEY.equals(key)) {
    			PASSWORD4ATTRIBUTES = value;
    			setLocally = true;			
    		} else if (URL4GROUPS_KEY.equals(key)) {
    			URL4GROUPS = value;
    			setLocally = true;
    		} else if (BASE4GROUPS_KEY.equals(key)) {
    			BASE4GROUPS = value;
    			setLocally = true;
    		} else if (USERID4GROUPS_KEY.equals(key)) {
    			USERID4GROUPS = value;
    			setLocally = true;
    		} else if (GROUPS2RETURN4GROUPS_KEY.equals(key)) {			
    			if (value.indexOf(",") < 0) {
    				if ("".equals(value)) {
    					GROUPS2RETURN4GROUPS = null;
    				} else {
    					GROUPS2RETURN4GROUPS = new String[1];
    					GROUPS2RETURN4GROUPS[0] = value;
    				}
    			} else {
    				GROUPS2RETURN4GROUPS = value.split(",");  							
    			}
    			setLocally = true;
    		} else if (FILTER4GROUPS_KEY.equals(key)) {
    			FILTER4GROUPS = value;    
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
    		if (log.isDebugEnabled()) log.debug(format(method, "IN LDAP and the url is . . ." + URL4ATTRIBUTES));
    		env.put(Context.PROVIDER_URL, URL4ATTRIBUTES);
    		
    		if (log.isDebugEnabled()) log.debug(format(method, "IN LDAP before newing IDC"));
			DirContext ctx = new InitialDirContext(env);
			if (log.isDebugEnabled()) log.debug(format(method, "IN LDAP afgter newing IDC ctx==" + ctx));
			String filter = new String(FILTER4ATTRIBUTES);

			//filter = filter.replaceFirst("{0}", USERID_ATTRIBUTE);
			filter = filter.replaceFirst("\\{0}", cacheElement.getUserid());
			if (log.isDebugEnabled()) log.debug(format(method, "IN LDAP filter becomes " + filter));

			Attributes matchingAttributes = new BasicAttributes();
			if (log.isDebugEnabled()) log.debug(format(method, "IN LDAP afgter newing BAs"));
			Attribute attribute = new BasicAttribute(USERID4ATTRIBUTES, cacheElement.getUserid());
			if (log.isDebugEnabled()) log.debug(format(method, "IN LDAP afgter newing BA. USERID_ATTRIBUTE==" + USERID4ATTRIBUTES + ", id==" + cacheElement.getUserid()));
			matchingAttributes.put(attribute);			
			if (log.isDebugEnabled()) {
				log.debug(format(method, "IN LDAP afgter upt, SEARCH_ROOT==" + BASE4ATTRIBUTES + ", ATTRIBUTES2RETURN==" + ATTRIBUTES2RETURN4ATTRIBUTES));
				for (int i = 0; i < ATTRIBUTES2RETURN4ATTRIBUTES.length; i++) {
					log.fatal(format(method, "IN LDAP  ATTRIBUTE2RETURN==" + ATTRIBUTES2RETURN4ATTRIBUTES[i]));				
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
			
			NamingEnumeration ne = ctx.search(BASE4ATTRIBUTES, filter, searchControls);
			
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
						for (int i = 0; i < ROLES2RETURN4ATTRIBUTES.length; i++) {
							Attribute attributee = attributes.get(ROLES2RETURN4ATTRIBUTES[i]);
							int size = attributee.size();
							for (int j = 0; j < size; j++) {
								Object o = attributee.get(j);
								if (log.isDebugEnabled()) log.debug(format(method, "another ldap role==" + o.getClass().getName() + " " + o));
								if (! v.contains(o)) {
									v.add(o);
								}
							}
						}
						for (int i = 0; i < ATTRIBUTES2RETURN4ATTRIBUTES.length; i++) {
							String key = ATTRIBUTES2RETURN4ATTRIBUTES[i];
							Attribute attributee = attributes.get(key);
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
    		cacheElement.populate(authenticated, v, m, null);
		} catch (Throwable th) {
			showThrowable(th, log, "ldap filter failure");
    	} finally {
    		if (log.isDebugEnabled()) log.debug(exit(method));
    	}
    	
	}
}
