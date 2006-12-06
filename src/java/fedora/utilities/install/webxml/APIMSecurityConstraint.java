package fedora.utilities.install.webxml;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.dom4j.Document;

/**
 * Required auth-constraint
 * Optional user-data-constraint
 * @author Edwin Shin
 *
 */
public class APIMSecurityConstraint extends SecurityConstraint {
	private static final Set<String> urlPatterns;
	private static final Set<String> httpMethods;
	
	static {
		urlPatterns = new HashSet<String>(Arrays.asList(new String[] {"/index.html", 
				"*.jws",
				"/services/management",
				"/management/backendSecurity",
				"/management/getNextPID",
				"/management/upload",
				"/getDSAuthenticated"}));
		httpMethods = new HashSet<String>(Arrays.asList(new String[] {"GET", "HEAD", "POST"}));
	}
	
	public APIMSecurityConstraint(Document document, WebXMLOptions options) {
		super(document, options);
		
		//addAuthConstraint();
		if (options.apimSSL) {
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
