package fedora.server.journal.xmlhelpers;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

public class XMLHelpersUnitTestSuite extends TestCase {

    public static Test suite() throws Exception {

        TestSuite suite = new TestSuite(XMLHelpersUnitTestSuite.class.getName());
   
        // classes in this package
        suite.addTestSuite(TestContextXmlWriterAndReader.class);

        // sub-package suites
        //suite.addTest(WhateverUnitTestSuite.suite());

        return suite;

    }

    public static void main(String[] args) throws Exception {
        if (System.getProperty("text") != null && System.getProperty("text").equals("true")) {
            junit.textui.TestRunner.run(XMLHelpersUnitTestSuite.suite());
        } else {
            TestRunner.run(XMLHelpersUnitTestSuite.class);
        }
    }
}
