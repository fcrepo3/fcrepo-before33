package fedora.utilities.install.webxml;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.dom4j.Document;

/**
 * Optional auth-constraint
 * Optional user-data-constraint
 * @author Edwin Shin
 *
 */
public class APIASecurityConstraint extends SecurityConstraint {
	private static final Set urlPatterns;
	private static final Set httpMethods;
	
	static {
		urlPatterns = new HashSet(Arrays.asList(new String[] {"/services/access",
				"/describe",
				"/get/*",
				"/getObjectHistory/*",
				"/listDatastreams/*",
				"/listMethods/*",
				"/getAccessParmResolver",
				"/oai",
				"/report",
				"/risearch",
				"/search"}));
		httpMethods = new HashSet(Arrays.asList(new String[] {"GET", "HEAD", "POST"}));
	}
	
	public APIASecurityConstraint(Document document, WebXMLOptions options) {
		super(document, options);
		
		if (options.apiaAuth) {
			addAuthConstraint();
		} else {
			removeAuthConstraint();
		}
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
