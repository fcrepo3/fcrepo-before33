package fedora.test.config;

import java.io.*;
import junit.framework.*;
import junit.extensions.TestSetup;
import fedora.test.integration.*;

import fedora.test.FedoraServerTestCase;
import fedora.test.FedoraServerTestSetup;

import fedora.client.*;
import fedora.server.access.*;
import fedora.server.management.*;
import fedora.server.types.gen.*;
import fedora.server.types.gen.ObjectProfile;
import fedora.test.*;

import org.custommonkey.xmlunit.SimpleXpathEngine;

/**
 * TestSuite to test sample policies
 *
 * It uses the default beSecurity.xml file, and a custom repository-policies
 * directory, the default object-policies directory, the default surrogate-policies
 * directory, and the default repository-policies-generated-by-policyguitool
 * directory.
 *
 * So 
 *
 * can be set multiple times:
 *   beSecurity.xml
 *   repository-policies/
 *   object-policies/
 *   surrogate-policies/
 *   repository-policies-generated-by-policyguitool/
 */
public class TestSamplePolicies extends FedoraServerTestCase {

    public static final String BASE_URL_SSL     = "https://localhost:8443/fedora";
	//public static final String BASE_URL         = "http://localhost:8080/fedora";

    public static final String ADMIN_USER       = getUsername();
    public static final String ADMIN_PASS       = getPassword();

	public static final String AUTHORIZED_USER  = "sdp";
	public static final String AUTHORIZED_PASS  = "sdp";
	
    public static final String LESS_AUTHORIZED_USER  = "andy";
    public static final String LESS_AUTHORIZED_PASS  = "andy";
    
	public static final String RESTRICTED_USER  = "guest";
	public static final String RESTRICTED_PASS  = "";

	public static Test suite() {
		TestSuite suite = new TestSuite("TestSamplePolicies");
		suite.addTestSuite(TestSamplePolicies.class);
		TestSetup wrapper = new TestSetup(suite) {
			public void setUp() throws Exception {
				TestIngestDemoObjects.ingestDemoObjects();
				SimpleXpathEngine.registerNamespace("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
				SimpleXpathEngine.registerNamespace(NS_FEDORA_TYPES_PREFIX, NS_FEDORA_TYPES);
				SimpleXpathEngine.registerNamespace("demo", "http://example.org/ns#demo");              
			}
			public void tearDown() throws Exception {
				SimpleXpathEngine.clearNamespaces();
				TestIngestDemoObjects.purgeDemoObjects();
			}
		};
		return new FedoraServerTestSetup(wrapper, TestSamplePolicies.class.getName());
	}

	/**
	 * Test case to determine that we are dealing with an object that is 
	 * of the particular content model that is specified in the policies that
	 * we are interested in testing.
	 */
	public void testCModel() throws Exception {
		System.out.println("Running testContentModel...");
        
		System.out.println("Loading sample policies...");
		usePolicies("noPolicies");
	
		System.out.println("Initiating APIA.getObjectProfile() on APIA...");
		ObjectProfile profile = null;
		try {
			FedoraAPIA apia = new FedoraClient(BASE_URL_SSL, ADMIN_USER, ADMIN_PASS).getAPIA();
			profile = apia.getObjectProfile("demo:5", null);
			assertEquals("UVA_STD_IMAGE", profile.getObjContentModel());
			System.out.println("OK. Got content model from profile: " + profile.getObjContentModel());
		} catch (Exception e) {
			System.out.println("apia.getObjectProfile failed. " +
			e.getClass().getName() + ": " + e.getMessage());
		}		     
	}
	    
	/**
	 * Test case for an authorized user trying to get a raw datastream 
	 * when a policy is in scope that restricts access to certain user roles 
	 * on a specific content model.  
	 * 
	 * TEST CASE SHOULD SUCCEED.
	 */
    public void testPolicyObjectCModelDSRole() throws Exception {
 
		System.out.println("Test: Permit user role to access datastream given a content model...");       
		usePolicies("APIA-RestrictObjects");
        try {
			FedoraAPIA apia = new FedoraClient(BASE_URL_SSL, AUTHORIZED_USER, AUTHORIZED_PASS).getAPIA();
			apia.getDatastreamDissemination("demo:5", "DS1", null); 
			System.out.println("PERMITTED.");
        } catch (Exception e) {
			// FIXME: Confirm that things failed because of policy exception For now...
            fail("BAD FAILURE. Authorized user " + AUTHORIZED_USER + 
				 ", was improperly denied access to getDatastreamDissemination: " + 
				 e.getClass().getName() + ": " + e.getMessage());
        }       
    }
    
	/**
	 * Test case for a less-authorized user trying to get a raw datastream 
	 * when a policy is in scope that restricts access to certain user roles 
	 * on a specific content model.   
	 * 
	 * TEST CASE SHOULD FAIL.
	 */
	public void testPolicyObjectCModelDSRoleFAIL() throws Exception {
        
		usePolicies("APIA-RestrictObjects");
		System.out.println("Test: Deny user role to access datastream given a content model..."); 
		try {
			FedoraAPIA apia = new FedoraClient(BASE_URL_SSL, LESS_AUTHORIZED_USER, LESS_AUTHORIZED_PASS).getAPIA();
			apia.getDatastreamDissemination("demo:5", "DS1", null); 
			System.out.println("PERMITTED.");
		} catch (Exception e) {
			// FIXME: Confirm that things failed because of policy exception For now... 
			fail("GOOD FAILURE. Less-authorized user " + LESS_AUTHORIZED_USER + 
				 ", was denied access to getDatastreamDissemination: " + 
				 e.getClass().getName() + ": " + e.getMessage());
		}       
	}
	
	/**
	 * Test case for an authorized users trying to get a dissemination 
	 * when a policy is in scope that restricts access to certain user roles 
	 * on a specific content model.  
	 * 
	 * TEST CASE SHOULD SUCCEED.
	 */
	public void testPolicyObjectCModelDissRole() throws Exception {
        
		usePolicies("APIA-RestrictObjects");
		System.out.println("Test: Permit user role to access dissemination given a content model..."); 
		try {
			FedoraAPIA apia = new FedoraClient(BASE_URL_SSL, AUTHORIZED_USER, AUTHORIZED_PASS).getAPIA();
			apia.getDissemination("demo:5", "demo:1", "getThumbnail", new Property[0], null); 
			System.out.println("PERMITTED.");
		} catch (Exception e) {
			// FIXME: Confirm that things failed because of policy exception For now... 
			fail("BAD FAILURE. Authorized user " + AUTHORIZED_USER + 
				 ", was improperly denied access to getDissemination: " + 
				 e.getClass().getName() + ": " + e.getMessage());
		} 
		
		System.out.println("Test: Permit user role to access dissemination given a content model..."); 
		System.out.println("Testing dissemination on UVA_STD_IMAGE object for authorized user ...");
		try {
			FedoraAPIA apia = new FedoraClient(BASE_URL_SSL, LESS_AUTHORIZED_USER, LESS_AUTHORIZED_PASS).getAPIA();
			apia.getDissemination("demo:5", "demo:1", "getThumbnail", new Property[0], null); 
			System.out.println("PERMITTED. The less-authorized user " + LESS_AUTHORIZED_USER + " was permitted.");
		} catch (Exception e) {
			// FIXME: Confirm that things failed because of policy exception For now...  
			fail("BAD FAILURE. Less-authorized user " + LESS_AUTHORIZED_USER + 
				 ", was improperly denied access to getDissemination: " + 
				 e.getClass().getName() + ": " + e.getMessage());
		}       
	}
    
	/**
	 * Test case where a restricted user tried to get a datastream dissemination 
	 * when a policy is in scope that restricts access to certain user roles 
	 * on a specific content model.   
	 * 
	 * TEST CASE SHOULD FAIL.
	 */
	public void testPolicyObjectCModelDissRoleFAIL() throws Exception {
        
		usePolicies("APIA-RestrictObjects");
		System.out.println("Test: Deny user role to access dissemination given a content model..."); 
		try {
			FedoraAPIA apia = new FedoraClient(BASE_URL_SSL, RESTRICTED_USER, RESTRICTED_PASS).getAPIA();
			apia.getDissemination("demo:5", "demo:1", "getThumbnail", new Property[0], null); 
		} catch (Exception e) {
			// FIXME: Confirm that things failed because of policy exception For now... 
			fail("GOOD FAILURE. Restricted user " + RESTRICTED_USER + 
				 ", was denied access to getDatastreamDissemination: " + 
				 e.getClass().getName() + ": " + e.getMessage());
		}       
	}
    

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestSamplePolicies.class);
    }

}