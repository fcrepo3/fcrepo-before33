/*
 * Created on May 25, 2005
 *
 */
package fedora.test;

import junit.framework.TestCase;

/**
 * @author Edwin Shin
 *
 */
public class TestBaz extends TestCase {

    public void testBaz() {
        System.out.println("testBaz()");
    }
    
    public void testBazBaz() {
        System.out.println("testBazBaz()");
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestBaz.class);
    }

}
