package fedora.server.config.webxml;

import java.util.ArrayList;
import java.util.List;

public class InitParam {
	private List<String> descriptions;
	private String paramName;
	private String paramValue;
	
	public InitParam() {
		descriptions = new ArrayList<String>();
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
	
	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	
}
