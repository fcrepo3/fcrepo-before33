package fedora.client.ingest;

import java.io.*;
import java.util.*;

import fedora.client.Administrator;
import fedora.client.APIMStubFactory;
import fedora.client.export.AutoExporter;

import fedora.server.management.FedoraAPIM;

public class Ingest {

    public static int ONE_FROM_FILE=0;
    public static int MULTI_FROM_DIR=1;
    public static int ONE_FROM_REPOS=2;
    public static int MULTI_FROM_REPOS=3;

    public static String LAST_PATH;
    
    // launch interactively
    public Ingest(int kind) 
            throws Exception {
    }

    // if logMessage is null, will use original path in logMessage
    public static String oneFromFile(File file, FedoraAPIM targetRepository,
                                     String logMessage)
            throws Exception {
        System.out.println("Ingesting from file " + file.getPath());
        LAST_PATH=file.getPath();
        return AutoIngestor.ingestAndCommit(targetRepository,
                                            new FileInputStream(file),
                                            getMessage(logMessage, file));
    }
    
    // if logMessage is null, will use original path in logMessage
    public static String[] multiFromDirectory(File dir, String fTypes, 
                                              FedoraAPIM targetRepository,
                                              String logMessage)
            throws Exception {
        String tps=fTypes.toUpperCase();
        Set toIngest;
        HashSet pidSet=new HashSet();
        if (tps.indexOf("D")!=-1) {
            toIngest=getFiles(dir, "FedoraBDefObject");
            System.out.println("Found " + toIngest.size() + " behavior definitions.");
            pidSet.addAll(ingestAll(toIngest, targetRepository, logMessage)); 
        }
        if (tps.indexOf("M")!=-1) {
            toIngest=getFiles(dir, "FedoraBMechObject");
            System.out.println("Found " + toIngest.size() + " behavior mechanisms.");
            pidSet.addAll(ingestAll(toIngest, targetRepository, logMessage)); 
        }
        if (tps.indexOf("O")!=-1) {
            toIngest=getFiles(dir, "FedoraObject");
            System.out.println("Found " + toIngest.size() + " regular objects.");
            pidSet.addAll(ingestAll(toIngest, targetRepository, logMessage)); 
        }
        Iterator iter=pidSet.iterator();
        String[] pids=new String[pidSet.size()];
        int i=0;
        while (iter.hasNext()) {
            pids[i++]=(String) iter.next(); 
        }
        return pids;
    }
    
    private static Set ingestAll(Set fileSet, 
                                 FedoraAPIM targetRepository, 
                                 String logMessage) 
            throws Exception {
        HashSet set=new HashSet();
        Iterator iter=fileSet.iterator();
        while (iter.hasNext()) {
            File f=(File) iter.next();
            set.add(oneFromFile(f, targetRepository, logMessage)); 
        }
        return set;
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
    
    // if logMessage is null, will make informative one up
    public static String oneFromRepository(FedoraAPIM sourceRepository, 
                                           String pid,
                                           FedoraAPIM targetRepository,
                                           String logMessage)
            throws Exception {
        System.out.println("Ingesting " + pid + " from source repository.");
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        AutoExporter.export(sourceRepository, 
                            pid, 
                            out, 
                            false);
        ByteArrayInputStream in=new ByteArrayInputStream(out.toByteArray());
        String realLogMessage=logMessage;
        if (realLogMessage==null) {
            realLogMessage="Ingested from source repository with pid " + pid;
        }
        return AutoIngestor.ingestAndCommit(targetRepository,
                                            in,
                                            realLogMessage);
    }
    
    // if logMessage is null, will make informative one up
    public static String[] multiFromRepository(FedoraAPIM sourceRepository,
                                               String fTypes,
                                               FedoraAPIM targetRepository,
                                               String logMessage)
            throws Exception {
        // do a search for each fType, adding it to the list
        return null;
    }
    
    private static String getMessage(String logMessage, File file) {
        if (logMessage!=null) return logMessage;
        return "Ingested from local file " + file.getPath();
    }

    /**
     * Print error message and show usage for command-line interface.
     */
    public static void badArgs(String msg) {
        System.err.println("Error  : " + msg);
        System.err.println();
        System.err.println("Command: fedora-ingest");
        System.err.println();
        System.err.println("Summary: Ingests one or more objects into a Fedora repository, from either");
        System.err.println("         the local filesystem or another Fedora repository.");
        System.err.println();
        System.err.println("Syntax:");
        System.err.println("  fedora-ingest f[ile] PATH THST:TPRT TUSR TPSS [LOG]");
        System.err.println("  fedora-ingest d[ir] PATH FTYPS THST:TPRT TUSR TPSS [LOG]");
        System.err.println("  fedora-ingest r[epos] SHST:SPRT SUSR SPSS PID|FTYPS THST:TPRT TUSR TPSS [LOG]");
        System.err.println();
        System.err.println("Where:");
        System.err.println("  PATH       is the local file or directory name.");
        System.err.println("  FTYPS      is any combination of the characters O, D, and M, specifying");
        System.err.println("             which Fedora object type(s) should be ingested. O=regular objects,");
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
        System.err.println("fedora-ingest file obj1.xml example.com:80 fedoraAdmin fedoraAdmin");
        System.err.println();
        System.err.println("  Ingests obj1.xml from the current directory into the repository at ");
        System.err.println("  example.com:80 as fedoraAdmin, using 'fedoraAdmin' as the password,"); 
        System.err.println("  The logmessage will be system-generated, indicating the source path+filename.");
        System.err.println();
        System.err.println("fedora-ingest dir c:\\archive M example.com:80 fedoraAdmin fedoraAdmin \"\"");
        System.err.println();
        System.err.println("  Traverses entire directory structure of c:\\archive, and ingests any file that");
        System.err.println("  looks like a behavior mechanism object.  All log messages will be empty.");
        System.err.println();
        System.err.println("fedora-ingest dir c:\\archive ODM example.com:80 fedoraAdmin fedoraAdmin \"\"");
        System.err.println();
        System.err.println("  Same as above, but ingests all regular and behavior definition objects, too.");
        System.err.println();
        System.exit(1);
    }

    /**
     * Command-line interface for doing ingests.
     */
    public static void main(String[] args) {
        try {
            if (args.length<1) {
                Ingest.badArgs("Not enough arguments.");
            }
            char kind=args[0].toLowerCase().charAt(0);
            if (kind=='f') {
                if (args.length<5 || args.length>6) {
                    Ingest.badArgs("Wrong number of arguments for file ingest.");
                }
                File f=new File(args[1]);
                String logMessage=null;
                if (args.length==6) {
                    logMessage=args[5];
                }
                String[] hp=args[2].split(":");
                FedoraAPIM targetRepos=
                        APIMStubFactory.getStub(hp[0],
                                                Integer.parseInt(hp[1]),
                                                args[3],
                                                args[4]);
                System.out.println("Ingested PID: " 
                        + Ingest.oneFromFile(f, targetRepos, logMessage));
            } else if (kind=='d') {
                if (args.length<6 || args.length>7) {
                    Ingest.badArgs("Wrong number of arguments for directory ingest.");
                }
                File d=new File(args[1]);
                String logMessage=null;
                if (args.length==7) {
                    logMessage=args[6];
                }
                String[] hp=args[3].split(":");
                FedoraAPIM targetRepos=
                        APIMStubFactory.getStub(hp[0],
                                                Integer.parseInt(hp[1]),
                                                args[4],
                                                args[5]);
                String[] pids=Ingest.multiFromDirectory(d, 
                                                        args[2], 
                                                        targetRepos,
                                                        logMessage);
                System.out.println("Ingested PID(s):");
                if (pids.length>0) {
                    for (int i=0; i<pids.length; i++) {
                        System.out.print(" " + pids[i]);
                    }
                } else {
                    System.out.print(" None.");
                }
                System.out.println();
            } else if (kind=='r') {
                if (args.length<8 || args.length>9) {
                    Ingest.badArgs("Wrong number of arguments for repository ingest.");
                }
                String logMessage=null;
                if (args.length==9) {
                    logMessage=args[8];
                }
                String[] hp=args[1].split(":");
                FedoraAPIM sourceRepos=
                        APIMStubFactory.getStub(hp[0],
                                                Integer.parseInt(hp[1]),
                                                args[2],
                                                args[3]);
                hp=args[5].split(":");
                FedoraAPIM targetRepos=
                        APIMStubFactory.getStub(hp[0],
                                                Integer.parseInt(hp[1]),
                                                args[6],
                                                args[7]);
                if (args[4].indexOf(":")!=-1) {
                    // single object
                    System.out.println("Ingested PID: "
                            + Ingest.oneFromRepository(sourceRepos,
                                                       args[4],
                                                       targetRepos,
                                                       logMessage));
                } else {
                    // multi-object
                    String[] pids=Ingest.multiFromRepository(sourceRepos,
                                                             args[4],
                                                             targetRepos,
                                                             logMessage);
                    System.out.println("Ingested PID(s):");
                    if (pids.length>0) {
                        for (int i=0; i<pids.length; i++) {
                            System.out.print(" " + pids[i]);
                        }
                    } else {
                        System.out.print(" None.");
                    }
                    System.out.println();
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
            if (Ingest.LAST_PATH!=null) {
                System.out.println("Last attempted file: " + Ingest.LAST_PATH);
            }
            System.err.println();
        }
    }

}