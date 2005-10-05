package fedora.client.utility.ingest;

import java.io.*;
import fedora.server.utilities.StreamUtility;

/**
 * <p><b>Title:</b> IngestLogger.java</p>
 * <p><b>Description: Methods to create and update a log of ingest activity.
 * 
 */
public class IngestLogger {
    
	public static File newLogFile(String logRootName){
		
		String fileName=logRootName + "-" + System.currentTimeMillis() + ".xml";
		File outFile;
		String fedoraHome=System.getProperty("fedora.home");
		if (fedoraHome=="") {
			// to current dir
			outFile=new File(fileName);
		} else {
			// to client/log
			File logDir=new File(new File(new File(fedoraHome), "client"), "logs");
			if (!logDir.exists()) {
				logDir.mkdir();
			}
			outFile=new File(logDir, fileName);
		}
		return outFile;
	}
    
	public static void openLog(PrintStream log, String rootName) throws Exception {
		log.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		log.println("<" + rootName + ">");
	}

	public static void logFromFile(PrintStream log, File f, String fType, String pid) 
		throws Exception {
		log.println("  <ingested file=\"" + f.getPath() + "\" fType=\"" 
			+ fType + "\" targetPID=\"" + pid + "\" />");
	}

	public static void logFromFile(PrintStream log, File f, char fType, String pid) 
		throws Exception {
		log.println("  <ingested file=\"" + f.getPath() + "\" fType=\"" 
			+ fType + "\" targetPID=\"" + pid + "\" />");
	}

	public static void logFailedFromFile(PrintStream log, File f, String fType, Exception e) 
		throws Exception {
		String message=e.getMessage();
		if (message==null) message=e.getClass().getName();
		log.println("  <failed file=\"" + f.getPath() + "\" fType=\"" + fType + "\">");
		log.println("    " + StreamUtility.enc(message));
		log.println("  </failed>");
	}

	public static void logFailedFromFile(PrintStream log, File f, char fType, Exception e) 
		throws Exception {
		String message=e.getMessage();
		if (message==null) message=e.getClass().getName();
		log.println("  <failed file=\"" + f.getPath() + "\" fType=\"" + fType + "\">");
		log.println("    " + StreamUtility.enc(message));
		log.println("  </failed>");
	}

	public static void logFromRepos(PrintStream log, String sourcePID, String fType, String targetPID) 
		throws Exception {
		log.println("  <ingested sourcePID=\"" + sourcePID 
			+ "\" fType=\"" + fType + "\" targetPID=\"" + targetPID + "\"/>");
	}

	public static void logFromRepos(PrintStream log, String sourcePID, char fType, String targetPID) 
		throws Exception {
		log.println("  <ingested sourcePID=\"" + sourcePID 
			+ "\" fType=\"" + fType + "\" targetPID=\"" + targetPID + "\"/>");
	}

	public static void logFailedFromRepos(PrintStream log, String sourcePID, String fType, Exception e) 
		throws Exception {
		String message=e.getMessage();
		if (message==null) message=e.getClass().getName();
		log.println("  <failed sourcePID=\"" + sourcePID + "\" fType=\"" + fType + "\">");
		log.println("    " + StreamUtility.enc(message));
		log.println("  </failed>");
	}
	
	public static void logFailedFromRepos(PrintStream log, String sourcePID, char fType, Exception e) 
		throws Exception {
		String message=e.getMessage();
		if (message==null) message=e.getClass().getName();
		log.println("  <failed sourcePID=\"" + sourcePID + "\" fType=\"" + fType + "\">");
		log.println("    " + StreamUtility.enc(message));
		log.println("  </failed>");
	}
	
	public static void closeLog(PrintStream log, String rootName) throws Exception {
		log.println("</" + rootName + ">");
		log.close();
	}
}