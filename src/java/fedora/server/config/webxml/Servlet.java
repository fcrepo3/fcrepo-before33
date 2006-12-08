package fedora.server.config.webxml;

import java.util.ArrayList;
import java.util.List;

public class Servlet {
	private List<String> descriptions;
	private String displayName;
	private String servletName;
	private String servletClass;
	private List<InitParam> initParams;
	private String loadOnStartup;
	
	public Servlet() {
		descriptions = new ArrayList<String>();
		initParams = new ArrayList<InitParam>();
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
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getLoadOnStartup() {
		return loadOnStartup;
	}

	public void setLoadOnStartup(String loadOnStartup) {
		this.loadOnStartup = loadOnStartup;
	}

	public String getServletClass() {
		return servletClass;
	}

	public void setServletClass(String servletClass) {
		this.servletClass = servletClass;
	}

	public String getServletName() {
		return servletName;
	}

	public void setServletName(String servletName) {
		this.servletName = servletName;
	}
	
	public List<InitParam> getInitParams() {
		return initParams;
	}
	
	public void addInitParam(InitParam initParam) {
		initParams.add(initParam);
	}
	
}
