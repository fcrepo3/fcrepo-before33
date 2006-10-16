package fedora.server.journal;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import fedora.server.journal.helpers.HelpersUnitTestSuite;
import fedora.server.journal.readerwriter.ReaderWriterUnitTestSuite;
import fedora.server.journal.xmlhelpers.XMLHelpersUnitTestSuite;

public class JournalUnitTestSuite extends TestCase {

    public static Test suite() throws Exception {

        TestSuite suite = new TestSuite(JournalUnitTestSuite.class.getName());
   
        // classes in this package
        //suite.addTestSuite(WhateverUnitTest.class);

        // sub-package suites
        suite.addTest(HelpersUnitTestSuite.suite());
        suite.addTest(ReaderWriterUnitTestSuite.suite());
        suite.addTest(XMLHelpersUnitTestSuite.suite());

        return suite;

    }

    public static void main(String[] args) throws Exception {
        if (System.getProperty("text") != null && System.getProperty("text").equals("true")) {
            junit.textui.TestRunner.run(JournalUnitTestSuite.suite());
        } else {
            TestRunner.run(JournalUnitTestSuite.class);
        }
    }
}
