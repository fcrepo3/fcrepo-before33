package fedora.utilities.install.webxml;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.dom4j.Document;

/**
 * No auth-constraint
 * Optional user-data-constraint
 * Additional http-methods
 * @author Edwin Shin
 *
 */
public class DefaultSecurityConstraint extends SecurityConstraint {
	private static final Set urlPatterns;
	private static final Set httpMethods;
	
	static {
		urlPatterns = new HashSet(Arrays.asList(new String[] {"/"}));
		httpMethods = new HashSet(Arrays.asList(new String[] {"GET", 
				"POST", "PUT", "DELETE", "HEAD", "OPTIONS", "TRACE"}));
	}
	
	public DefaultSecurityConstraint(Document document, WebXMLOptions options) {
		super(document, options);
		
		removeAuthConstraint();
		if (options.apiaSSL) {
			addUserDataConstraint();
		} else {
			removeUserDataConstraint();
		}
	}
	
	public Set getUrlPatterns() {
		return urlPatterns;
	}
	
	public Set getHttpMethods() {
		return httpMethods;
	}
	
}
