package fedora.client.batch;

import java.util.Properties;
import java.io.File;

/**
 *
 * <p><b>Title:</b> AutoBatchIngest.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class AutoBatchIngest {
    private Properties batchProperties = new Properties();

    public AutoBatchIngest(String objectDir, String logFile, String logFormat, String objectFormat,
        String host, String port, String username, String password, String protocol) throws Exception {

        this.batchProperties.setProperty("ingest", "yes");
        this.batchProperties.setProperty("objects", objectDir);
        this.batchProperties.setProperty("ingested-pids", logFile);
        this.batchProperties.setProperty("pids-format", logFormat);
        this.batchProperties.setProperty("object-format", objectFormat);
        this.batchProperties.setProperty("server-fqdn", host);
        this.batchProperties.setProperty("server-port", port);
        this.batchProperties.setProperty("username", username);
        this.batchProperties.setProperty("password", password);
		this.batchProperties.setProperty("server-protocol", protocol);

        BatchTool batchTool = new BatchTool(this.batchProperties, null, null);
        batchTool.prep();
        batchTool.process();
    }

    public static final void main(String[] args) throws Exception {
        boolean errors = false;
        if (args.length == 8) {
            if (!new File(args[0]).isDirectory()) {
                System.out.println("Specified object directory: \""
                                   + args[0] + "\" is not a directory.");
                errors = true;
            }
            if (!args[2].equals("xml") && !args[2].equals("text")) {
                System.out.println("Format for log file must must be either: \""
                                   + "\"xml\"  or  \"txt\"");
                errors = true;
            }
            if (!args[3].equals("foxml1.0") && !args[3].equals("metslikefedora1")) {
                System.out.println("Object format must must be either: \""
                                   + "\"foxml1.0\"  or  \"metslikefedora1\"");
                errors = true;
            }            
            String[] server = args[4].split(":");
            if (server.length !=2) {
                System.out.println("Specified server name does not specify "
                                   + "port number: \"" + args[4] + "\" .");
                errors = true;
            }
			if (!args[7].equals("http") && !args[7].equals("https")) {
				System.out.println("Protocl must be either: \""
								   + "\"http\"  or  \"https\"");
				errors = true;
			}
            if (!errors) {
                AutoBatchIngest autoBatch = new AutoBatchIngest(args[0], args[1], args[2], args[3], server[0], server[1], args[5], args[6], args[7]);
            }
        } else {
            System.out.println("\n**** Wrong Number of Arguments *****\n");
            System.out.println("AutoBatchIngest requires 8 arguments.");
            System.out.println("(1) - full path to object directory");
            System.out.println("(2) - full path to log file");
            System.out.println("(3) - format of log file (xml or text)");
            System.out.println("(4) - object format (foxml1.0 or metslikefedora1)\n");
            System.out.println("(5) - host name and port of Fedora server (host:port)");
            System.out.println("(6) - admin username of Fedora server");
            System.out.println("(7) - password for admin user of Fedora server\n");
			System.out.println("(8) - protocol to communicate with Fedora server (http or https)");
        }

    }

}