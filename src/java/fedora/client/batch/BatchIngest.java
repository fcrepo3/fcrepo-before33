package fedora.client.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.util.Hashtable;
import java.util.Vector;

import fedora.client.ingest.AutoIngestor;

/**
 *
 * <p><b>Title:</b> BatchIngest.java</p>
 * <p><b>Description:</b> </p>
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
 * <p>The entire file consists of original code.  Copyright © 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author wdn5e@virginia.edu
 * @version 1.0
 */
class BatchIngest {

	String host = null; //"localhost";
	int port = 0; //8080;
	String username;
	String password;

	//set by arguments to constructor
	String objectsPath = null;
	String pidsPath = null;
	String pidsFormat = null;

	BatchIngest(Properties optValues) throws Exception {
		objectsPath = optValues.getProperty(BatchTool.OBJECTSPATH);
		pidsPath = optValues.getProperty(BatchTool.PIDSPATH);
		pidsFormat = optValues.getProperty(BatchTool.PIDSFORMAT);
		host = optValues.getProperty(BatchTool.SERVERFQDN);
		String serverPortAsString = optValues.getProperty(BatchTool.SERVERPORT);
		username = optValues.getProperty(BatchTool.USERNAME);
		password = optValues.getProperty(BatchTool.PASSWORD);
		if (! BatchTool.argOK(objectsPath)) {
			System.err.println("objectsPath required");
			throw new Exception();
		}
		if (! BatchTool.argOK(pidsPath)) {
			System.err.println("pidsPath required");
			throw new Exception();
		}
		if (! BatchTool.argOK(pidsFormat)) {
			System.err.println("pids-format required");
			throw new Exception();
		}
		if (! BatchTool.argOK(host)) {
			System.err.println("server-fqdn required");
			throw new Exception();
		}
		if (! BatchTool.argOK(serverPortAsString)) {
			System.err.println("server-port required");
			throw new Exception();
		} else {
			port = Integer.parseInt(serverPortAsString);
		}
		if (! BatchTool.argOK(username)) {
			System.err.println("username required");
			throw new Exception();
		}
		if (! BatchTool.argOK(password)) {
			System.err.println("password required");
			throw new Exception();
		}

	}

	private boolean good2go = false;

	final void prep() throws Exception {
		good2go = true;
	}

	private Hashtable pidMaps = null;
	private Vector keys = null;

	/* package */ Hashtable getPidMaps() {
		return pidMaps;
	}

	/* package */ Vector getKeys() {
		return keys;
	}

	final void process() throws Exception {
    		//System.err.println("in BatchIngest.process()");
		pidMaps = new Hashtable();
		keys = new Vector();
		AutoIngestor autoIngestor = new AutoIngestor(host, port, username, password);

		//get files from batchDirectory
		File[] files = null; {
			File batchDirectory = new File(objectsPath);
			files = batchDirectory.listFiles();
		}


		if (! (pidsFormat.equals("xml") || pidsFormat.equals("text")) ) {
			System.err.println("bad pidsFormat");
		} else {
			int badFileCount = 0;
			int succeededIngestCount = 0;
			int failedIngestCount = 0;
			String logMessage = "another fedora object";
			for (int i = 0; i < files.length; i++) {
				if (! files[i].isFile()) {
					badFileCount++;
					System.err.println("batch directory contains unexpected directory or file: " + files[i].getName());
				} else {
					String pid = null;
					try {
						pid = autoIngestor.ingestAndCommit(new FileInputStream(files[i]), logMessage);
					} catch (Exception e) {
						System.err.println("ingest failed for: " + files[i].getName());
						System.err.println("\t" + e.getClass().getName());
						System.err.println("\t" + e.getMessage());
						System.err.println("===BATCH HAS FAILED===");
						System.err.println("consider manually backing out " +
						"any objects which were already successfully ingested in this batch");
						throw e;
					}
					if ((pid == null) || (pid.equals (""))) {
						failedIngestCount++;
						System.err.println("ingest failed for: " + files[i].getName());
					} else {
						succeededIngestCount++;
						System.out.println("ingest succeeded for: " + files[i].getName());
						keys.add(files[i].getName());
						pidMaps.put(files[i].getName(),pid);
					}
				}
			}
			System.err.println("\n" + "Batch Ingest Summary");
			System.err.println("\n" + (succeededIngestCount + failedIngestCount + badFileCount) + " files processed in this batch");
			System.err.println("\t" + succeededIngestCount + " objects successfully ingested into Fedora");
			System.err.println("\t" + failedIngestCount + " objects failed");
			System.err.println("\t" + badFileCount + " unexpected files in directory");
			System.err.println("\t" + (files.length - (succeededIngestCount + failedIngestCount + badFileCount)) + " files ignored after error");
		}
	}

	public static final void main(String[] args) {
		try {
			Properties miscProperties = new Properties();
			miscProperties.load(new FileInputStream("c:\\batchdemo\\batchtool.properties"));
			BatchIngest batchIngest = new BatchIngest(miscProperties);
			batchIngest.prep();
			batchIngest.process();
		} catch (Exception e) {
		}
	}
}
