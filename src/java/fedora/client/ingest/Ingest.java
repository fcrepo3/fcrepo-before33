package fedora.client.ingest;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

import fedora.client.Administrator;
import fedora.client.APIAStubFactory;
import fedora.client.APIMStubFactory;
import fedora.client.FTypeDialog;
import fedora.client.ObjectFormatDialog;
import fedora.client.export.AutoExporter;
import fedora.client.search.AutoFinder;

import fedora.server.management.FedoraAPIM;
import fedora.server.access.FedoraAPIA;
import fedora.server.utilities.StreamUtility;

import fedora.server.types.gen.Condition;
import fedora.server.types.gen.ComparisonOperator;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.FieldSearchResult;
import fedora.server.types.gen.ListSession;
import fedora.server.types.gen.ObjectFields;
import fedora.server.access.FedoraAPIA;
import fedora.server.types.gen.RepositoryInfo;

/**
 * Ingests from filesystem or repository.
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
 */
public class Ingest {

    public static int ONE_FROM_FILE=0;
    public static int MULTI_FROM_DIR=1;
    public static int ONE_FROM_REPOS=2;
    public static int MULTI_FROM_REPOS=3;

    public static String LAST_PATH;

    private static String s_rootName;
    public static String s_logPath;
    private static PrintStream s_log;
    private static int s_failedCount;

    // launch interactively via Administrator.java
	public Ingest(int kind) {
        s_failedCount=0;
        boolean wasMultiple=false;
        try {
            if (kind==ONE_FROM_FILE) {
                JFileChooser browse=new JFileChooser(Administrator.getLastDir());
                int returnVal = browse.showOpenDialog(Administrator.getDesktop());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = browse.getSelectedFile();
                    Administrator.setLastDir(file.getParentFile());
					ObjectFormatDialog fmtDialog = 
						new ObjectFormatDialog("Select XML Format of Ingest File(s)");
					if (fmtDialog.getSelection()!=null) {
						String ingestFormat=fmtDialog.getSelection(); 
	                    String pid=oneFromFile(file, ingestFormat, 
	                    	Administrator.APIA, Administrator.APIM, null);
	                    JOptionPane.showMessageDialog(Administrator.getDesktop(),
	                        "Ingest succeeded.  PID='" + pid + "'.");
					}
                }
            } else if (kind==MULTI_FROM_DIR) {
                wasMultiple=true;
                JFileChooser browse=new JFileChooser(Administrator.getLastDir());
                browse.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = browse.showOpenDialog(Administrator.getDesktop());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = browse.getSelectedFile();
                    Administrator.setLastDir(file);
                    FTypeDialog dlg=new FTypeDialog();
                    if (dlg.getResult()!=null) {
                        String fTypes=dlg.getResult();
                        openLog("ingest-from-dir");
                        long st=System.currentTimeMillis();
						ObjectFormatDialog fmtDialog = 
							new ObjectFormatDialog("Select XML Format of Ingest File(s)");
						if (fmtDialog.getSelection()!=null) {
							String ingestFormat=fmtDialog.getSelection(); 
	                        String[] pids=multiFromDirectory(file, ingestFormat, fTypes, 
	                        	Administrator.APIA, Administrator.APIM, null);
	                        long et=System.currentTimeMillis();
	                        JOptionPane.showMessageDialog(Administrator.getDesktop(),
	                            pids.length + " objects successfully ingested.\n"
	                            + s_failedCount + " objects failed.\n"
	                            + "Time elapsed: " + getDuration(et-st));
	                         //   Details are in File->Advanced->STDOUT/STDERR window.");
						}
                    }
                }
            } else if (kind==ONE_FROM_REPOS) {
                SourceRepoDialog sdlg=new SourceRepoDialog();
				// First, determine the default export format of the source repo.
				// For backward compatibility with pre-2.0 repositories, 
				// assume the "metslikefedora1" format.
				RepositoryInfo repoinfo = sdlg.getAPIA().describeRepository();
				String sourceExportFormat = null;
				StringTokenizer stoken = new StringTokenizer(repoinfo.getRepositoryVersion(), ".");
				if (new Integer(stoken.nextToken()).intValue() < 2){
					sourceExportFormat = "metslikefedora1";
				} else {
					sourceExportFormat = repoinfo.getDefaultExportFormat();
				}
                if (sdlg.getAPIA()!=null) {
                    String pid=JOptionPane.showInputDialog("Enter the PID of the object to ingest.");
                    if (pid!=null && !pid.equals("")) {
                       pid=oneFromRepository(sdlg.getHost(),
                                             sdlg.getPort(),
                                             sdlg.getAPIA(),
                                             sdlg.getAPIM(),
											 sourceExportFormat,
                                             pid,
											 Administrator.APIA,
                                             Administrator.APIM,
                                             null);
                       JOptionPane.showMessageDialog(Administrator.getDesktop(),
                               "Ingest succeeded.  PID=" + pid);
                    }
                }
            } else if (kind==MULTI_FROM_REPOS) {
                wasMultiple=true;
                SourceRepoDialog sdlg=new SourceRepoDialog();
				// First, determine the default export format of the source repo.
				// For backward compatibility with pre-2.0 repositories, 
				// assume the "metslikefedora1" format.
				RepositoryInfo repoinfo = sdlg.getAPIA().describeRepository();
				String sourceExportFormat = null;
				StringTokenizer stoken = new StringTokenizer(repoinfo.getRepositoryVersion(), ".");
				if (new Integer(stoken.nextToken()).intValue() < 2){
					sourceExportFormat = "metslikefedora1";
				} else {
					sourceExportFormat = repoinfo.getDefaultExportFormat();
				}
                if (sdlg.getAPIA()!=null) {
                    FTypeDialog dlg=new FTypeDialog();
                    if (dlg.getResult()!=null) {
                        // looks ok... do the request
                        String fTypes=dlg.getResult();
                        long st=System.currentTimeMillis();
                        openLog("ingest-from-repos");
                        String[] pids=multiFromRepository(sdlg.getHost(),
                                                          sdlg.getPort(),
														  sdlg.getAPIA(),
                                                          sdlg.getAPIM(),
                                                          sourceExportFormat,
                                                          fTypes,
														  Administrator.APIA,
                                                          Administrator.APIM,
                                                          null);
                        long et=System.currentTimeMillis();
                        JOptionPane.showMessageDialog(Administrator.getDesktop(),
                            pids.length + " objects successfully ingested.\n"
                            + s_failedCount + " objects failed.\n"
                            + "Time elapsed: " + getDuration(et-st));
                    }
                }
            }
        } catch (Exception e) {
            String msg=e.getMessage();
            if (msg==null) {
                msg=e.getClass().getName();
            }
            JOptionPane.showMessageDialog(Administrator.getDesktop(),
                    msg,
                    "Ingest Failure",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (s_log!=null && wasMultiple) {
                    closeLog();
                    int n = JOptionPane.showConfirmDialog(Administrator.getDesktop(),
                        "A detailed log file was created at\n"
                        + s_logPath + "\n\n"
                        + "View it now?",
                        "View Ingest Log?",
                        JOptionPane.YES_NO_OPTION);
                    if (n==JOptionPane.YES_OPTION) {
                        JTextComponent textEditor=new JTextArea();
                        textEditor.setFont(new Font("monospaced", Font.PLAIN, 12));
                        textEditor.setText(fileAsString(s_logPath));
                        textEditor.setCaretPosition(0);
                        textEditor.setEditable(false);
                        JInternalFrame viewFrame=new JInternalFrame("Viewing " + s_logPath, true, true, true, true);
                        viewFrame.setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/standard/general/Edit16.gif")));
                        viewFrame.getContentPane().add(new JScrollPane(textEditor));
                        viewFrame.setSize(720,520);
                        viewFrame.setVisible(true);
                        Administrator.getDesktop().add(viewFrame);
                        try {
                            viewFrame.setSelected(true);
                        } catch (java.beans.PropertyVetoException pve) {}

                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Administrator.getDesktop(),
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static String fileAsString(String path)
            throws Exception {
        StringBuffer buffer = new StringBuffer();
        InputStream fis=new FileInputStream(path);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        Reader in = new BufferedReader(isr);
        int ch;
        while ((ch = in.read()) > -1) {
            buffer.append((char)ch);
        }
        in.close();
        return buffer.toString();
    }

    private static String getDuration(long millis) {
        long tsec=millis/1000;
        long h=tsec/60/60;
        long m=(tsec - (h*60*60))/60;
        long s=(tsec - (h*60*60) - (m*60));
        StringBuffer out=new StringBuffer();
        if (h>0) {
            out.append(h + " hour");
            if (h>1) out.append('s');
        }
        if (m>0) {
            if (h>0) out.append(", ");
            out.append(m + " minute");
            if (m>1) out.append('s');
        }
        if (s>0 || (h==0 && m==0)) {
            if (h>0 || m>0) out.append(", ");
            out.append(s + " second");
            if (s!=1) out.append('s');
        }
        return out.toString();
    }

    // if logMessage is null, will use original path in logMessage
    public static String oneFromFile(File file, 
    								 String ingestFormat, 
    								 FedoraAPIA targetRepoAPIA, 
    								 FedoraAPIM targetRepoAPIM, 
    								 String logMessage)
            throws Exception {
        System.out.println("Ingesting from file " + file.getPath());
        LAST_PATH=file.getPath();
        String pid=AutoIngestor.ingestAndCommit(targetRepoAPIA, 
        									    targetRepoAPIM,
                                                new FileInputStream(file),
                                                ingestFormat,
                                                getMessage(logMessage, file));
        return pid;
    }

    // if logMessage is null, will use original path in logMessage
    public static String[] multiFromDirectory(File dir, String ingestFormat, String fTypes,
                                              FedoraAPIA targetRepoAPIA, FedoraAPIM targetRepoAPIM,
                                              String logMessage)
            throws Exception {
        String tps=fTypes.toUpperCase();
        Set toIngest;
        HashSet pidSet=new HashSet();
        if (tps.indexOf("D")!=-1) {
            toIngest=getFiles(dir, "FedoraBDefObject");
            System.out.println("Found " + toIngest.size() + " behavior definitions.");
            pidSet.addAll(
            	ingestAll("D", toIngest, ingestFormat, targetRepoAPIA, targetRepoAPIM, logMessage));
        }
        if (tps.indexOf("M")!=-1) {
            toIngest=getFiles(dir, "FedoraBMechObject");
            System.out.println("Found " + toIngest.size() + " behavior mechanisms.");
            pidSet.addAll(
            	ingestAll("M", toIngest, ingestFormat, targetRepoAPIA, targetRepoAPIM, logMessage));
        }
        if (tps.indexOf("O")!=-1) {
            toIngest=getFiles(dir, "FedoraObject");
            System.out.println("Found " + toIngest.size() + " data objects.");
            pidSet.addAll(
            	ingestAll("O", toIngest, ingestFormat, targetRepoAPIA, targetRepoAPIM, logMessage));
        }
        Iterator iter=pidSet.iterator();
        String[] pids=new String[pidSet.size()];
        int i=0;
        while (iter.hasNext()) {
            pids[i++]=(String) iter.next();
        }
        return pids;
    }
    
	// if logMessage is null, will make informative one up
	public static String oneFromRepository(String sourceHost,
										   int sourcePort,
										   FedoraAPIA sourceRepoAPIA,
										   FedoraAPIM sourceRepoAPIM,
										   String sourceExportFormat,
										   String pid,
										   FedoraAPIA targetRepoAPIA,
										   FedoraAPIM targetRepoAPIM,
										   String logMessage)
			throws Exception {
		System.out.println("Ingesting " + pid + " from source repository.");

		// EXPORT from source repository
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		AutoExporter.export(sourceRepoAPIA, sourceRepoAPIM,
							pid,
							null, // take the default export format of source repo
							out,
							false);
		
		// fix the host-specific references before ingesting (except "M" datastreams)
		StringBuffer fixed=new StringBuffer();
		BufferedReader in=new BufferedReader(
			new InputStreamReader(new ByteArrayInputStream(out.toByteArray())));
		String line;
		while ( (line=in.readLine()) != null ) {
			if (line.indexOf("fedora-system:3")==-1) {
				// if the line doesn't have a managed datastream reference,
				// replace all occurances of sourceHost:sourcePort with
				// local.fedora.server
				fixed.append(line.replaceAll(
						sourceHost + ":" + sourcePort, "local.fedora.server"));
			} else {
				fixed.append(line);
			}
			fixed.append("\n");
		}
		in.close();

		// INGEST into target repository		
		String realLogMessage=logMessage;
		if (realLogMessage==null) {
			realLogMessage="Ingested from source repository with pid " + pid;
		}
		return AutoIngestor.ingestAndCommit(targetRepoAPIA,
											targetRepoAPIM,
											new ByteArrayInputStream(
											 fixed.toString().getBytes("UTF-8")),
											sourceExportFormat,
											realLogMessage);
	}

/*
	public static String[] multiFromRepository(String sourceHost,
											   int sourcePort,
											   String sourceUser,
											   String sourcePass,
											   String fTypes,
											   FedoraAPIM targetRepos,
											   String logMessage)
			throws Exception {
		FedoraAPIA sourceRepoAPIA=APIAStubFactory.getStub(sourceHost,
														sourcePort,
														sourceUser,
														sourcePass);
		FedoraAPIM sourceRepoAPIM=APIMStubFactory.getStub(sourceHost,
														sourcePort,
														sourceUser,
														sourcePass);
		return multiFromRepository(sourceHost, sourcePort, 
			sourceRepoAPIA, sourceRepoAPIM, fTypes, targetRepos, logMessage);
   }
*/

   public static String[] multiFromRepository(String sourceHost,
											  int sourcePort,
											  FedoraAPIA sourceRepoAPIA,
											  FedoraAPIM sourceRepoAPIM,
											  String sourceExportFormat,
											  String fTypes,
											  FedoraAPIA targetRepoAPIA,
											  FedoraAPIM targetRepoAPIM,
											  String logMessage)
			throws Exception {
		String tps=fTypes.toUpperCase();
		Set pidSet=new HashSet();
		if (tps.indexOf("D")!=-1) {
			pidSet.addAll(ingestAllFromRepository(
								sourceHost,
								sourcePort,
								sourceRepoAPIA,
								sourceRepoAPIM,
								sourceExportFormat,
								"D",
								targetRepoAPIA,
								targetRepoAPIM,
								logMessage));
		}
		if (tps.indexOf("M")!=-1) {
			pidSet.addAll(ingestAllFromRepository(
								sourceHost,
								sourcePort,
								sourceRepoAPIA,
								sourceRepoAPIM,
								sourceExportFormat,
								"M",
								targetRepoAPIA,
								targetRepoAPIM,
								logMessage));
		}
		if (tps.indexOf("O")!=-1) {
			pidSet.addAll(ingestAllFromRepository(
								sourceHost,
								sourcePort,
								sourceRepoAPIA,
								sourceRepoAPIM,
								sourceExportFormat,
								"O",
								targetRepoAPIA,
								targetRepoAPIM,
								logMessage));
		}
		Iterator iter=pidSet.iterator();
		String[] pids=new String[pidSet.size()];
		int i=0;
		while (iter.hasNext()) {
			pids[i++]=(String) iter.next();
		}
		return pids;
	}

    private static Set ingestAll(String fType,
                                 Set fileSet,
                                 String ingestFormat,
								 FedoraAPIA targetRepoAPIA,
                                 FedoraAPIM targetRepoAPIM,
                                 String logMessage)
            throws Exception {
        HashSet set=new HashSet();
        Iterator iter=fileSet.iterator();
        while (iter.hasNext()) {
            File f=(File) iter.next();
            try {
                String pid=oneFromFile(f, ingestFormat, targetRepoAPIA, targetRepoAPIM, logMessage);
                // success...log it
                logFromFile(f, fType, pid);
                set.add(pid);
            } catch (Exception e) {
                // failed... just log it and continue
                s_failedCount++;
                logFailedFromFile(f, fType, e);
            }
        }
        return set;
    }
    
	private static Set ingestAllFromRepository(String sourceHost,
								 int sourcePort,
								 FedoraAPIA sourceRepoAPIA,
								 FedoraAPIM sourceRepoAPIM,
								 String sourceExportFormat,
								 String fType,
								 FedoraAPIA targetRepoAPIA,
								 FedoraAPIM targetRepoAPIM,
								 String logMessage)
			throws Exception {
		// get pids with fType='$fType', adding all to set at once,
		// then singleFromRepository(sourceRepos, pid, targetRepos, logMessage)
		// for each, then return the set
		HashSet set=new HashSet();
		String[] res=AutoFinder.getPIDs(sourceHost, sourcePort, "fType=" + fType);
		for (int i=0; i<res.length; i++) set.add(res[i]);
		String friendlyName="data objects";
		if (fType.equals("D"))
			friendlyName="behavior definitions";
		if (fType.equals("M"))
			friendlyName="behavior mechanisms";
		System.out.println("Found " + set.size() + " " + friendlyName + " to ingest.");
		Iterator iter=set.iterator();
		HashSet successSet=new HashSet();
		while (iter.hasNext()) {
			String pid=(String) iter.next();
			try {
				String newPID=oneFromRepository(sourceHost,
												sourcePort,
												sourceRepoAPIA,
												sourceRepoAPIM,
											    sourceExportFormat,
												pid,
												targetRepoAPIA,
												targetRepoAPIM,
												logMessage);
				successSet.add(newPID);
				logFromRepos(pid, fType, newPID);
			} catch (Exception e) {
				s_failedCount++;
				logFailedFromRepos(pid, fType, e);
			}
		}
		return successSet;
	}

    private static void openLog(String rootName) throws Exception {
        s_rootName=rootName;
        String fileName=s_rootName + "-" + System.currentTimeMillis() + ".xml";
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
        s_logPath=outFile.getPath();
        s_log=new PrintStream(new FileOutputStream(outFile), true, "UTF-8");
        s_log.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        s_log.println("<" + s_rootName + ">");
    }

    private static void logFromFile(File f, String fType, String pid) throws Exception {
        s_log.println("  <ingested file=\"" + f.getPath() + "\" fType=\"" + fType + "\" targetPID=\"" + pid + "\" />");
    }

    private static void logFailedFromFile(File f, String fType, Exception e) throws Exception {
        String message=e.getMessage();
        if (message==null) message=e.getClass().getName();
        s_log.println("  <failed file=\"" + f.getPath() + "\" fType=\"" + fType + "\">");
        s_log.println("    " + StreamUtility.enc(message));
        s_log.println("  </failed>");
    }

    private static void closeLog() throws Exception {
        s_log.println("</" + s_rootName + ">");
        s_log.close();
        // force garbage collection and ensure s_log is null immediately following close
        s_log=null;
    }

    private static Set getFiles(File dir, String fTypeString)
            throws Exception {
        if (!dir.isDirectory()) {
            throw new IOException("Not a directory: " + dir.getPath());
        }
        HashSet set=new HashSet();
        File[] files=dir.listFiles();
        for (int i=0; i<files.length; i++) {
            if (files[i].isDirectory()) {
                set.addAll(getFiles(files[i], fTypeString));
            } else {
                // if the file is a candidate, add it
                BufferedReader in=new BufferedReader(new FileReader(files[i]));
                boolean isCandidate=false;
                String line;
                while ( (line=in.readLine()) != null ) {
                    if (line.indexOf(fTypeString)!=-1) {
                        isCandidate=true;
                    }
                }
                if (isCandidate) {
                    set.add(files[i]);
                }
            }
        }
        return set;
    }

    private static void logFromRepos(String sourcePID, String fType, String targetPID) throws Exception {
        s_log.println("  <ingested sourcePID=\"" + sourcePID + "\" fType=\"" + fType + "\" targetPID=\"" + targetPID + "\" />");
    }

    private static void logFailedFromRepos(String sourcePID, String fType, Exception e) throws Exception {
        String message=e.getMessage();
        if (message==null) message=e.getClass().getName();
        s_log.println("  <failed sourcePID=\"" + sourcePID + "\" fType=\"" + fType + "\">");
        s_log.println("    " + StreamUtility.enc(message));
        s_log.println("  </failed>");
    }

    private static String getMessage(String logMessage, File file) {
        if (logMessage!=null) return logMessage;
        return "Ingested from local file " + file.getPath();
    }

	/**
	 * Print error message and show usage for command-line interface.
	 */
	public static void badArgs(String msg) {
		System.err.println("Command: fedora-ingest");
		System.err.println();
		System.err.println("Summary: Ingests one or more objects into a Fedora repository, from either");
		System.err.println("         the local filesystem or another Fedora repository.");
		System.err.println();
		System.err.println("Syntax:");
		System.err.println("  fedora-ingest f[ile] INPATH FORMAT THST:TPRT TUSR TPSS [LOG]");
		System.err.println("  fedora-ingest d[ir] INPATH FORMAT FTYPS THST:TPRT TUSR TPSS [LOG]");
		System.err.println("  fedora-ingest r[epos] SHST:SPRT SUSR SPSS PID|FTYPS THST:TPRT TUSR TPSS [LOG]");
		System.err.println();
		System.err.println("Where:");
		System.err.println("  INPATH     is the local file or directory name that is ingest source.");
		System.err.println("  FORMAT     is a string value (either 'foxml1.0' or 'metslikefedora1')");
		System.err.println("             which indicates the XML format of the ingest file(s)");
		System.err.println("  FTYPS      is any combination of the characters O, D, and M, specifying");
		System.err.println("             which Fedora object type(s) should be ingested. O=data objects,");
		System.err.println("             D=behavior definitions, and M=behavior mechanisms.");
		System.err.println("  PID        is the id of the object to ingest from the source repository.");
		System.err.println("  SHST/THST  is the source or target repository's hostname.");
		System.err.println("  SPRT/TPRT  is the source or target repository's port number.");
		System.err.println("  SUSR/TUSR  is the id of the source or target repository user.");
		System.err.println("  SPSS/TPSS  is the password of the source or target repository user.");
		System.err.println("  LOG        is the optional log message.  If unspecified, the log message");
		System.err.println("             will indicate the source filename or repository of the object(s).");
		System.err.println();
		System.err.println("Examples:");
		System.err.println("fedora-ingest f obj1.xml foxml1.0 myrepo.com:80 jane jpw");
		System.err.println();
		System.err.println("  Ingests obj1.xml (encoded in foxml1.0 format) from the");
		System.err.println("  current directory into the repository at myrepo.com:80");
		System.err.println("  as user 'jane' with password 'jpw'.");
		System.err.println("  The logmessage will be system-generated, indicating");
		System.err.println("  the source path+filename.");
		System.err.println();
		System.err.println("fedora-ingest d c:\\archive foxml1.0 M myrepo.com:80 jane janepw \"\"");
		System.err.println();
		System.err.println("  Traverses entire directory structure of c:\\archive, and ingests ");
		System.err.println("  any file that looks like a behavior mechanism object (M). ");
		System.err.println("  It assumes all files will be in the XML format 'foxml1.0'");
		System.err.println("  and will fail on ingests of files that are not of this format.");
		System.err.println("  All log messages will be the quoted string.");
		System.err.println();
		System.err.println("fedora-ingest d c:\\archive foxml1.0 ODM myrepo.com:80 jane janepw \"for jane\"");
		System.err.println();
		System.err.println("  Same as above, but ingests all three types of objects (O,D,M).");
		System.err.println();
		System.err.println("fedora-ingest r jrepo.com:8081 mike mpw demo:1 myrepo.com:80 jane jpw \"\"");
		System.err.println();
		System.err.println("  Exports the object whose pid is 'demo:1' from the source repository");
		System.err.println("  'srcrepo.com:8081' and ingests it into the target repository 'myrepo.com:80'.");
		System.err.println("  The object will be exported from the source repository in whatever format");
		System.err.println("  the source has configured as its default export format.");
		System.err.println("  All log messages will be empty.");
		System.err.println();
		System.err.println("ERROR  : " + msg);
		System.exit(1);
	}

    /**
     * Command-line interface for doing ingests.
     */
    public static void main(String[] args) {
        try {
            if (args.length<1) {
                Ingest.badArgs("No arguments entered!");
            }
            char kind=args[0].toLowerCase().charAt(0);
            if (kind=='f') {
				// USAGE: fedora-ingest f[ile] INPATH FORMAT THST:TPRT TUSR TPSS [LOG]
                if (args.length<6 || args.length>7) {
                    Ingest.badArgs("Wrong number of arguments for file ingest.");
                    System.out.println(
					"USAGE: fedora-ingest f[ile] INPATH FORMAT THST:TPRT TUSR TPSS [LOG]");
                }
                File f=new File(args[1]);
                String ingestFormat = args[2];
                String logMessage=null;
                if (args.length==7) {
                    logMessage=args[6];
                }
                String[] hp=args[3].split(":");
				FedoraAPIA targetRepoAPIA=
						APIAStubFactory.getStub(hp[0],
												Integer.parseInt(hp[1]),
												args[4],
												args[5]);
                FedoraAPIM targetRepoAPIM=
                        APIMStubFactory.getStub(hp[0],
                                                Integer.parseInt(hp[1]),
                                                args[4],
                                                args[5]);
                String pid = Ingest.oneFromFile(f, ingestFormat, targetRepoAPIA, targetRepoAPIM, logMessage);
                if (pid==null){
					System.out.print("ERROR: No object ingested.  Check log for errors.");
					System.out.println();
					System.out.println("A detailed log is at " + Ingest.s_logPath);
                } else {
					System.out.println("Ingested PID: " + pid);                	
                }
            } else if (kind=='d') {
				// USAGE: fedora-ingest d[ir] INPATH FORMAT FTYPS THST:TPRT TUSR TPSS [LOG]
                if (args.length<7 || args.length>8) {
                    Ingest.badArgs("Wrong number of arguments for directory ingest.");
                    System.out.println(
						"USAGE: fedora-ingest d[ir] INPATH FORMAT FTYPS THST:TPRT TUSR TPSS [LOG]");
                }
                File d=new File(args[1]);
                String ingestFormat = args[2];
                String logMessage=null;
                if (args.length==8) {
                    logMessage=args[7];
                }
                String[] hp=args[4].split(":");
				FedoraAPIA targetRepoAPIA=
						APIAStubFactory.getStub(hp[0],
												Integer.parseInt(hp[1]),
												args[5],
												args[6]);
                FedoraAPIM targetRepoAPIM=
                        APIMStubFactory.getStub(hp[0],
                                                Integer.parseInt(hp[1]),
                                                args[5],
                                                args[6]);
                Ingest.openLog("ingest-from-dir");
                String[] pids=Ingest.multiFromDirectory(d,
                										ingestFormat,
                                                        args[3],
                                                        targetRepoAPIA,
                                                        targetRepoAPIM,
                                                        logMessage);
                if (pids.length>0) {
                    for (int i=0; i<pids.length; i++) {
						System.out.println("Ingested PID: " + pids[i]);
                    }
                } else {
					System.out.print("ERROR: No objects ingested.  Check log for errors.");
                }
				Ingest.closeLog();
                System.out.println();
                System.out.println("WARNING: check log for possible ingest failures.");
				System.out.println();
                System.out.println("A detailed log is at " + Ingest.s_logPath);
            } else if (kind=='r') {
            	// USAGE: fedora-ingest r[epos] SHST:SPRT SUSR SPSS PID|FTYPS THST:TPRT TUSR TPSS [LOG]
                if (args.length<8 || args.length>9) {
                    Ingest.badArgs("Wrong number of arguments for repository ingest.");
                }
                String logMessage=null;
                if (args.length==9) {
                    logMessage=args[8];
                }
                String[] hp=args[1].split(":");
				FedoraAPIA sourceRepoAPIA=
						APIAStubFactory.getStub(hp[0],
												Integer.parseInt(hp[1]),
												args[2],
												args[3]);
                FedoraAPIM sourceRepoAPIM=
                        APIMStubFactory.getStub(hp[0],
                                                Integer.parseInt(hp[1]),
                                                args[2],
                                                args[3]);
                hp=args[5].split(":");
				FedoraAPIA targetRepoAPIA=
						APIAStubFactory.getStub(hp[0],
												Integer.parseInt(hp[1]),
												args[6],
												args[7]);
                FedoraAPIM targetRepoAPIM=
                        APIMStubFactory.getStub(hp[0],
                                                Integer.parseInt(hp[1]),
                                                args[6],
                                                args[7]);
				// First, determine the default export format of the source repo.
				// For backward compatibility with pre-2.0 repositories, 
				// assume the "metslikefedora1" format.
				RepositoryInfo repoinfo = sourceRepoAPIA.describeRepository();
				System.out.println("Ingest: exporting from a source repo version " + repoinfo.getRepositoryVersion());
				String sourceExportFormat = null;
				StringTokenizer stoken = new StringTokenizer(repoinfo.getRepositoryVersion(), ".");
				if (new Integer(stoken.nextToken()).intValue() < 2){
					sourceExportFormat = "metslikefedora1";
					System.out.println("Ingest: source repos is using 'metslikefedora1' as export.");
				} else {
					sourceExportFormat = repoinfo.getDefaultExportFormat();
					System.out.println("Ingest: source repos default export format is " + sourceExportFormat);
				}
				
                if (args[4].indexOf(":")!=-1) {
                    // single object
                    String successfulPID = Ingest.oneFromRepository(hp[0],
                                                       Integer.parseInt(hp[1]),
													   sourceRepoAPIA,
                                                       sourceRepoAPIM,
													   sourceExportFormat,
                                                       args[4],
                                                       targetRepoAPIA,
                                                       targetRepoAPIM,
                                                       logMessage);
					if (successfulPID==null){
						System.out.print("ERROR: No object ingested.  Check log for errors.");
						System.out.println();
						System.out.println("A detailed log is at " + Ingest.s_logPath);
					} else {
						System.out.println("Ingested PID: " + successfulPID);
					}
                } else {
                    // multi-object
                    hp=args[1].split(":");
                    Ingest.openLog("ingest-from-repository");
					String[] pids=Ingest.multiFromRepository(hp[0],
													  Integer.parseInt(hp[1]),
													  sourceRepoAPIA,
													  sourceRepoAPIM,
													  sourceExportFormat,
													  args[4],
													  targetRepoAPIA,
													  targetRepoAPIM,
													  logMessage);
                    if (pids.length>0) {
                        for (int i=0; i<pids.length; i++) {
							System.out.println("Ingested PID: " + pids[i]);
                        }
                    } else {
                        System.out.print("ERROR: No objects ingested.  Check log for errors.");
                    }
					Ingest.closeLog();
                    System.out.println();
					System.out.println("WARNING: check log for possible ingest failures.");
					System.out.println();
                    System.out.println("A detailed log is at " + Ingest.s_logPath);
                }

            } else {
                Ingest.badArgs("First argument must start with f, d, or r.");
            }
        } catch (Exception e) {
            System.err.print("Error  : ");
            if (e.getMessage()==null) {
                e.printStackTrace();
            } else {
                System.err.print(e.getMessage());
            }
            System.err.println();
            if (Ingest.LAST_PATH!=null) {
                System.out.println("(Last attempted file was " + Ingest.LAST_PATH + ")");
            }
        }
    }

}