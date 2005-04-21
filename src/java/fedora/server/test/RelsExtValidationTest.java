package fedora.server.test;

import junit.framework.TestCase;

import fedora.server.validation.*;
import fedora.server.errors.*;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * <p><b>Title:</b> RelsExtValidationTest.java</p>
 * <p><b>Description:</b> Tests the RELS-EXT datastream deserializer and validation.</p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class RelsExtValidationTest
	extends TestCase {

	protected File inFile = null;
	private String doURI;
		
	protected void setUp() {
		inFile=new File("TestIngestFiles/RELS-EXT-preferred.xml");
		doURI="info:fedora/nsdl:100";
		System.setProperty("fedoraServerHost", "localhost");
		System.setProperty("fedoraServerPort", "8080");
		
		FileInputStream in=null;
		try {
			in=new FileInputStream(inFile);
		} catch (IOException ioe) {
				System.out.println("Error on RELS-EXT file inputstream: " + ioe.getMessage());
				ioe.printStackTrace();
		}
		try {	
			// setup	
			RelsExtValidator deser=new RelsExtValidator("UTF-8", false);
			
			// deserialize input XML
			System.out.println("Deserializing RELS-EXT...");
			deser.deserialize(in, doURI);
			System.out.println("Done validating RELS-EXT.");
		} catch (SAXException e) {
			e.printStackTrace();
			System.out.println("SAXException: (" + e.getClass().getName() + "):" + e.getMessage());
		} catch (ServerException e) {
			System.out.println("ServerException: suppressing info not available without running server.");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception: (" + e.getClass().getName() + "):" + e.getMessage());

		}
	}
		
	public void testDigitalObject() {	
		//assertNotNull("Failure: digital object PID is null.", obj.getPid());
		//assertNotNull("Failure: digital object audit record set is null.", obj.getAuditRecords());
		//assertNotNull("Failure: digital object cmodel is null.", obj.getContentModelId());
		//assertNotNull("Failure: digital object createDate is null.", obj.getCreateDate());	
	}
}