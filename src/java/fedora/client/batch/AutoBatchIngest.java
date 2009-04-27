/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.batch;

import java.io.File;

import java.util.Properties;

import fedora.common.Constants;

/**
 * Auto Batch Ingest.
 *
 * @author Ross Wayland
 */
public class AutoBatchIngest
        implements Constants {

    private final Properties batchProperties = new Properties();

    public AutoBatchIngest(String objectDir,
                           String logFile,
                           String logFormat,
                           String objectFormat,
                           String host,
                           String port,
                           String username,
                           String password,
                           String protocol,
                           String context)
            throws Exception {

        batchProperties.setProperty("ingest", "yes");
        batchProperties.setProperty("objects", objectDir);
        batchProperties.setProperty("ingested-pids", logFile);
        batchProperties.setProperty("pids-format", logFormat);
        batchProperties.setProperty("object-format", objectFormat);
        batchProperties.setProperty("server-fqdn", host);
        batchProperties.setProperty("server-port", port);
        batchProperties.setProperty("server-context", context);
        batchProperties.setProperty("username", username);
        batchProperties.setProperty("password", password);
        batchProperties.setProperty("server-protocol", protocol);

        BatchTool batchTool = new BatchTool(batchProperties, null, null);
        batchTool.prep();
        batchTool.process();
    }

    public static final void main(String[] args) throws Exception {
        String context = Constants.FEDORA_DEFAULT_APP_CONTEXT;
        boolean errors = false;
        if (args.length == 8 || args.length == 9) {
            if (!new File(args[0]).isDirectory()) {
                System.out.println("Specified object directory: \"" + args[0]
                        + "\" is not a directory.");
                errors = true;
            }
            if (!args[2].equals("xml") && !args[2].equals("text")) {
                System.out
                        .println("Format for log file must must be either: \""
                                + "\"xml\"  or  \"txt\"");
                errors = true;
            }
            if (!args[3].equals(FOXML1_1.uri)
                    && !args[3].equals(METS_EXT1_1.uri)) {
                System.out.println("Object format must must be either: \""
                        + "\"" + FOXML1_1.uri + "\" or \"" + METS_EXT1_1.uri
                        + "\"");
                errors = true;
            }
            String[] server = args[4].split(":");
            if (server.length != 2) {
                System.out.println("Specified server name does not specify "
                        + "port number: \"" + args[4] + "\" .");
                errors = true;
            }
            if (!args[7].equals("http") && !args[7].equals("https")) {
                System.out.println("Protocl must be either: \""
                        + "\"http\"  or  \"https\"");
                errors = true;
            }

            if (args.length == 9 && !args[8].equals("")) {
                context = args[8];
            }
            if (!errors) {
                AutoBatchIngest autoBatch =
                        new AutoBatchIngest(args[0],
                                            args[1],
                                            args[2],
                                            args[3],
                                            server[0],
                                            server[1],
                                            args[5],
                                            args[6],
                                            args[7],
                                            context);
            }
        } else {
            System.out.println("\n**** Wrong Number of Arguments *****\n");
            System.out.println("AutoBatchIngest requires 8 arguments.");
            System.out.println("(1) - full path to object directory");
            System.out.println("(2) - full path to log file");
            System.out.println("(3) - format of log file (xml or text)");
            System.out.println("(4) - object format (" + FOXML1_1.uri + " or "
                    + METS_EXT1_1.uri + ")\n");
            System.out
                    .println("(5) - host name and port of Fedora server (host:port)");
            System.out.println("(6) - admin username of Fedora server");
            System.out
                    .println("(7) - password for admin user of Fedora server\n");
            System.out
                    .println("(8) - protocol to communicate with Fedora server (http or https)");
            System.out
                    .println("(9) - optional, alternate context location of the Fedora app server (default is fedora)");

        }

    }

}
