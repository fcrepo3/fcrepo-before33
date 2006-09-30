/*
 * Created on May 23, 2005
 *
 */
package fedora.test.api;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.w3c.dom.Document;

import fedora.test.FedoraServerTestCase;

/**
 * @author Edwin Shin
 *
 */
public class TestAPIMLite extends FedoraServerTestCase {
    
    public static Test suite() {
        TestSuite suite = new TestSuite("APIMLite TestSuite");
        suite.addTestSuite(TestAPIMLite.class);
        return suite;
    }
    
    public void testGetNextPID() throws Exception {
        Document result;
        result = getXMLQueryResult("/management/getNextPID?xml=true");
        assertXpathEvaluatesTo("1", "count(/pidList/pid)", result);
        
        result = getXMLQueryResult("/management/getNextPID?numpids=10&namespace=demo&xml=true");
        assertXpathEvaluatesTo("10", "count(/pidList/pid)", result);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestAPIMLite.class);
    }
}
