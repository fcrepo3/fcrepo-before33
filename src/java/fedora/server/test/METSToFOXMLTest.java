package fedora.server.test;

import junit.framework.TestCase;

import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.translation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * <p><b>Title:</b> METSToFOXMLTest.java</p>
 * <p><b>Description:</b> Tests the METS deserializer and FOXML serializer 
 * by opening a METS file, deserializing it, re-serializing it as FOXML, 
 * and sending it to STDOUT.</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class METSToFOXMLTest
	extends TestCase {

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
			METSLikeDODeserializer deser=new METSLikeDODeserializer();
			FOXMLDOSerializer ser=new FOXMLDOSerializer();
			HashMap desermap=new HashMap();
			HashMap sermap=new HashMap();
			desermap.put("metslikefedora1", deser);
			DOTranslatorImpl trans=new DOTranslatorImpl(sermap, desermap, null);
			obj=new BasicDigitalObject();
			System.out.println("Deserializing METS input...");
			trans.deserialize(in, obj, "metslikefedora1", "UTF-8", DOTranslationUtility.DESERIALIZE_INSTANCE);
			System.out.println("Digital Object PID= " + obj.getPid());
			// serialize
			sermap.put("foxml1.0", ser);
			System.out.println("Re-serializing as FOXML...");
			System.out.println("Writing file to... " + outFile.getPath());
			FileOutputStream out = new FileOutputStream(outFile);
			//ByteArrayOutputStream out=new ByteArrayOutputStream();
			trans.serialize(obj, out, "foxml1.0", "UTF-8", DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL);
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
		assertNotNull("Failure: digital object cmodel is null.", obj.getContentModelId());
		//assertNotNull("Failure: digital object createDate is null.", obj.getCreateDate());
		assertNotNull("Failure: digital object label is null.", obj.getLabel());
		//assertNotNull("Failure: digital object modDate is null.", obj.getLastModDate());
		assertNotNull("Failure: digital object namespaceMap is null.", obj.getNamespaceMapping());
		//assertNotNull("Failure: digital object state is null.", obj.getState());
		//assertNotNull("Failure: digital object ftype is null.", obj.getFedoraObjectType());
		assertNotNull("Failure: digital object ownerID is null.", obj.getOwnerId());
		//assertNotNull("Failure: digital object PID is null.", obj.datastreamIdIterator());
		//assertNotNull("Failure: digital object PID is null.", obj.disseminatorIdIterator());	
	}
}