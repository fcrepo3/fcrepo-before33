/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fedora.common.Constants;

/**
 * @author Edwin Shin
 * @version $Id$
 */
public class DOValidatorXMLSchemaTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testFoxmlValidation() throws Exception {
        InputStream in =
                new FileInputStream("src/demo-objects/foxml/local-server-demos/simple-image-demo/obj_demo_5.xml");
        DOValidatorXMLSchema dov =
                new DOValidatorXMLSchema("src/xsd/foxml1-1.xsd");
        dov.validate(in);
    }

    @Test
    public void testMetsValidation() throws Exception {
        InputStream in =
                new FileInputStream("src/demo-objects/mets/local-server-demos/simple-image-demo/demo_35.xml");
        DOValidatorXMLSchema dov =
                new DOValidatorXMLSchema("src/xsd/mets-fedora-ext1-1.xsd");
        dov.validate(in);
    }

    @Test
    public void testAtomValidation() throws Exception {
        InputStream in =
                new FileInputStream("src/demo-objects/atom/local-server-demos/simple-image-demo/obj_demo_5.xml");
        DOValidatorXMLSchema dov = new DOValidatorXMLSchema("src/xsd/atom.xsd");
        dov.validate(in);

        SchemaFactory sf =
                SchemaFactory.newInstance(Constants.XML_XSD.uri);
        Schema schema = sf.newSchema(new File("src/xsd/atom.xsd"));
        Validator validator = schema.newValidator();
        //validator.validate(new StreamSource(in));
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(DOValidatorXMLSchemaTest.class);
    }
}
