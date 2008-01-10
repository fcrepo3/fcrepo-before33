/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import fedora.server.validation.DOValidatorXMLSchema;

/**
 * @author Sandy Payette
 */
public class ValidateXMLSchemaTest
        extends TestCase {

    protected String inFile = null;

    protected String inXMLSchemaFile = null;

    protected String tempdir = null;

    @Override
    protected void setUp() {
        tempdir = "TestValidation";

        // METS
        //inFile="TestValidation/bdef-simple-image.xml";
        //inFile="TestValidation/minMETS.xml";				
        //inXMLSchemaFile="src/xsd/mets-fedora-ext1-1.xsd";

        // FOXML
        //inFile="TestValidation/foxml-reference-example.xml";
        inFile = "TestValidation/foxml-bdef.xml";
        //inFile="TestValidation/minFOX4.xml";
        //inFile="TestValidation/minFOX5.xml";				
        inXMLSchemaFile = "src/xsd/foxml1-1.xsd";

        FileInputStream in = null;
        try {
            in = new FileInputStream(new File(inFile));
        } catch (IOException ioe) {
            System.out.println("Error on XML file inputstream: "
                    + ioe.getMessage());
            ioe.printStackTrace();
        }

        try {
            DOValidatorXMLSchema dov =
                    new DOValidatorXMLSchema(inXMLSchemaFile);
            dov.validate(in);
        } catch (Exception e) {
            System.out.println("Error: (" + e.getClass().getName() + "):"
                    + e.getMessage());
            e.printStackTrace();
        }
    }

    public void testFoo() {
        //assertNotNull("Failure: foo is null.", foo.getA());	
    }
}
