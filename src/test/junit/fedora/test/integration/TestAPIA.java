/*
 * Created on May 23, 2005
 *
 */
package fedora.test.integration;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.custommonkey.xmlunit.SimpleXpathEngine;

import fedora.client.APIAStubFactory;
import fedora.server.access.FedoraAPIA;
import fedora.server.types.gen.MIMETypedStream;
import fedora.server.types.gen.RepositoryInfo;
import fedora.server.types.gen.ObjectProfile;
import fedora.server.types.gen.DatastreamDef;
import fedora.server.types.gen.ObjectMethodsDef;
import fedora.server.types.gen.MethodParmDef;
import fedora.test.FedoraServerTestCase;
import fedora.test.FedoraServerTestSetup;

/**
 * @author Edwin Shin
 * @author Sandy Payette
 *
 */
public class TestAPIA extends FedoraServerTestCase {
    private FedoraAPIA apia;
    private String testPID;
    
    public static Test suite() {
		TestSuite suite = new TestSuite("Unit test for APIA");
		suite.addTestSuite(TestAPIA.class);
		TestSetup wrapper = new TestSetup(suite) {
    	//
        //TestSuite suite = new TestSuite(TestAPIA.class);
        //TestSetup wrapper = new TestSetup(suite) {
            public void setUp() throws Exception {
                TestIngestDemoObjects.ingestDemoObjects();
                SimpleXpathEngine.registerNamespace("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
                SimpleXpathEngine.registerNamespace("dc", "http://purl.org/dc/elements/1.1/");
            }
            
            public void tearDown() throws Exception {
                TestIngestDemoObjects.purgeDemoObjects();
                SimpleXpathEngine.clearNamespaces();
            }
        };
        return new FedoraServerTestSetup(wrapper);
    }
    
    public void setUp() throws Exception {
		testPID = "demo:11";
        apia = APIAStubFactory.getStub(getProtocol(), getHost(), 
                Integer.parseInt(getPort()), getUsername(), getPassword());
    }
    
    public void testDescribeRepository() throws Exception {
        RepositoryInfo describe = apia.describeRepository();
        assertTrue(!describe.getRepositoryName().equals(""));
    }
    
	public void testGetObjectProfile() throws Exception {
		ObjectProfile profile = apia.getObjectProfile(testPID, null);
		assertEquals(testPID, profile.getPid());
		assertEquals("UVA_MRSID_IMAGE", profile.getObjContentModel());
		assertEquals("O", profile.getObjType());
		assertTrue(!profile.getObjDissIndexViewURL().equals(""));
		assertTrue(!profile.getObjItemIndexViewURL().equals(""));
	}
	
	public void testObjectHistory() throws Exception {
		String[] timestamps = apia.getObjectHistory(testPID);
		assertTrue(timestamps.length > 0);
	}
    
    public void testGetDatastreamDissemination() throws Exception {
        MIMETypedStream ds = apia.getDatastreamDissemination(testPID, "DC", null);
        assertXpathExists("/oai_dc:dc", new String(ds.getStream()));
    }
 
	public void testListDatastreams() throws Exception {
		DatastreamDef[] dsDefs = apia.listDatastreams(testPID, null);
		assertEquals(dsDefs.length,8);
		verifyDatastreamDefs(dsDefs, "testListDatastream: ");
	}
	
	public void testListMethods() throws Exception {
		ObjectMethodsDef[] methodDefs = apia.listMethods(testPID, null);
		assertEquals(methodDefs.length,7);
		verifyObjectMethods(methodDefs, "testListMethods: ");
	}

	public void verifyDatastreamDefs(DatastreamDef[] dsDefArray, String msg) throws Exception {
	
		String dsID = null;
		String label = null;
		String mimeType = null;
		DatastreamDef dsDef = null;
	        
		for (int i=0; i<dsDefArray.length; i++) {
			dsDef = dsDefArray[i];
			dsID = dsDef.getID();
			label = dsDef.getLabel();
			mimeType = dsDef.getMIMEType();
			System.out.println(msg + " datastreamDef["+i+"] "
				 + "dsID: "+dsID);
			System.out.println(msg + " datastreamDef["+i+"] "
				 + "label: '"+label+"'");
			System.out.println(msg + " datastreamDef["+i+"] "
				 + "mimeType: "+mimeType);
		}
	        
	}

	public void verifyObjectMethods(ObjectMethodsDef[] methodDefsArray, String msg) throws Exception {
	
		String bDefPID = null;
		String methodName = null;
		MethodParmDef[] parms = null;
		ObjectMethodsDef methodDef = null;
	        
		for (int i=0; i<methodDefsArray.length; i++) {
			methodDef = methodDefsArray[i];
			bDefPID = methodDef.getBDefPID();
			methodName = methodDef.getMethodName();
			parms = methodDef.getMethodParmDefs();
			System.out.println(msg + " methodDef["+i+"] "
				 + "bDefPID: "+bDefPID);
			System.out.println(msg + " methodDef["+i+"] "
				 + "methodName: '"+methodName+"'");
			for (int j=0; j<parms.length; j++) {
				MethodParmDef p = parms[j];	
				System.out.println(msg + " methodDef["+i+"] "
				 	+ "parmName["+j+"] "+p.getParmName());
			}
		}
	        
	}    
    
    //TODO test the rest of APIA
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestAPIA.class);
    }

}
