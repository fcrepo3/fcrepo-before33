/*
 * Created on May 26, 2005
 *
 */
package fedora.test;

import junit.framework.TestCase;

/**
 * @author Edwin Shin
 *
 */
public class TestQuux extends TestCase {
    public void testQuuz() {
        System.out.println("testQuux()"); 
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestQuux.class);
    }

}
