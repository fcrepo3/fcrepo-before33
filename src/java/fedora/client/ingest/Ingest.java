package fedora.client.ingest;

import java.io.*;

import fedora.client.Administrator;

import fedora.server.management.FedoraAPIM;

public class Ingest {

    public static int ONE_FROM_FILE=0;
    public static int MULTI_FROM_DIRECTORY=1;
    public static int ONE_FROM_REPOSITORY=2;
    public static int MULTI_FROM_REPOSITORY=3;

    public Ingest(int kind) 
            throws Exception {
        if (kind==ONE_FROM_FILE) {
        } else if (kind==MULTI_FROM_DIRECTORY) {
        } else if (kind==ONE_FROM_REPOSITORY) {
        } else if (kind==MULTI_FROM_REPOSITORY) {
        }
    }

    // if logMessage is null, will use original path in logMessage
    public static String oneFromFile(File file, FedoraAPIM targetRepository,
                                     String logMessage)
            throws Exception {
        return AutoIngestor.ingestAndCommit(targetRepository,
                                            new FileInputStream(file),
                                            getMessage(logMessage, file));
    }
    
    // if logMessage is null, will use original path in logMessage
    public static String[] multiFromDirectory(File dir, String fType, 
                                              FedoraAPIM targetRepository,
                                              String logMessage)
            throws Exception {
            return null;
    }
    
    // if logMessage is null, will use original path in logMessage
    public static String oneFromRepository(FedoraAPIM sourceRepository, 
                                           String pid,
                                           FedoraAPIM targetRepository,
                                           String logMessage)
            throws Exception {
            return null;
    }
    
    // if logMessage is null, will use original path in logMessage
    public static String[] multiFromRepository(String host, int port, 
                                               String user, String pass, 
                                               String fType,
                                               String logMessage)
            throws Exception {
            return null;
    }
    
    private static String getMessage(String logMessage, File file) {
        if (logMessage!=null) return logMessage;
        return "Original local location was " + file.getPath();
    }

    public static void badArgs(String msg) {
        System.err.println("Command: fedora-ingest");
        System.err.println("Summary: Ingests one or more objects into a Fedora repository, from either");
        System.err.println("         the local filesystem or another Fedora repository.");
        System.err.println("Syntax:");
        System.err.println("  fedora-ingest file PATH THST TPRT TUSR TPSS [LOG]");
        System.err.println("  fedora-ingest dir PATH FTYPS THST TPRT TUSR TPSS [LOG]");
        System.err.println("  fedora-ingest repos SHST SPRT SUSR SPSS PID|FTYPS THST TPRT TUSR TPSS [LOG]");
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
        System.err.println("Examples:");
        System.err.println("> fedora-ingest file obj1.xml example.com 80 fedoraAdmin fedoraAdmin");
        System.err.println("  Ingests obj1.xml from the current directory into the repository at ");
        System.err.println("  example.com:80 as fedoraAdmin, using 'fedoraAdmin' as the password,"); 
        System.err.println("  The logmessage will be system-generated, indicating the source path+filename.");
        System.err.println("> fedora-ingest dir c:\\archive M example.com 80 fedoraAdmin fedoraAdmin \"\"");
        System.err.println("  Traverses entire directory structure of c:\\archive, and ingests any file that");
        System.err.println("  looks like a behavior mechanism object.  All log messages will be empty.");
        System.err.println("> fedora-ingest dir c:\\archive ODM example.com 80 fedoraAdmin fedoraAdmin \"\"");
        System.err.println("  Same as above, but ingests all regular and behavior definition objects, too.");
        System.err.println("> fedora-ingest repos old.com 8088 fedoraAdmin oldPass oldcom:100 example.com \\");
        System.err.println("                80 fedoraAdmin newPass \"Just moving this...\"");
        System.err.println("> fedora-ingest repos old.com 8088 fedoraAdmin oldPass oldcom:100 example.com");

    }

    public static void main(String[] args) {
        try {
            if (args.length<1) {
                Ingest.badArgs("Not enough arguments.");
            }
        } catch (Exception e) {
            System.err.print("ERROR - ");
            if (e.getMessage()==null) {
                System.err.println(e.getClass().getName());
            } else {
                System.err.print(e.getMessage());
            }
        }
    }

}