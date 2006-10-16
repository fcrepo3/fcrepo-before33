package fedora.server.journal.readerwriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import fedora.server.journal.readerwriter.multifile.MultiFileUnitTestSuite;

public class ReaderWriterUnitTestSuite extends TestCase {

    public static Test suite() throws Exception {

        TestSuite suite = new TestSuite(ReaderWriterUnitTestSuite.class.getName());
   
        // classes in this package
        //suite.addTestSuite(WhateverUnitTest.class);

        // sub-package suites
        suite.addTest(MultiFileUnitTestSuite.suite());

        return suite;

    }

    public static void main(String[] args) throws Exception {
        if (System.getProperty("text") != null && System.getProperty("text").equals("true")) {
            junit.textui.TestRunner.run(ReaderWriterUnitTestSuite.suite());
        } else {
            TestRunner.run(ReaderWriterUnitTestSuite.class);
        }
    }
}
