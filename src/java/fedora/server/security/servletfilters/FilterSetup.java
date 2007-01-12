package fedora.server.security.servletfilters;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Enumeration;
import java.util.Map;
import java.util.Hashtable;
import java.util.Hashtable;
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
public class FilterSetup extends Base implements Filter {
    protected static Log log = LogFactory.getLog(FilterSetup.class);
    
    /*
    protected String getClassName() {
    	return this.getClass().getName();
    }
    */

    
    protected static final String NOT_SET = "NOT SET";
    protected String FILTER_NAME = NOT_SET;
  	protected boolean inited = false;
    
    public void init(FilterConfig filterConfig) {
    	String method = "init() "; if (log.isDebugEnabled()) log.debug(enter(method));
    	inited = false;
    	initErrors = false;
		if ( filterConfig != null ) {
			FILTER_NAME = filterConfig.getFilterName();
			if ((FILTER_NAME == null) || "".equals(FILTER_NAME)) {
				if (log.isErrorEnabled()) log.error(format(method, "FILTER_NAME not set"));
			} else {
				if (log.isDebugEnabled()) log.debug(format(method, null, "FILTER_NAME", FILTER_NAME));
		        Enumeration enumer = filterConfig.getInitParameterNames();
		        while (enumer.hasMoreElements()) {
		        	String key = (String) enumer.nextElement();
		        	String value = (String) filterConfig.getInitParameter(key);
		        	initThisSubclass(key, value);
		        }
		        inited = true;
			}
		}
		if (log.isDebugEnabled()) log.debug(exit(method)); 
    }
    
	public void destroy() {
		String method = "destroy()"; if (log.isDebugEnabled()) log.debug(enter(method));
		if (log.isDebugEnabled()) log.debug(exit(method));
    }
    
    
    
    protected void initThisSubclass(String key, String value) {
    	log.debug("AF.iTS");
    	String method = "initThisSubclass() "; if (log.isDebugEnabled()) log.debug(enter(method));
   		super.initThisSubclass(key, value);
		if (log.isDebugEnabled()) log.debug(exit(method)); 
	}
    
	public ExtendedHttpServletRequest wrap(HttpServletRequest httpServletRequest) throws Exception {
		String method = "wrap() "; if (log.isDebugEnabled()) log.debug(enter(method));
		ExtendedHttpServletRequestWrapper wrap = new ExtendedHttpServletRequestWrapper(httpServletRequest);
		if (log.isDebugEnabled()) log.debug(exit(method)); 
		return wrap;
	}
	
	public void doThisSubclass(ExtendedHttpServletRequest extendedHttpServletRequest, HttpServletResponse response) throws Throwable {
		String method = "doThisSubclass() "; if (log.isDebugEnabled()) log.debug(enter(method));
		String test = null;

		test = "init";
		if ((! inited) || initErrors) {
			if (log.isErrorEnabled()) log.error("inited==" + inited);
			if (log.isErrorEnabled()) log.error("initErrors==" + initErrors);
			String msg = fail(method,test);
			if (log.isErrorEnabled()) log.error(msg);
			throw new Exception(msg);
		}
		if (log.isDebugEnabled()) log.debug(pass(method,test));								
		
		test = "HttpServletRequest";
		if (! (extendedHttpServletRequest instanceof HttpServletRequest) ) {
			String msg = fail(method,test);
			if (log.isErrorEnabled()) log.error(msg);
			throw new Exception(msg);
		}
		if (log.isDebugEnabled()) log.debug(pass(method,test));								

		if (log.isDebugEnabled()) log.debug(exit(method)); 
	}
		
    
	public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain) {
		String method = "doFilter() "; if (log.isDebugEnabled()) log.debug(enter(method));
		if (log.isDebugEnabled()) log.debug(format(method, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"));
		if (log.isDebugEnabled()) log.debug(format(method, "FILTER_NAME", FILTER_NAME));
		String test = null;

		ExtendedHttpServletRequest extendedHttpServletRequest = null;
		try {
			
			//only one filter should wrap
			if (servletRequest instanceof ExtendedHttpServletRequest) {
				log.debug(format(method, "using existing request..."));								
				extendedHttpServletRequest = (ExtendedHttpServletRequest) servletRequest;				
			} else {
				if (log.isDebugEnabled()) log.debug(format(method, "wrapping request..."));
				extendedHttpServletRequest = wrap((HttpServletRequest)servletRequest);				
			}
			
			test = "HttpServletResponse";
			if (! (response instanceof HttpServletResponse)) {
				String msg = fail(method,test);
				if (log.isErrorEnabled()) log.error(msg);
				throw new Exception(msg);
			}
			if (log.isDebugEnabled()) log.debug(pass(method,test));
			
			doThisSubclass(extendedHttpServletRequest, (HttpServletResponse)response);
			
						
		} catch (Throwable th) {
			showThrowable(th, log, "can't process this filter()");
			//current filter should not break the filter chain -- go ahead, regardless of internal failure
		}
		
		try {
			if (log.isDebugEnabled()) {
				log.debug(format(method, "before next doFilter()"));
				log.debug(format(method, null, "extendedHttpServletRequest") + extendedHttpServletRequest);
				log.debug(format(method, "extendedHttpServletRequest", extendedHttpServletRequest.getClass().getName()));
				log.debug(format(method, null, "response" + response));
			}
			chain.doFilter(extendedHttpServletRequest, response);
			if (log.isDebugEnabled()) log.debug("back from next doFilter()");
		} catch (Throwable th) {
			showThrowable(th, log, "can't do next doFilter()");
		}
		if (log.isDebugEnabled()) log.debug(exit(method)); 
	}

}
