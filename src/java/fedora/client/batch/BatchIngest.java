import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;

import fedora.client.ingest.AutoIngestor;
class BatchIngest {
	
	String host = null; //"localhost";
	int port = 0; //8080;
	
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
	}
	
	private boolean good2go = false;
	
	final void prep() throws Exception {		
		good2go = true;
	}
		
	final void process() throws Exception {
    		//System.err.println("in BatchIngest.process()");			
		AutoIngestor autoIngestor = new AutoIngestor(host, port);

		//get files from batchDirectory
		File[] files = null; {
			File batchDirectory = new File(objectsPath);
			files = batchDirectory.listFiles();
		}

		
		if (! (pidsFormat.equals("xml") || pidsFormat.equals("text")) ) {
			System.err.println("bad pidsFormat");
		} else {
			PrintStream out = new PrintStream(new FileOutputStream(pidsPath)); //= System.err; 
			int badFileCount = 0;
			int succeededIngestCount = 0;
			int failedIngestCount = 0;
			String logMessage = "another fedora object";
			if (pidsFormat.equals("xml")) {
				out.println("<map-inputids-to-pids>");
			}
			for (int i = 0; i < files.length; i++) {
				if (! files[i].isFile()) {
					badFileCount++;
					System.err.println("batch directory contains unexpected directory or file: " + files[i].getName());
				} else {
					String pid = autoIngestor.ingestAndCommit(new FileInputStream(files[i]), logMessage);
					if ((pid == null) || (pid.equals (""))) {
						failedIngestCount++;
						System.err.println("ingest failed for file: " + files[i].getName());
					} else {
						succeededIngestCount++;
						if (pidsFormat.equals("xml")) {
							out.println("\t<map inputid=\"" + files[i].getName() + "\" pid=\"" + pid + "\" />");
						} else if (pidsFormat.equals("text")) {
							out.println(files[i].getName() + "\t" + pid);
						} else {
							System.err.println("bad pidsFormat");
						}
					}
				}
			}
			if (pidsFormat.equals("xml")) {
				out.println("</map-inputids-to-pids>");
			}
			out.close();
			System.err.println("\n" + (succeededIngestCount + failedIngestCount + badFileCount) + " files processed in this batch");
			System.err.println("\t" + succeededIngestCount + " objects successfully ingested into Fedora");
			System.err.println("\t" + failedIngestCount + " objects unsuccessfully ingest into Fedora");
			System.err.println("\t" + badFileCount + " unexpected files in batch directory");		
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
