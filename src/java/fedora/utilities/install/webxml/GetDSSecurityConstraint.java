package fedora.utilities.install.webxml;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.dom4j.Document;

/**
 * No user-data-constraint
 * No auth-constraint
 * @author Edwin Shin
 *
 */
public class GetDSSecurityConstraint extends SecurityConstraint {
	private static final Set urlPatterns;
	private static final Set httpMethods;
	
	static {
		urlPatterns = new HashSet(Arrays.asList(new String[] {"/getDS"}));
		httpMethods = new HashSet(Arrays.asList(new String[] {"GET", "HEAD", "POST"}));
	}
	
	public GetDSSecurityConstraint(Document document, WebXMLOptions options) {
		super(document, options);
		
		removeAuthConstraint();
		removeUserDataConstraint();
	}
	
	public Set getUrlPatterns() {
		return urlPatterns;
	}
	
	public Set getHttpMethods() {
		return httpMethods;
	}
	
}
