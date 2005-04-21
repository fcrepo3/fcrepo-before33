package fedora.server.test;

import junit.framework.TestCase;

import fedora.server.validation.DOValidatorSchematron;
import fedora.server.validation.DOValidatorSchematronResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


/**
 *
 * <p><b>Title:</b> ValidateSchematronTest.java</p>
 * <p><b>Description:</b></p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class ValidateSchematronTest
	extends TestCase {

	protected String inFile=null;		
	protected String inSchematronPPFile=null;
	protected String inSchematronRulesFile=null;
	
	protected String tempdir=null;
	protected DOValidatorSchematronResult result = null;
		
	protected void setUp() {
		tempdir="TestValidation";
		inSchematronPPFile="src/xsl/schematron/preprocessor.xslt";
		
		// FOXML
		//inFile="TestValidation/foxml-reference-example.xml";
		//inFile="TestIngestFiles/foxml-reference-example.xml";
		//inFile="TestValidation/foxml-bdef.xml";
		inFile="TestIngestFiles/foxml-reference-ingest.xml";
		//inFile="TestExportFiles/problem.xml";				
		inSchematronRulesFile="src/xml/schematron/foxmlRules1-0.xml";

		// METS
		//inFile="TestValidation/bdef-simple-image.xml";			
		//inSchematronRulesFile="src/xml/schematron/metsExtRules1-0.xml";

				
		FileInputStream in=null;
		try {
			in=new FileInputStream(new File(inFile));
		} catch (IOException ioe) {
				System.out.println("Error on XML file inputstream: " + ioe.getMessage());
				ioe.printStackTrace();
		}
		
		try {			
			DOValidatorSchematron dovs = 
				new DOValidatorSchematron(inSchematronRulesFile, inSchematronPPFile, "ingest");
			dovs.validate(in);	
		} catch (Exception e) {
			System.out.println("Error: (" + e.getClass().getName() + "):" + e.getMessage());
			e.printStackTrace();
		}
	}
		
	public void testFoo() {	
		//assertNotNull("Failure: foo is null.", foo.getA());	
	}
}