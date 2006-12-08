package fedora.server.config.webxml;

import java.util.ArrayList;
import java.util.List;

public class WebResourceCollection {
	private String webResourceName;
	private List<String> descriptions;
	private List<String> urlPatterns;
	private List<String> httpMethods;
	
	public WebResourceCollection() {
		descriptions = new ArrayList<String>();
		urlPatterns = new ArrayList<String>();
		httpMethods = new ArrayList<String>();
	}
	
	public String getWebResourceName() {
		return webResourceName;
	}

	public void setWebResourceName(String webResourceName) {
		this.webResourceName = webResourceName;
	}

	public List<String> getDescriptions() {
		return descriptions;
	}
	
	public void addDescription(String description) {
		descriptions.add(description);
	}
	
	public void removeDescription(String description) {
		descriptions.remove(description);
	}

	public List<String> getHttpMethods() {
		return httpMethods;
	}

	public void addHttpMethod(String httpMethod) {
		httpMethods.add(httpMethod);
	}

	public List<String> getUrlPatterns() {
		return urlPatterns;
	}

	public void addUrlPattern(String urlPattern) {
		urlPatterns.add(urlPattern);
	}
}
