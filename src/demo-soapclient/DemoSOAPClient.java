package demo.soapclient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.StringTokenizer;
import java.util.HashMap;

import javax.xml.rpc.ServiceException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import fedora.client.APIMStubFactory;
import fedora.client.APIAStubFactory;
import fedora.server.management.FedoraAPIM;
import fedora.server.access.FedoraAPIA;
import fedora.server.types.gen.RepositoryInfo;

/**
 *
 * <p><b>Title:</b> DemoSOAPClient</p>
 * <p><b>Description: </b> </p>
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
 */
public class DemoSOAPClient {

    private FedoraAPIM APIM;
	private FedoraAPIA APIA;
    private static HashMap s_repoInfo=new HashMap();

    public DemoSOAPClient(String host, int port, String user, String pass)
            throws MalformedURLException, ServiceException {
            	
        // get SOAP stubs to connect to the repository web services
	APIA = APIAStubFactory.getStub(host, port, user, pass);
        APIM = APIMStubFactory.getStub(host, port, user, pass);
    }
    

	public RepositoryInfo describeRepository() 
		throws RemoteException {
			
		// make the SOAP call on API-A using the connection stub
		RepositoryInfo repoinfo = APIA.describeRepository();
		
		// print results
		System.out.println("SOAP Request: describeRepository...");
		System.out.println("SOAP Response: repository version = " + repoinfo.getRepositoryVersion());
		System.out.println("SOAP Response: repository name = " + repoinfo.getRepositoryName());
		System.out.println("SOAP Response: repository pid namespace = " + repoinfo.getRepositoryPIDNamespace());	
		System.out.println("SOAP Response: repository default export = " + repoinfo.getDefaultExportFormat());
		System.out.println("SOAP Response: repository base URL = " + repoinfo.getRepositoryBaseURL());	
		System.out.println("SOAP Response: repository OAI namespace = " + repoinfo.getOAINamespace());
		System.out.println("SOAP Response: repository sample OAI identifier = " + repoinfo.getSampleOAIIdentifier());
		System.out.println("SOAP Response: repository sample OAI URL = " + repoinfo.getSampleOAIURL());
		System.out.println("SOAP Response: repository sample access URL = " + repoinfo.getSampleAccessURL());
		System.out.println("SOAP Response: repository sample search URL = " + repoinfo.getSampleSearchURL());		
		System.out.println("SOAP Response: repository sample PID = " + repoinfo.getSamplePID());
		return repoinfo;						
	}
	
	public String ingest(InputStream ingestStream, String ingestFormat, String logMessage)
		throws RemoteException, IOException {
		
		// prep 		
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		pipeStream(ingestStream, out, 4096);
		
		// make the SOAP call on API-M using the connection stub
		String pid = APIM.ingest(out.toByteArray(), ingestFormat, logMessage);
		
		System.out.println("SOAP Request: ingest...");
		System.out.println("SOAP Response: pid = " + pid);
		return pid;
	}
	public String addDatastream(String pid,String dsID, String[] altIDs, String dsLabel, 
		boolean versionable, String dsMIME, String formatURI, 
		String dsLocation, String dsControlGroup, String dsState, String logMessage)
		throws RemoteException {

			// make the SOAP call on API-M using the connection stub			
			String datastreamID = APIM.addDatastream(
				pid, dsID, altIDs, dsLabel, versionable, dsMIME, formatURI,
				dsLocation, dsControlGroup, dsState, logMessage);
				
			System.out.println("SOAP Request: addDatastream...");
			System.out.println("SOAP Response: datastreamID = " + datastreamID);				
			return datastreamID;
		}
		
	public String modifyDatastreamByReference(String pid, String dsID, String[] altIDs, String dsLabel, 
		boolean versionable, String dsMIME, String formatURI, String dsLocation, String dsState,
		String logMessage, boolean force)
		throws RemoteException {

			// make the SOAP call on API-M using the connection stub
			String datastreamID = APIM.modifyDatastreamByReference(
				pid, dsID, altIDs, dsLabel, versionable, dsMIME,
				formatURI, dsLocation, dsState, logMessage, force);			
				
			System.out.println("SOAP Request: modifyDatastreamByReference...");
			System.out.println("SOAP Response: datastreamID = " + datastreamID);				
			return datastreamID;
		}
		
	public byte[] export(String pid, String format, String exportContext, OutputStream outStream) 
		throws RemoteException, IOException {
					
		// make the SOAP call on API-M
		byte[] objectXML = APIM.export(pid, format, exportContext);
		
		// serialize the object XML to the specified output stream
		try {						
			// use Xerces to pretty print the xml, assuming it's well formed
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder=factory.newDocumentBuilder();
			Document doc=builder.parse(new ByteArrayInputStream(objectXML));
			OutputFormat fmt=new OutputFormat("XML", "UTF-8", true);
			fmt.setIndent(2);
			fmt.setLineWidth(120);
			fmt.setPreserveSpace(false);
			XMLSerializer ser=new XMLSerializer(outStream, fmt);
			ser.serialize(doc);
		} catch (Exception e) {
			System.out.println("Error on export while serializing object XML." + 
				e.getClass().getName() + " : " + e.getMessage());
		} finally {
		  outStream.close();
		}
		
		// print results
		System.out.println("SOAP Request: export...");
		System.out.println("SOAP Response: see result serialized in XML export file.");	
		return objectXML;						
	}

	public byte[] getObjectXML(String pid) 
		throws RemoteException {
			
		// make the SOAP call on API-M	
		byte[] objectXML = APIM.getObjectXML(pid);
		
		// print results

		return objectXML;						
	}
	
	/**
	 * Copies the contents of an InputStream to an OutputStream, then closes
	 * both.  
	 *
	 * @param in The source stream.
	 * @param out The target stram.
	 * @param bufSize Number of bytes to attempt to copy at a time.
	 * @throws IOException If any sort of read/write error occurs on either
	 *         stream.
	 */
	public static void pipeStream(InputStream in, OutputStream out, int bufSize)
			throws IOException {
		try {
			byte[] buf = new byte[bufSize];
			int len;
			while ( ( len = in.read( buf ) ) > 0 ) {
				out.write( buf, 0, len );
			}
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				System.err.println("WARNING: Could not close stream.");
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			
			// instantiate the client which will set up connection stubs for
			// making SOAP requests on API-A and API-M		
			DemoSOAPClient caller = new DemoSOAPClient(
				"localhost", 8080, 
				"fedoraAdmin", "fedoraAdmin");
				
			//******** STEP 1 : get info about the repository
			System.out.println("Test describeRepository...");
			RepositoryInfo repoinfo = caller.describeRepository();
				
			//******** STEP 2: ingest the demo object
			System.out.println("Test ingest...");
			File ingestFile=new File("TestIngestFiles/obj_test_100.xml");		
			FileInputStream inStream=null;
			try {
				inStream=new FileInputStream(ingestFile);
			} catch (IOException ioe) {
					System.out.println("Error on ingest file inputstream: " + ioe.getMessage());
					ioe.printStackTrace();
			}
			String ingestPID = caller.ingest(inStream, "foxml1.0", "ingest of demo object");
			
			//******** STEP 3: add a datastream to the object
			System.out.println("Test add datastream...");
			String[] altIDs = new String[] {"id1", "id2", "id3"};
			String datastreamID = caller.addDatastream(
				ingestPID, // the object pid
				"MY-DS",   // user-assigned datastream name or id
				altIDs,
				"Add my test datastream",  // user-assigned label
				true, // in version 2.0 always set datastream versioning to true
				"image/gif", // mime type of the datastream content
				"info:fedora/format/myformat", // an optional format URI
				"http://www.cs.cornell.edu/payette/images/sjcomp.gif", // URL for content
				"E",  // type E for External Referenced Datastream
				"A",  // datastream state is A for Active
				"added new datastream MY-DS");  // log message
			
			// modify the datastream using null to indicate which attributes should stay the same.
			System.out.println("First test of modify datastream ...");
			String modDSID = caller.modifyDatastreamByReference(
				ingestPID, // the object pid
				"MY-DS",   // user-assigned datastream name or id
				null, // altIDs (no change)
				"modify-1 of my test datastream",  // new user-assigned label
				true, // versionable
				null, // MIME type (no change)
				null, // new formatURI (no change)
				null, // new URL for content (no change)
				null, // ds state (no change)
				"first modify to change label only", // an optional log message about the change
				 false);  // do not force changes that break ref integrity

			// again, modify the datastream and test setting attributes to empty strings.
			// NOTE:  attempt to set system required attribute to empty will default to no change.
			System.out.println("Second test of modify datastream...");
			modDSID = caller.modifyDatastreamByReference(
				ingestPID, // the object pid
				"MY-DS",   // user-assigned datastream name or id
				new String[0], // altIDs (empty array)
				"",  // new user-assigned label
				true, // versionable
				"", // MIME type (empty)
				"", // new formatURI (empty)
				"", // new URL for content (no change since required field cannot be emptied)
				"", // ds state (no change since required field cannot be emptied)
				"second modify to empty all non-required fields", // an optional log message about the change
				 false);  // do not force changes that break ref integrity
			
			// get datastream history
			
			// purge a datastream in the demo object
			
			// add a disseminator to the demo object
			
			//******** STEP X: export the demo object
			File exportFile = new File("demo-export.xml");
			FileOutputStream outStream = null;
			try {
				outStream = new FileOutputStream(exportFile);
			} catch (IOException ioe) {
					System.out.println("Error on export output stream: " + ioe.getMessage());
					ioe.printStackTrace();
			}		
			byte[] objectXML = caller.export(ingestPID, "foxml1.0", null, outStream);
			
		} catch (Exception e) {
			System.out.println("Exception in main: " +  e.getMessage());
			e.printStackTrace();
		}

	}
	

}
