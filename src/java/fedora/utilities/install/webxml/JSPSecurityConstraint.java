package fedora.utilities.install.webxml;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.dom4j.Document;

/**
 * No auth-constraint
 * Optional user-data-constraint
 * @author Edwin Shin
 *
 */
public class JSPSecurityConstraint extends SecurityConstraint {
	private static final Set<String> urlPatterns;
	private static final Set<String> httpMethods;
	
	static {
		urlPatterns = new HashSet<String>(Arrays.asList(new String[] {"*.jsp", "*.faces"}));
		httpMethods = new HashSet<String>(Arrays.asList(new String[] {"GET", "HEAD", "POST"}));
	}
	
	public JSPSecurityConstraint(Document document, WebXMLOptions options) {
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
