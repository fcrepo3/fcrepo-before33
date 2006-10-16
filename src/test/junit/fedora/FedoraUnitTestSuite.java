package fedora;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import fedora.server.ServerUnitTestSuite;

public class FedoraUnitTestSuite extends TestCase {

    public static Test suite() throws Exception {

        TestSuite suite = new TestSuite(FedoraUnitTestSuite.class.getName());
   
        // classes in this package
        //suite.addTestSuite(WhateverUnitTest.class);

        // sub-package suites
        suite.addTest(ServerUnitTestSuite.suite());

        return suite;

    }

    public static void main(String[] args) throws Exception {
        if (System.getProperty("text") != null && System.getProperty("text").equals("true")) {
            junit.textui.TestRunner.run(FedoraUnitTestSuite.suite());
        } else {
            TestRunner.run(FedoraUnitTestSuite.class);
        }
    }
}
