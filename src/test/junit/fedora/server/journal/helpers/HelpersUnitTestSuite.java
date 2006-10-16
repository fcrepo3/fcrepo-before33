package fedora.server.journal.helpers;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

public class HelpersUnitTestSuite extends TestCase {

    public static Test suite() throws Exception {

        TestSuite suite = new TestSuite(HelpersUnitTestSuite.class.getName());
   
        // classes in this package
        suite.addTestSuite(TestParameterHelper.class);
        suite.addTestSuite(TestPasswordCipher.class);

        // sub-package suites
        //suite.addTest(WhateverUnitTestSuite.suite());

        return suite;

    }

    public static void main(String[] args) throws Exception {
        if (System.getProperty("text") != null && System.getProperty("text").equals("true")) {
            junit.textui.TestRunner.run(HelpersUnitTestSuite.suite());
        } else {
            TestRunner.run(HelpersUnitTestSuite.class);
        }
    }
}
