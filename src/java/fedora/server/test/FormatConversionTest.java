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
 * <p><b>Title:</b> FormatConversionTest.java</p>
 * <p><b>Description:</b> Tests an object deserializer and serializer 
 * by opening an object XML file in one format, deserializing it, 
 * re-serializing it another format, and sending output to a file.</p>
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
public class FormatConversionTest
	extends TestCase {

	protected File inFile = null;
	protected File outFile = null;
	protected String inFormat = null;
	protected String outFormat = null;
	protected DigitalObject obj = null;
	private HashMap desermap;
	private HashMap sermap;
		
	protected void setUp() {
		// Set the input/output files and formats
		//inFile=new File("TestIngestFiles/bmech-simple-image-4res.xml");
		//inFile=new File("TestIngestFiles/demo_5.xml");
		//inFile=new File("TestIngestFiles/demo5withaudits.xml");
		inFile=new File("TestIngestFiles/test_demo_5_audits.xml");
		//inFile=new File("TestExportFiles/OUT-conversionTest.xml");
		inFormat="metslikefedora1";
		
		outFile=new File("TestExportFiles/OUT-conversionTest.xml");
		outFormat="foxml1.0";
		
		System.setProperty("fedoraServerHost", "localhost");
		System.setProperty("fedoraServerPort", "80");
		
		try {
			// set up possible serializers and deserializers	
			METSLikeDODeserializer deserM=new METSLikeDODeserializer();
			METSLikeDOSerializer serM=new METSLikeDOSerializer();
			FOXMLDODeserializer deserF=new FOXMLDODeserializer();
			FOXMLDOSerializer serF=new FOXMLDOSerializer();
			desermap=new HashMap();
			sermap=new HashMap();
			desermap.put("metslikefedora1", deserM);
			desermap.put("foxml1.0", deserF);
			sermap.put("metslikefedora1", serM);
			sermap.put("foxml1.0", serF);
		} catch (Exception e) {
			System.out.println("Error: (" + e.getClass().getName() + "):" + e.getMessage());
			e.printStackTrace();
		}
		
		FileInputStream in=null;
		try {
			in=new FileInputStream(inFile);
		} catch (IOException ioe) {
				System.out.println("I/O error on XML inputstream: " + ioe.getMessage());
				ioe.printStackTrace();
		}
		
		// do it!
		DOTranslatorImpl trans=new DOTranslatorImpl(sermap, desermap, null);
		try {
			// deserialize...				
			obj=new BasicDigitalObject();
			System.out.println("Deserializing XML input of format: " + inFormat + "...");
			trans.deserialize(in, obj, inFormat, "UTF-8", DOTranslationUtility.DESERIALIZE_INSTANCE);
			System.out.println("Digital Object PID= " + obj.getPid());
		} catch (Exception e) {
			System.out.println("Error deserializing: " + e.getMessage());
			e.printStackTrace();
		}
		
		try {			
			// serialize...
			System.out.println("Re-serializing as format: " + outFormat + "...");
			System.out.println("Writing file to... " + outFile.getPath());
			FileOutputStream out = new FileOutputStream(outFile);
			trans.serialize(obj, out, outFormat, "UTF-8", DOTranslationUtility.SERIALIZE_EXPORT_MIGRATE);
			System.out.println("Done.");
			//System.out.println("Here it is:");
			//System.out.println(out.toString("UTF-8"));
		} catch (Exception e) {
			System.out.println("Error serializing: " + e.getMessage());
			e.printStackTrace();
		}
	}
		
	public void testDigitalObject() {
		/*	
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
		*/
	}
}