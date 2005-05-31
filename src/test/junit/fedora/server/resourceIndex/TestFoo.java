package fedora.server.resourceIndex;

import junit.framework.TestCase;

import org.jrdf.graph.Triple;
import org.trippi.TripleMaker;

/**
 * @author Edwin Shin
 */
public class TestFoo extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestFoo.class);
    }
    
//    public void testBar() throws Exception {
//        String dateString = "2005-01-14T22:33:44.0";
//        System.out.println("* " + getDate(dateString));
//    }
    
    public void testBaz() throws Exception {
        Triple t1 = TripleMaker.createTyped("urn:foo", "urn:bar", "2001-09-09T10:11:23.123", "http://www.w3.org/2001/XMLSchema#dateTime");
        Triple t2 = TripleMaker.createTyped("urn:foo", "urn:bar", "2001-09-09T10:11:23.123", "http://www.w3.org/2001/XMLSchema#dateTime");
        assertEquals(t1, t2);
        System.out.println(t1.hashCode() + " : " + t2.hashCode());
    }
}
