package fedora.server.security;

import fedora.server.errors.GeneralException;
import java.util.Set;
import java.util.Hashtable;


/**
 * 
 * @author payette
 *
 * Class that instantiates information parsed from the beSecurity.xml file. 
 * Methods are provides to set and get backend security properties by role id.
 */
public class BackendSecuritySpec {

	/**
	 * The Hashtable is as follows:
	 * 
	 *   roleKey = the role identifier for the backend service, for example: 
	 *           - "bmech:9"  (the role key for a backend service)
	 * 			 - "bmech:9/getThumb" (the role key for a method within a backend service)   
	 * 			 - "fedora" (the role key for fedora calling back to itself)
	 * 
	 *   VALUE = a Hashtable of security properties whose keys
	 * 	         are defined in BackendSecurityDeserializer.java as:
	 * 		   	 - BackendSecurityDeserializer.BE_BASIC_AUTH
	 * 		   	 - BackendSecurityDeserializer.BE_SSL
	 * 		   	 - BackendSecurityDeserializer.BE_IPLIST
	 * 		   	 - BackendSecurityDeserializer.BE_USERNAME
	 * 		   	 - BackendSecurityDeserializer.BE_PASSWORD
	 */	
	private Hashtable rolePropertiesTable;	
	
	public BackendSecuritySpec() {
		rolePropertiesTable = new Hashtable();

	}

	/**
	 * Set the default backend security properties.
	 * @param properties
	 */	
	public void setDefaultSecuritySpec(Hashtable properties) {
		
		System.out.println(">>>>>> setSecuritySpec: "
			+ " property count=" + properties.size()	);
			
		rolePropertiesTable.put("ROLE_DEFAULT", properties);
	}
		
	/**
	 * Set the security properties at the backend service or for a 
	 * method of that backend service.   
	 * @param serviceRoleID
	 * @param methodRoleID - a methodname within the backend service.  If this 
	 *        parm is null, then this method will set default security properties
	 *        for the backend service. 
	 * @param properties
	 */	
	public void setSecuritySpec(String serviceRoleID, String methodRoleID, Hashtable properties) 
		throws GeneralException {
	
		System.out.println(">>>>>> setSecuritySpec: "
			+ " serviceRoleID=" + serviceRoleID
			+ " methodRoleID=" + methodRoleID
			+ " property count=" + properties.size()	);
			
		if (serviceRoleID == null || serviceRoleID.equals("")) {
			throw new GeneralException("serviceRoleID is missing.");
		}
		// if methodRoleID is missing, then set properties at the service level.
		if (methodRoleID == null || methodRoleID.equals("")) {
			rolePropertiesTable.put(serviceRoleID, properties);
			
		// otherwise set properties at the method level, but only if
		// parent service-level properties already exist.
		} else {
			Hashtable serviceProps = (Hashtable) rolePropertiesTable.get(serviceRoleID);
			if (serviceProps == null) {
				throw new GeneralException("Cannot add method-level security properties"  +
					" if there are no properties defined for the backend service that the " +
					" method is part of. ");
				
			}
			String roleKey = serviceRoleID + "/" + methodRoleID;
			rolePropertiesTable.put(roleKey, properties);			
		}
	}


	/**
	 * Get the default backend security properties.
	 * @param properties
	 */	
	public Hashtable getDefaultSecuritySpec() {
		return (Hashtable) rolePropertiesTable.get("ROLE_DEFAULT");
	}
			
	/**
	 * Get security properties for either the a backend service or
	 * a method within that backend service. 
	 * 
	 * @param serviceRoleID - role identifier for a backend service
	 * @param methodRoleID  - if null, will return the default 
	 *        security properties for the backend service
	 * @return
	 */		
	public Hashtable getSecuritySpec(String serviceRoleID, String methodRoleID){
		if (serviceRoleID == null || serviceRoleID.equals("")){
			return getDefaultSecuritySpec();			
		}
		else if (methodRoleID == null || methodRoleID.equals("")){
			return (Hashtable) rolePropertiesTable.get(serviceRoleID);
		}
		else {
			String roleKey = serviceRoleID + "/" + methodRoleID;
			// First see if there is a role key that is at the method level
			Hashtable properties = (Hashtable) rolePropertiesTable.get(roleKey);
			
			// if we did not find security properties for the method level,
			// roll up to the parent service level and get properties.
			if (properties == null) {
				properties = (Hashtable) rolePropertiesTable.get(serviceRoleID);
			}
			
			// if we did not find method or service-level properties,
			// roll up the the default level and get default properties.
			if (properties == null){
				properties =  getDefaultSecuritySpec();
			}
			return properties;			
		}
	}
	
	/**
	 * Get security properties for either the a backend service or
	 * a method within that backend service. 
	 * 
	 * @param roleKey - role key
	 * @return
	 */		
	public Hashtable getSecuritySpec(String roleKey){
		return (Hashtable) rolePropertiesTable.get(roleKey);
			
	}
	
	public Set listRoleKeys(){
		return (Set) rolePropertiesTable.keySet();
	}

	//=======================================
	public static void main(String[] args) throws Exception {

	}
}
