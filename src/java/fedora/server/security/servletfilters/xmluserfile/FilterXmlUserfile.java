package fedora.server.security.servletfilters.xmluserfile;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Hashtable;
import java.util.Set;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Cookie;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fedora.server.security.servletfilters.Base;
//import fedora.server.security.servletfilters.AuthFilter4Container;
import fedora.server.security.servletfilters.FinishedParsingException;
//import fedora.server.security.servletfilters.AttributePrincipal;
import fedora.server.security.servletfilters.BaseCaching;
import fedora.server.security.servletfilters.CacheElement;
import fedora.server.security.servletfilters.ExtendedHttpServletRequest;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException; 
import java.util.Iterator;
import java.util.HashSet;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/** 
 *  @author Bill Niebel (niebel@virginia.edu)
 */
public class FilterXmlUserfile extends BaseCaching {
    protected static Log log = LogFactory.getLog(FilterXmlUserfile.class);
    /*
    static {
    	System.err.print(AuthFilter4TomcatUsers.class.getName() + " logging includes ");
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


    private static final String FILEBASE_KEY = "filebase";
    private String FILEBASE = "";
    
    private static final String FILEPATH_KEY = "filepath";
    private String FILEPATH = "";
    
    private final String getFilepath() {
    	String filepath = null;
    	if ((FILEBASE == null) || "".equals(FILEBASE)) {
    		filepath = FILEPATH;
    	} else {
    		filepath = System.getProperty(FILEBASE) + FILEPATH;
    	}
    	return filepath;
    }
    
    
	public void destroy() {
		String method = "destroy()"; if (log.isDebugEnabled()) log.debug(enter(method));
		super.destroy();
		if (log.isDebugEnabled()) log.debug(exit(method));
    }

    
	protected void initThisSubclass(String key, String value) {
    	String method = "initThisSubclass()"; if (log.isDebugEnabled()) log.debug(enter(method));
		boolean setLocally = false;
		
    	if (FILEPATH_KEY.equals(key)) {
    		FILEPATH = value; 
        	setLocally = true;
    	} else if (FILEBASE_KEY.equals(key)) {
    		FILEBASE = value; 
        	setLocally = true;        	
    	} else {
        	if (log.isErrorEnabled()) log.error(format(method, "deferring to super"));
    		super.initThisSubclass(key, value);
    	}
		if (setLocally) {
			if (log.isInfoEnabled()) log.info(method + "known parameter " + key + "==" + value);										
		}
		if (log.isDebugEnabled()) log.debug(exit(method));
	}


	public void populateCacheElement(CacheElement cacheElement, String password) {
    	String method = "populateCacheElement()"; if (log.isDebugEnabled()) log.debug(enter(method));
		Boolean authenticated = null;
		Set roles = null; 
		Map namedAttributes = null;
		String errorMessage = null;
		authenticated = Boolean.FALSE;
 
		try {
			InputStream is;				
			try {
				is = new FileInputStream(getFilepath());
			} catch (Throwable th) {
				showThrowable(th, log, "error reading tomcat users file " + getFilepath());
				throw th;
			}
			if (log.isDebugEnabled()) log.debug("read tomcat-users.xml");

			ParserXmlUserfile parser = new ParserXmlUserfile(is);
			if (log.isDebugEnabled()) log.debug("got parser");
			try {
				parser.parse(cacheElement.getUserid(),password);
				if (log.isDebugEnabled()) log.debug("back from parsing");
			} catch (FinishedParsingException f) {
				if (log.isDebugEnabled()) log.debug(format(method,"got finished parsing exception"));				
			} catch (Throwable th) {
				String msg = "error parsing tomcat users file";
				showThrowable(th, log, msg);
				throw new IOException(msg);
			}
			authenticated = parser.getAuthenticated();
			roles = parser.getUserRoles();
			namedAttributes = parser.getNamedAttributes();
		} catch (Throwable t) {
			authenticated = null;
			roles = null; 
			namedAttributes = null;
		}
		if (log.isDebugEnabled()) {
			log.debug(format(method, null, "authenticated"));
			log.debug(authenticated);
			log.debug(format(method, null, "roles"));
			log.debug(roles);
			log.debug(format(method, null, "namedAttributes"));
			log.debug(namedAttributes);
			log.debug(format(method, null, "errorMessage", errorMessage));
		}
		cacheElement.populate(authenticated, roles, namedAttributes, errorMessage);
		 if (log.isDebugEnabled()) log.debug(exit(method));
	}

	
}
