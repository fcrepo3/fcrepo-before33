package fedora.utilities.install.webxml;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.dom4j.Document;

/**
 * Required auth-constraint
 * No user-data-constraint
 * @author Edwin Shin
 *
 */
public class MControlSecurityConstraint extends SecurityConstraint {
	private static final Set<String> urlPatterns;
	private static final Set<String> httpMethods;
	
	static {
		urlPatterns = new HashSet<String>(Arrays.asList(new String[] {"/management/control"}));
		httpMethods = new HashSet<String>(Arrays.asList(new String[] {"GET", "HEAD", "POST"}));
	}
	
	public MControlSecurityConstraint(Document document, WebXMLOptions options) {
		super(document, options);
		
		addAuthConstraint();
		removeUserDataConstraint();
	}
	
	public Set getUrlPatterns() {
		return urlPatterns;
	}
	
	public Set getHttpMethods() {
		return httpMethods;
	}
	
}
