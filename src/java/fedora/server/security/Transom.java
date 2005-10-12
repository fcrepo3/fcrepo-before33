package fedora.server.security;

/**
 * @author wdn5e@virginia.edu
 */

public class Transom { 	
	
	private Boolean allowSurrogate = null;
	private String surrogatePolicyDirectory = null;
	private Boolean validateSurrogatePolicies = null;
	private String policySchemaPath = null;
	
	private Transom() {

	}

	static final Transom singleton = new Transom();
	
	public static final Transom getInstance() {
		return singleton;
	}
	
	public boolean getAllowSurrogate() throws Exception {
		if ((allowSurrogate == null) 
		||  (surrogatePolicyDirectory == null) 
		||  (validateSurrogatePolicies == null) 
		||  (policySchemaPath == null)) {
			throw new Exception("Transom not initialized");
		}
		return allowSurrogate.booleanValue();
	}

	public String getSurrogatePolicyDirectory() throws Exception {
		if ((allowSurrogate == null) 
		||  (surrogatePolicyDirectory == null) 
		||  (validateSurrogatePolicies == null) 
		||  (policySchemaPath == null)) {
			throw new Exception("Transom not initialized");
		}
		return surrogatePolicyDirectory;
	}
	
	public String getPolicySchemaPath() throws Exception {
		if ((allowSurrogate == null) 
		||  (surrogatePolicyDirectory == null) 
		||  (validateSurrogatePolicies == null) 
		||  (policySchemaPath == null)) {
			throw new Exception("Transom not initialized");
		}		
		return policySchemaPath;
	}
	
	public boolean getValidateSurrogatePolicies() throws Exception {
		if ((allowSurrogate == null) 
		||  (surrogatePolicyDirectory == null) 
		||  (validateSurrogatePolicies == null) 
		||  (policySchemaPath == null)) {
			throw new Exception("Transom not initialized");
		}		
		return validateSurrogatePolicies.booleanValue();
	}
	
	public void setAllowSurrogate(boolean allowSurrogate) throws Exception {
		if (this.allowSurrogate != null) {
			throw new Exception("Transom already initialized");			
		}
		this.allowSurrogate = new Boolean(allowSurrogate);
	}
	
	public void setSurrogatePolicyDirectory(String surrogatePolicyDirectory) throws Exception {
		if (this.surrogatePolicyDirectory != null) {
			throw new Exception("Transom already initialized");			
		}
		this.surrogatePolicyDirectory = surrogatePolicyDirectory;
	}
	
	public void setValidateSurrogatePolicies(boolean validateSurrogatePolicies) throws Exception {
		if (this.validateSurrogatePolicies != null) {
			throw new Exception("Transom already initialized");			
		}
		this.validateSurrogatePolicies = new Boolean(validateSurrogatePolicies);
	}
	
	public void setPolicySchemaPath(String policySchemaPath) throws Exception {
		if (this.policySchemaPath != null) {
			throw new Exception("Transom already initialized");			
		}
		this.policySchemaPath = policySchemaPath;
	}

}




