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
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class ValidateSchematronTest
	extends TestCase {

	protected String inFOXMLFile=null;		
	protected String inSchematronPPFile=null;
	protected String inSchematronRulesFile=null;
	
	protected String tempdir=null;
	protected DOValidatorSchematronResult result = null;
		
	protected void setUp() {
		inFOXMLFile="TestValidation/foxml-reference-example.xml";				
		inSchematronRulesFile="TestValidation/fedoraRulesFOXML.xml";
		inSchematronPPFile="TestValidation/preprocessor.xslt";
		tempdir="TestValidation";
				
		FileInputStream in=null;
		try {
			in=new FileInputStream(new File(inFOXMLFile));
		} catch (IOException ioe) {
				System.out.println("Error on FOXML file inputstream: " + ioe.getMessage());
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