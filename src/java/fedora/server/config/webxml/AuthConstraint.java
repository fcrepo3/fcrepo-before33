package fedora.server.config.webxml;

import java.util.ArrayList;
import java.util.List;

public class AuthConstraint {
	private List<String> descriptions;
	private String roleName;

	public AuthConstraint() {
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
	
	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}
