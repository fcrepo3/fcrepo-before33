package fedora.server;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import fedora.server.journal.JournalUnitTestSuite;

public class ServerUnitTestSuite extends TestCase {

    public static Test suite() throws Exception {

        TestSuite suite = new TestSuite(ServerUnitTestSuite.class.getName());
   
        // classes in this package
        //suite.addUnitTestSuite(WhateverUnitTest.class);

        // sub-package suites
        suite.addTest(JournalUnitTestSuite.suite());

        return suite;

    }

    public static void main(String[] args) throws Exception {
        if (System.getProperty("text") != null && System.getProperty("text").equals("true")) {
            junit.textui.TestRunner.run(ServerUnitTestSuite.suite());
        } else {
            TestRunner.run(ServerUnitTestSuite.class);
        }
    }
}
