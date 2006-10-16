package fedora.server.journal.readerwriter.multifile;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

public class MultiFileUnitTestSuite extends TestCase {

    public static Test suite() throws Exception {

        TestSuite suite = new TestSuite(MultiFileUnitTestSuite.class.getName());
   
        // classes in this package
        suite.addTestSuite(TestLockingFollowingJournalReader.class);

        // sub-package suites
        //suite.addTest(WhateverUnitTestSuite.suite());

        return suite;

    }

    public static void main(String[] args) throws Exception {
        if (System.getProperty("text") != null && System.getProperty("text").equals("true")) {
            junit.textui.TestRunner.run(MultiFileUnitTestSuite.suite());
        } else {
            TestRunner.run(MultiFileUnitTestSuite.class);
        }
    }
}
