package fedora.client.utility.ingest;

import java.io.*;
import java.util.*;

import fedora.client.FedoraClient;
import fedora.client.utility.export.AutoExporter;
import fedora.client.utility.ingest.AutoIngestor;
import fedora.client.utility.AutoFinder;

import fedora.server.management.FedoraAPIM;
import fedora.server.access.FedoraAPIA;

import fedora.server.types.gen.Condition;
import fedora.server.types.gen.ComparisonOperator;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.FieldSearchResult;
import fedora.server.types.gen.ObjectFields;
import fedora.server.types.gen.RepositoryInfo;

import fedora.utilities.FileComparator;

/**
 * <p><b>Title:</b> Ingest.java</p>
 * <p><b>Description: A utility class to initiate ingest of one or more objects.
 * This class provides static utility methods, and it is also called by
 * command line utilities.
 */
public class Ingest {

    public static String LAST_PATH;

    private static FileComparator _FILE_COMPARATOR = new FileComparator();

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

    /**************************************************************************

                            Ingest from directory

    **************************************************************************/
    
    // if logMessage is null, will use original path in logMessage
    public static void multiFromDirectory(File dir, 
                                          String ingestFormat, 
                                          String fTypes,
                                          FedoraAPIA targetRepoAPIA, 
                                          FedoraAPIM targetRepoAPIM,
                                          String logMessage, 
                                          PrintStream log, 
                                          IngestCounter c) throws Exception {
        String tps = fTypes.toUpperCase();
        if (tps.indexOf("D") != -1) {
            multiFromDirectory(dir, ingestFormat, 'D', targetRepoAPIA, 
                               targetRepoAPIM, logMessage, log, c);
        }
        if (tps.indexOf("M")!=-1) {
            multiFromDirectory(dir, ingestFormat, 'M', targetRepoAPIA, 
                               targetRepoAPIM, logMessage, log, c);
        }
        if (tps.indexOf("O")!=-1) {
            multiFromDirectory(dir, ingestFormat, 'O', targetRepoAPIA, 
                               targetRepoAPIM, logMessage, log, c);
        }
    }

    private static String getSearchString(char fType) {
        if (fType == 'D') {
            return "FedoraBDefObject";
        } else if (fType == 'M') {
            return "FedoraBMechObject";
        } else if (fType == 'O') {
            return "FedoraObject";
        } else {
            throw new RuntimeException("Unrecognized fType: " + fType);
        }
    }

    public static void multiFromDirectory(File dir,
                                          String ingestFormat,
                                          char fType,
                                          FedoraAPIA targetRepoAPIA,
                                          FedoraAPIM targetRepoAPIM,
                                          String logMessage,
                                          PrintStream log,
                                          IngestCounter c) throws Exception {
        String searchString = getSearchString(fType);
        System.out.println("Ingesting all " + searchString + "s in " + dir.getPath());
        multiFromDirectory(dir, ingestFormat, fType, searchString, 
                           targetRepoAPIA, targetRepoAPIM, logMessage, log, c);
    }

    private static void multiFromDirectory(File dir,
                                           String ingestFormat,
                                           char fType,
                                           String searchString,
                                           FedoraAPIA targetRepoAPIA,
                                           FedoraAPIM targetRepoAPIM,
                                           String logMessage,
                                           PrintStream log,
                                           IngestCounter c) throws Exception {
        File[] files = dir.listFiles();
        Arrays.sort(files, _FILE_COMPARATOR);
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                multiFromDirectory(files[i], ingestFormat, fType, searchString,
                                   targetRepoAPIA, targetRepoAPIM,
                                   logMessage, log, c);
            } else {
                if (matches(files[i], searchString)) {
                    try {
                        String pid = oneFromFile(files[i], ingestFormat, targetRepoAPIA, targetRepoAPIM, logMessage);
                        c.successes++;
                        IngestLogger.logFromFile(log, files[i], fType, pid);
                    } catch (Exception e) {
                        // failed... just log it and continue
                        c.failures++;
                        IngestLogger.logFailedFromFile(log, files[i], fType, e);
                    }
                }
            }
        }
    }

    private static boolean matches(File file, String searchString) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(file));
        try {
            String line;
            while ( (line=in.readLine()) != null ) {
                if (line.indexOf(searchString)!=-1) return true;
            }
        } finally {
            try { in.close(); } catch (Exception e) { }
        }
        return false;
    }

    /**************************************************************************

                            Ingest from repository

    **************************************************************************/
    
    // if logMessage is null, will make informative one up
    public static String oneFromRepository(FedoraAPIA sourceRepoAPIA,
                                           FedoraAPIM sourceRepoAPIM,
                                           String sourceExportFormat,
                                           String pid,
                                           FedoraAPIA targetRepoAPIA,
                                           FedoraAPIM targetRepoAPIM,
                                           String logMessage)
                                           throws Exception {
        System.out.println("Ingesting " + pid + " from source repository.");

        // EXPORT from source repository
        // The export context is set to "migrate" since the intent
        // of ingest from repository is to migrate an object from
        // one repository to another.  The "migrate" option will 
        // ensure that URLs that were relative to the "exporting"
        // repository are made relative to the "importing" repository.
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        AutoExporter.export(sourceRepoAPIA, sourceRepoAPIM,
                            pid,
                            sourceExportFormat,
                            "migrate",
                            out);
        
        // INGEST into target repository        
        String realLogMessage=logMessage;
        if (realLogMessage==null) {
            realLogMessage="Ingested from source repository with pid " + pid;
        }
        return AutoIngestor.ingestAndCommit(targetRepoAPIA,
                                            targetRepoAPIM,
                                            new ByteArrayInputStream(out.toByteArray()),
                                             //fixed.toString().getBytes("UTF-8")),
                                            sourceExportFormat,
                                            realLogMessage);
    }

   public static void multiFromRepository(String sourceProtocol,
                                          String sourceHost,
                                          int sourcePort,
                                          FedoraAPIA sourceRepoAPIA,
                                          FedoraAPIM sourceRepoAPIM,
                                          String sourceExportFormat,
                                          String fTypes,
                                          FedoraAPIA targetRepoAPIA,
                                          FedoraAPIM targetRepoAPIM,
                                          String logMessage, 
                                          PrintStream log, 
                                          IngestCounter c) throws Exception {
        String tps = fTypes.toUpperCase();
        if (tps.indexOf("D")!=-1) {
            multiFromRepository(sourceProtocol, sourceHost, sourcePort,
                                sourceRepoAPIA, sourceRepoAPIM,
                                sourceExportFormat, 'D', targetRepoAPIA,
                                targetRepoAPIM, logMessage, log, c);
        }
        if (tps.indexOf("M")!=-1) {
            multiFromRepository(sourceProtocol, sourceHost, sourcePort,
                                sourceRepoAPIA, sourceRepoAPIM,
                                sourceExportFormat, 'M', targetRepoAPIA,
                                targetRepoAPIM, logMessage, log, c);
        }
        if (tps.indexOf("O")!=-1) {
            multiFromRepository(sourceProtocol, sourceHost, sourcePort,
                                sourceRepoAPIA, sourceRepoAPIM,
                                sourceExportFormat, 'O', targetRepoAPIA,
                                targetRepoAPIM, logMessage, log, c);
        }
        System.out.println("Finished ingesting.");
    }

   public static void multiFromRepository(String sourceProtocol,
                                          String sourceHost,
                                          int sourcePort,
                                          FedoraAPIA sourceRepoAPIA,
                                          FedoraAPIM sourceRepoAPIM,
                                          String sourceExportFormat,
                                          char fType,
                                          FedoraAPIA targetRepoAPIA,
                                          FedoraAPIM targetRepoAPIM,
                                          String logMessage, 
                                          PrintStream log, 
                                          IngestCounter c) throws Exception {
        String searchString = getSearchString(fType);
        System.out.println("Ingesting all " + getSearchString(fType) + "s from " 
                + sourceHost + ":" + sourcePort);

        // prepare the FieldSearch query
        String fTypeString = "" + fType;
        FieldSearchQuery query = new FieldSearchQuery();
        Condition cond = new Condition();
        cond.setProperty("fType");
        cond.setOperator(ComparisonOperator.fromValue("eq"));
        cond.setValue(fTypeString);
        Condition[] conditions = new Condition[1];
        conditions[0] = cond;
        query.setConditions(conditions);
        query.setTerms(null);

        String[] resultFields = new String[1];
        resultFields[0] = "pid";

        // get the first chunk of search results
        FieldSearchResult result = AutoFinder.findObjects(sourceRepoAPIA,
                                                          resultFields,
                                                          100,
                                                          query);

        while (result != null) {

            ObjectFields[] ofs = result.getResultList();

            // ingest all objects from this chunk of search results
            for (int i=0; i < ofs.length; i++) {
                String pid = ofs[i].getPid();
                try {
                    String newPID = oneFromRepository(sourceRepoAPIA,
                                                      sourceRepoAPIM,
                                                      sourceExportFormat,
                                                      pid,
                                                      targetRepoAPIA,
                                                      targetRepoAPIM,
                                                      logMessage);
                    c.successes++;
                    IngestLogger.logFromRepos(log, pid, fType, newPID);
                } catch (Exception e) {
                    // failed... just log it and continue
                    c.failures++;
                    IngestLogger.logFailedFromRepos(log, pid, fType, e);
                }
            }

            // get the next chunk of search results, if any
            String token = null;
            try { 
                token = result.getListSession().getToken(); 
            } catch (Throwable th) { }

            if (token != null) {
                result = AutoFinder.resumeFindObjects(sourceRepoAPIA, token);
            } else {
                result = null;
            }
        }
                           
    }

    private static String getMessage(String logMessage, File file) {
        if (logMessage!=null) return logMessage;
        return "Ingested from local file " + file.getPath();
    }

    // fixme: this isn't ingest-specific... it doesn't belong here
    public static String getDuration(long millis) {
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
        System.err.println("  fedora-ingest f[ile] INPATH FORMAT THST:TPRT TUSR TPSS TPROTOCOL [LOG]");
        System.err.println("  fedora-ingest d[ir] INPATH FORMAT FTYPS THST:TPRT TUSR TPSS TPROTOCOL [LOG]");
        System.err.println("  fedora-ingest r[epos] SHST:SPRT SUSR SPSS PID|FTYPS THST:TPRT TUSR TPSS SPROTOCOL TPROTOCOL [LOG]");
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
        System.err.println("  SPROTOCOL  is the protocol to communicate with source repository (http or https)");
        System.err.println("  TPROTOCOL  is the protocol to communicate with target repository (http or https)");
        System.err.println("  LOG        is the optional log message.  If unspecified, the log message");
        System.err.println("             will indicate the source filename or repository of the object(s).");
        System.err.println();
        System.err.println("Examples:");
        System.err.println("fedora-ingest f obj1.xml foxml1.0 myrepo.com:8443 jane jpw https");
        System.err.println();
        System.err.println("  Ingests obj1.xml (encoded in foxml1.0 format) from the");
        System.err.println("  current directory into the repository at myrepo.com:80");
        System.err.println("  as user 'jane' with password 'jpw' using the secure https protocol (SSL).");
        System.err.println("  The logmessage will be system-generated, indicating");
        System.err.println("  the source path+filename.");
        System.err.println();
        System.err.println("fedora-ingest d c:\\archive foxml1.0 M myrepo.com:80 jane janepw http \"\"");
        System.err.println();
        System.err.println("  Traverses entire directory structure of c:\\archive, and ingests ");
        System.err.println("  any file that looks like a behavior mechanism object (M). ");
        System.err.println("  It assumes all files will be in the XML format 'foxml1.0'");
        System.err.println("  and will fail on ingests of files that are not of this format.");
        System.err.println("  All log messages will be the quoted string.");
        System.err.println();
        System.err.println("fedora-ingest d c:\\archive foxml1.0 ODM myrepo.com:80 jane janepw http \"for jane\"");
        System.err.println();
        System.err.println("  Same as above, but ingests all three types of objects (O,D,M).");
        System.err.println();
        System.err.println("fedora-ingest r jrepo.com:8081 mike mpw demo:1 myrepo.com:8443 jane jpw http https \"\"");
        System.err.println();
        System.err.println("  Ingests the object whose pid is 'demo:1' from the source repository");
        System.err.println("  'srcrepo.com:8081' into the target repository 'myrepo.com:80'.");
        System.err.println("  The object will be exported from the source repository in the default");
        System.err.println("  export format configured at the source." );
        System.err.println("  All log messages will be empty.");
        System.err.println();
        System.err.println("fedora-ingest r jrepo.com:8081 mike mpw O myrepo.com:8443 jane jpw http https \"\"");
        System.err.println();
        System.err.println("  Same as above, but ingests all data objects (type O).");
        System.err.println();
        System.err.println("ERROR  : " + msg);
        System.exit(1);
    }

    private static void summarize(IngestCounter counter, File logFile) {
        System.out.println();
        if (counter.failures > 0) {
            System.out.println("WARNING: " + counter.failures + " of " + counter.getTotal() + " objects failed.  Check log.");
        } else {
            System.out.println("SUCCESS: All " + counter.getTotal() + " objects were ingested.");
        }
        System.out.println();
        System.out.println("A detailed log is at " + logFile.getPath());
    }

    /**
     * Command-line interface for doing ingests.
     */
    public static void main(String[] args) {
        try {
            if (args.length<1) {
                Ingest.badArgs("No arguments entered!");
            }
            PrintStream log=null;
            File logFile=null;
            String logRootName=null;
            IngestCounter counter = new IngestCounter();
            char kind=args[0].toLowerCase().charAt(0);
            if (kind=='f') {
                // USAGE: fedora-ingest f[ile] INPATH FORMAT THST:TPRT TUSR TPSS PROTOCOL [LOG]
                if (args.length<7 || args.length>8) {
                    Ingest.badArgs("Wrong number of arguments for file ingest.");
                    System.out.println(
                    "USAGE: fedora-ingest f[ile] INPATH FORMAT THST:TPRT TUSR TPSS PROTOCOL [LOG]");
                }
                File f=new File(args[1]);
                String ingestFormat = args[2];
                String logMessage=null;
                if (args.length==8) {
                    logMessage=args[7];
                }

                String protocol=args[6];            
                String[] hp=args[3].split(":");
                
                // ******************************************
                // NEW: use new client utility class
                // FIXME:  Get around hardcoding the path in the baseURL
                String baseURL = protocol + "://" + hp[0] + ":" + Integer.parseInt(hp[1]) + "/fedora";
                FedoraClient fc = new FedoraClient(baseURL, args[4], args[5]);
                FedoraAPIA targetRepoAPIA=fc.getAPIA();
                FedoraAPIM targetRepoAPIM=fc.getAPIM();
                //*******************************************
                
                String pid = Ingest.oneFromFile(f, ingestFormat, targetRepoAPIA, targetRepoAPIM, logMessage);
                if (pid==null){
                    System.out.print("ERROR: ingest failed for file: " + args[1]);
                } else {
                    System.out.println("Ingested PID: " + pid);                    
                }
            } else if (kind=='d') {
                // USAGE: fedora-ingest d[ir] INPATH FORMAT FTYPS THST:TPRT TUSR TPSS PROTOCOL [LOG]
                if (args.length<8 || args.length>9) {
                    Ingest.badArgs("Wrong number of arguments (" + args.length + ") for directory ingest.");
                    System.out.println(
                        "USAGE: fedora-ingest d[ir] INPATH FORMAT FTYPS THST:TPRT TUSR TPSS PROTOCOL [LOG]");
                } 
                File d=new File(args[1]);
                String ingestFormat = args[2];
                String logMessage=null;
                if (args.length==9) {
                    logMessage=args[8];
                }
  
                String protocol=args[7];                
                String[] hp=args[4].split(":");
                
                // ******************************************
                // NEW: use new client utility class
                // FIXME:  Get around hardcoding the path in the baseURL
                String baseURL = protocol + "://" + hp[0] + ":" + Integer.parseInt(hp[1]) + "/fedora";
                FedoraClient fc = new FedoraClient(baseURL, args[5], args[6]);
                FedoraAPIA targetRepoAPIA=fc.getAPIA();
                FedoraAPIM targetRepoAPIM=fc.getAPIM();
                //*******************************************
                
                logRootName="ingest-from-dir";
                logFile = IngestLogger.newLogFile(logRootName);
                log =new PrintStream(new FileOutputStream(logFile), true, "UTF-8");
                IngestLogger.openLog(log, logRootName);
                Ingest.multiFromDirectory(d,
                                             ingestFormat,
                                             args[3],
                                             targetRepoAPIA,
                                             targetRepoAPIM,
                                             logMessage, log, counter);
                IngestLogger.closeLog(log, logRootName);
                summarize(counter, logFile);
            } else if (kind=='r') {
                // USAGE: fedora-ingest r[epos] SHST:SPRT SUSR SPSS PID|FTYPS THST:TPRT TUSR TPSS SPROTOCOL TPROTOCOL [LOG]
                if (args.length<10 || args.length>11) {
                    Ingest.badArgs("Wrong number of arguments for repository ingest.");
                }
                String logMessage=null;
                if (args.length==11) {
                    logMessage=args[10];
                }
                //Source repository
                String[] shp=args[1].split(":");
                String source_host = shp[0];
                String source_port = shp[1];
                String source_user = args[2];
                String source_password = args[3];  
                String source_protocol=args[8];
                
                // ******************************************
                // NEW: use new client utility class
                // FIXME:  Get around hardcoding the path in the baseURL
                String sourceBaseURL = 
                    source_protocol + "://" + source_host + ":" + Integer.parseInt(source_port) + "/fedora";
                FedoraClient sfc = new FedoraClient(sourceBaseURL, source_user, source_password);
                FedoraAPIA sourceRepoAPIA=sfc.getAPIA();
                FedoraAPIM sourceRepoAPIM=sfc.getAPIM();
                //*******************************************

                //Target repository
                String[] thp=args[5].split(":");
                String target_host = thp[0];
                String target_port = thp[1];
                String target_user = args[6];
                String target_password = args[7];  
                String target_protocol=args[9];
                
                // ******************************************
                // NEW: use new client utility class
                // FIXME:  Get around hardcoding the path in the baseURL
                String targetBaseURL = 
                    target_protocol + "://" + target_host + ":" + Integer.parseInt(target_port) + "/fedora";
                FedoraClient tfc = new FedoraClient(targetBaseURL, target_user, target_password);
                FedoraAPIA targetRepoAPIA=tfc.getAPIA();
                FedoraAPIM targetRepoAPIM=tfc.getAPIM();
                //*******************************************
                
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
                    String successfulPID = Ingest.oneFromRepository(
                                                       sourceRepoAPIA,
                                                       sourceRepoAPIM,
                                                       sourceExportFormat,
                                                       args[4],
                                                       targetRepoAPIA,
                                                       targetRepoAPIM,
                                                       logMessage);
                    if (successfulPID==null){
                        System.out.print("ERROR: ingest from repo failed for PID=" + args[4]);
                    } else {
                        System.out.println("Ingested PID: " + successfulPID);
                    }
                } else {
                    // multi-object
                    //hp=args[1].split(":");
                    logRootName="ingest-from-repository";
                    logFile = IngestLogger.newLogFile(logRootName);
                    log =new PrintStream(new FileOutputStream(logFile), true, "UTF-8");
                    IngestLogger.openLog(log, logRootName);
                    Ingest.multiFromRepository(
                                                      source_protocol,
                                                      source_host,
                                                      Integer.parseInt(source_port),
                                                      sourceRepoAPIA,
                                                      sourceRepoAPIM,
                                                      sourceExportFormat,
                                                      args[4],
                                                      targetRepoAPIA,
                                                      targetRepoAPIM,
                                                      logMessage, log, counter);
                    IngestLogger.closeLog(log, logRootName);
                    summarize(counter, logFile);
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