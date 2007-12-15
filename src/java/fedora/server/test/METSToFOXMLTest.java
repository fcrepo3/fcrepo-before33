/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.HashMap;

import junit.framework.TestCase;

import fedora.common.Constants;

import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.translation.*;

/**
 * Tests the METS deserializer and FOXML serializer 
 * by opening a METS file, deserializing it, re-serializing it as FOXML, 
 * and sending it to STDOUT.
 *
 * @author payette@cs.cornell.edu
 */
public class METSToFOXMLTest
	extends TestCase
	implements Constants {

	protected File inFile = null;
	protected File outFile = null;
	protected DigitalObject obj = null;
		
	protected void setUp() {
		// Set the METS input file and FOXML output file
		inFile=new File("TestIngestFiles/bmech-simple-image-4res.xml");
		outFile=new File("TestExportFiles/OUT-foxml.xml");
		System.setProperty("fedoraServerHost", "localhost");
		System.setProperty("fedoraServerPort", "80");
		
		FileInputStream in=null;
		try {
			in=new FileInputStream(inFile);
		} catch (IOException ioe) {
				System.out.println("Error on FOXML file inputstream: " + ioe.getMessage());
				ioe.printStackTrace();
		}
		try {	
			// deserialize	
			METSFedoraExt1_1DODeserializer deser=new METSFedoraExt1_1DODeserializer();
			FOXML1_1DOSerializer ser=new FOXML1_1DOSerializer();
			HashMap desermap=new HashMap();
			HashMap sermap=new HashMap();
			desermap.put(METS_EXT1_1.uri, deser);
			DOTranslatorImpl trans=new DOTranslatorImpl(sermap, desermap);
			obj=new BasicDigitalObject();
			System.out.println("Deserializing METS input...");
			trans.deserialize(in, obj, METS_EXT1_1.uri, "UTF-8", DOTranslationUtility.DESERIALIZE_INSTANCE);
			System.out.println("Digital Object PID= " + obj.getPid());
			// serialize
			sermap.put(FOXML1_1.uri, ser);
			System.out.println("Re-serializing as FOXML...");
			System.out.println("Writing file to... " + outFile.getPath());
			FileOutputStream out = new FileOutputStream(outFile);
			//ByteArrayOutputStream out=new ByteArrayOutputStream();
			trans.serialize(obj, out, FOXML1_1.uri, "UTF-8", DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL);
			System.out.println("Done.");
			//System.out.println("Here it is:");
			//System.out.println(out.toString("UTF-8"));
		} catch (Exception e) {
			System.out.println("Error: (" + e.getClass().getName() + "):" + e.getMessage());
			e.printStackTrace();
		}
	}
		
	public void testDigitalObject() {	
		assertNotNull("Failure: digital object PID is null.", obj.getPid());
		assertNotNull("Failure: digital object audit record set is null.", obj.getAuditRecords());
		assertNotNull("Failure: digital object label is null.", obj.getLabel());
		assertNotNull("Failure: digital object ownerID is null.", obj.getOwnerId());
	}
}
