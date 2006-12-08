package fedora.server.config.webxml;

public class LoginConfig {
	private String authMethod;
	private String realmName;
	
	public LoginConfig() {}

	public String getAuthMethod() {
		return authMethod;
	}

	public void setAuthMethod(String authMethod) {
		this.authMethod = authMethod;
	}

	public String getRealmName() {
		return realmName;
	}

	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}
	
	
}
