/*
 * Created on May 23, 2005
 *
 */
package fedora.test.api;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.custommonkey.xmlunit.SimpleXpathEngine;
import org.w3c.dom.Document;

import fedora.client.FedoraClient;
import fedora.client.HttpInputStream;
import fedora.test.DemoObjectTestSetup;
import fedora.test.FedoraServerTestCase;

/**
 * @author Edwin Shin
 *
 */
public class TestAPIALite extends FedoraServerTestCase {
    private static FedoraClient client;
    
    public static Test suite() {
        TestSuite suite = new TestSuite("APIALite TestSuite");
		suite.addTestSuite(TestAPIALite.class);
		return new DemoObjectTestSetup(suite);
    }
    
    public void setUp() throws Exception {
        client = getFedoraClient();
        SimpleXpathEngine.registerNamespace(NS_FEDORA_TYPES_PREFIX, NS_FEDORA_TYPES);
        SimpleXpathEngine.registerNamespace("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
        SimpleXpathEngine.registerNamespace("uvalibadmin", "http://dl.lib.virginia.edu/bin/admin/admin.dtd/");
    }
    
    public void tearDown() {
    	SimpleXpathEngine.clearNamespaces();
    }
    
    public void testDescribeRepository() throws Exception {
        Document result;
        result = getXMLQueryResult("/describe?xml=true");
        assertXpathExists("/fedoraRepository/repositoryName", result);
    }
    
    public void testGetDatastreamDissemination() throws Exception {
    	Document result;
    	
    	// test for DC datastream
        result = getXMLQueryResult("/get/demo:11/DC");
		assertXpathExists("/oai_dc:dc", result);
		
		// test for type X datastream
		result = getXMLQueryResult("/get/demo:11/TECH1");
		assertXpathExists("//uvalibadmin:technical", result);
		assertXpathEvaluatesTo(
				"wavelet", "/uvalibadmin:admin/uvalibadmin:technical/uvalibadmin:compression/text( )", result);
		
		// test for type E datastream 			
		HttpInputStream his = client.get("/get/demo:11/DS1", true);
		assertEquals(his.getContentType(),"image/x-mrsid-image");
		assertTrue(his.getContentLength() > 0);	
		
		// test for type R datastream
		his = client.get("/get/demo:30/DS1", false, false);
		assertEquals(his.getStatusCode(),302);
		
		// test for type M datastream 			
		his = client.get("/get/demo:5/DS1", true);
		assertEquals(his.getContentType(),"image/jpeg");
		
		his.close();
	}
    
    public void testGetDissemination() throws Exception {
		// test dissemination of the Default Disseminator
    	HttpInputStream his = client.get("/get/demo:11/fedora-system:3/viewDublinCore", true);
    	assertEquals(his.getContentType(),"text/html");
    	his.close();
    }
    
    public void testObjectHistory() throws Exception {
    	Document result;
        result = getXMLQueryResult("/getObjectHistory/demo:11?xml=true");
        assertXpathExists("/fedoraObjectHistory/objectChangeDate", result);
	}
    
    public void testGetObjectProfile() throws Exception {
    	Document result;
        result = getXMLQueryResult("/get/demo:11?xml=true");
        assertXpathEvaluatesTo("demo:11", "/objectProfile/attribute::pid", result);
        assertXpathEvaluatesTo("UVA_MRSID_IMAGE", "/objectProfile/objContentModel", result);
        assertXpathEvaluatesTo("Fedora Data Object", "/objectProfile/objType", result);
	}
    
    public void testListDatastreams() throws Exception {
    	Document result;
        result = getXMLQueryResult("/listDatastreams/demo:11?xml=true");
		assertXpathEvaluatesTo("8", "count(/objectDatastreams/datastream)", result);
	}
    
    public void testListMethods() throws Exception {
    	Document result;
        result = getXMLQueryResult("/listMethods/demo:11?xml=true");
		assertXpathEvaluatesTo("6", "count(/objectMethods/bDef/method)", result);
	}
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestAPIALite.class);
    }

}
