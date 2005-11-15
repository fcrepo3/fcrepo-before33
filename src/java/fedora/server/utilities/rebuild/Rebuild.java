package fedora.server.utilities.rebuild;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fedora.server.config.Configuration;
import fedora.server.config.Parameter;
import fedora.server.config.ServerConfiguration;
import fedora.server.config.ServerConfigurationParser;
import fedora.server.storage.translation.DODeserializer;
import fedora.server.storage.translation.DOTranslationUtility;
import fedora.server.storage.translation.FOXMLDODeserializer;
import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DigitalObject;
import fedora.server.utilities.ProtocolPort;
import fedora.server.utilities.ServerUtility;
import gnu.trove.TIntHashSet;

/**
 * Entry-point for rebuilding various aspects of the repository.
 *
 * @@version $Id$
 */
public class Rebuild {

    /**
     * Rebuilders that the rebuild utility knows about.
     */
    public static String[] REBUILDERS = new String[] {
            "fedora.server.resourceIndex.ResourceIndexRebuilder",
            "fedora.server.utilities.rebuild.SQLRebuilder" };

    public Rebuild(File serverDir, String profile) throws Exception {
        ServerConfiguration serverConfig = getServerConfig(serverDir,
                                                           profile);
        // set these here so DOTranslationUtility doesn't try to get a Server instance
        System.setProperty("fedoraServerHost", serverConfig.getParameter("fedoraServerHost").getValue());
        System.setProperty("fedoraServerPort", serverConfig.getParameter("fedoraServerPort").getValue());

        System.out.println();
        System.out.println("                       Fedora Rebuild Utility");
        System.out.println("                     ..........................");
        System.out.println();
        System.out.println("WARNING: Live rebuilds are not currently supported.");
        System.out.println("         Make sure your server is stopped before continuing.");
        System.out.println();
        System.out.println("Server directory is " + serverDir.toString());
        System.out.print  ("Server profile is ");
        if (profile == null) {
            System.out.println("unspecified");
        } else {
            System.out.println(profile);
        }
        System.out.println();
        System.out.println("---------------------------------------------------------------------");
        System.out.println();
        Rebuilder rebuilder = getRebuilder(serverDir, serverConfig);
        if (rebuilder != null) {
            System.out.println();
            System.out.println(rebuilder.getAction());
            System.out.println();
            Map options = getOptions(rebuilder.init(serverDir, serverConfig));
            boolean serverIsRunning = false;
            try {
                serverIsRunning = ServerUtility.pingServletContainerRunning("/fedora/describe", 20);
            } catch (Exception e) { }
            if (serverIsRunning && rebuilder.shouldStopServer()) {
            	ProtocolPort protocolPort = ServerUtility.getProtocolPort(ServerUtility.HTTP, ServerUtility.HTTPS); 
            	String username = ServerUtility.getServerProperties().getProperty(ServerUtility.ADMIN_USERNAME_KEY);
            	String password = ServerUtility.getServerProperties().getProperty(ServerUtility.ADMIN_PASSWORD_KEY);            	
            	ServerUtility.shutdown(protocolPort.getProtocol(), username, password);
            }
            if (options != null) {
                System.out.println();
                System.out.println("Rebuilding...");
                try {
                    rebuilder.start(options);
                    // fedora.server.storage.lowlevel.Configuration conf = fedora.server.storage.lowlevel.Configuration.getInstance();
                    // String objStoreBaseStr = conf.getObjectStoreBase();
                    String objStoreBaseStr = serverConfig.getParameter("object_store_base").getValue();
                    File dir = new File(objStoreBaseStr);
                    TIntHashSet saw = new TIntHashSet();
                    rebuildFromDirectory(rebuilder, dir, "FedoraBDefObject", saw);
                    rebuildFromDirectory(rebuilder, dir, "FedoraBMechObject", saw);
                    rebuildFromDirectory(rebuilder, dir, "FedoraObject", saw);
                } 
                finally {
                    rebuilder.finish();
                }
                System.out.println("Finished.");
                System.out.println();
            }
        }
    }

    /**
     * Recurse directories looking for files that contain searchString,
     * and call rebuilder.addObject on them as long as their PIDs have
     * not already been seen.
     */
    private void rebuildFromDirectory(Rebuilder rebuilder, 
                                      File dir, 
                                      String searchString,
                                      TIntHashSet saw) throws Exception {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                rebuildFromDirectory(rebuilder, files[i], searchString, saw);
            } else {
                BufferedReader reader = null;
                InputStream in;
                try {
                    in = null;
                    reader = new BufferedReader(
                                 new InputStreamReader(
                                     new FileInputStream(files[i]), "UTF-8"));
                    String line = reader.readLine();
                    while (line != null) {
                        if (line.indexOf(searchString) != -1) {
                            in = new FileInputStream(files[i]);
                            line = null;
                        } else {
                            line = reader.readLine();
                        }
                    }
                    if (in != null) {
                        try {
                            System.out.println(files[i].getAbsoluteFile());
                            DigitalObject obj = new BasicDigitalObject();
                            DODeserializer deser = new FOXMLDODeserializer();
                            deser.deserialize(in, 
                                              obj, 
                                              "UTF-8", 
                                              DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL);
                            int hashCode = obj.getPid().hashCode();
                            if (saw.contains(hashCode)) {
                                System.out.println("Skipping (already saw " 
                                                   + obj.getPid() + ")");
                            } else {
                                rebuilder.addObject(obj);
                                saw.add(hashCode);
                            }
                        } finally {
                            try { in.close(); } catch (Exception e) { }
                        }
                    }
                } finally {
                    if (reader != null) try { reader.close(); } catch (Exception e) { }
                }
            }
        }
    }

    private Map getOptions(Map descs) throws IOException {
        Map options = new HashMap();
        Iterator iter = descs.keySet().iterator();
        while (iter.hasNext()) {
            String name = (String) iter.next();
            String desc = (String) descs.get(name);
            options.put(name, getOptionValue(name, desc));
        }
        int c = getChoice("Start rebuilding with the above options?", 
new String[] {"Yes", "No, let me re-enter the options.", "No, exit."});
        if (c == 0) return options;
        if (c == 1) {
            System.out.println();
            return getOptions(descs);
        }
        return null;
    }

    private String getOptionValue(String name, String desc) throws IOException {
        System.out.println("[" + name + "]");
        System.out.println(desc);
        System.out.println();
        System.out.print("Enter a value --> ");
        String val = new BufferedReader(new InputStreamReader(System.in)).readLine();
        System.out.println();
        return val;
    }

    private Rebuilder getRebuilder(File serverDir, ServerConfiguration serverConfig) 
                      throws Exception 
    {
        String[] labels = new String[REBUILDERS.length + 1];
        Rebuilder[] rebuilders = new Rebuilder[REBUILDERS.length];
        int i = 0;
        for (i = 0; i < REBUILDERS.length; i++) 
        {
            Rebuilder r = (Rebuilder) Class.forName(REBUILDERS[i]).newInstance();
            labels[i] = r.getAction();
            rebuilders[i] = r;
        }
        labels[i] = "Exit";
        int choiceNum = getChoice("What do you want to do?", labels);
        if (choiceNum == i) 
        {
            return null;
        } 
        else 
        {
            return rebuilders[choiceNum];
        }
    }

    private int getChoice(String title, String[] labels) throws IOException {
        boolean validChoice = false;
        int choiceIndex = -1;
        System.out.println(title);
        System.out.println();
        for (int i = 1; i <= labels.length; i++) {
            System.out.println("  " + i + ") " + labels[i-1]);
        }
        System.out.println();
        while (!validChoice) {
            System.out.print("Enter (1-" + labels.length + ") --> ");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line = in.readLine();
            try {
                int choiceNum = Integer.parseInt(line);
                if (choiceNum > 0 && choiceNum <= labels.length) {
                  choiceIndex = choiceNum - 1;
                  validChoice = true;
                }
            } catch (NumberFormatException nfe) { }
        }
        return choiceIndex;
    }

    private static ServerConfiguration getServerConfig(File serverDir, 
                                                String profile) throws IOException
 {
        ServerConfigurationParser parser = new ServerConfigurationParser(
                new FileInputStream(new File(serverDir, "config/fedora.fcfg")));
        ServerConfiguration serverConfig = parser.parse();
        // set all the values according to the profile, if specified
        if (profile != null) {
            int c = setValuesForProfile(serverConfig, profile);
            c += setValuesForProfile(serverConfig.getModuleConfigurations(),
                    profile);
            c += setValuesForProfile(serverConfig.getDatastoreConfigurations(),
                    profile);
            if (c == 0) {
                throw new IOException("Unrecognized server-profile: " + profile);
            }
            // System.out.println("Set " + c + " profile-specific values.");
        }
        return serverConfig;
    }

    private static int setValuesForProfile(Configuration config, String profile) {
        int c = 0;
        Iterator iter = config.getParameters().iterator();
        while (iter.hasNext()) {
            Parameter param = (Parameter) iter.next();
            String profileValue = (String) param.getProfileValues().get(profile);
            if (profileValue != null) {
                //System.out.println(param.getName() + " was '" + param.getValue() + "', now '" + profileValue + "'.");
                param.setValue(profileValue);
                c++;
            }
        }
        return c;
    }

    private static int setValuesForProfile(List configs, String profile) {
        Iterator iter = configs.iterator();
        int c = 0;
        while (iter.hasNext()) {
            c += setValuesForProfile((Configuration) iter.next(), profile);
        }
        return c;
    }

    public static void fail(String message, boolean showUsage, boolean exit) {
        System.err.println("Error: " + message);
        System.err.println();
        if (showUsage) {
            System.out.println("Usage: fedora-rebuild [server-profile]");
            System.out.println();
            System.out.println("server-profile : the argument you start Fedora with, such as 'mckoi'");
            System.out.println("                 or 'oracle'.  If you start fedora with 'fedora-start'");
            System.out.println("                 (without arguments), don't specify a server-profile here either.");
            System.out.println();
        }
        if (exit) {
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        // tell commons-logging to use log4j
        System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.Log4jFactory");
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Log4JLogger");
        // log4j
//        File log4jConfig = new File(new File(homeDir), "config/log4j.xml");
//        DOMConfigurator.configure(log4jConfig.getPath());
        String profile = null;
        if (args.length == 1) {
            profile = args[0];
        }
        if (args.length > 1) {
            fail("Too many arguments", true, true);
        }
        try {
            String home = System.getProperties().getProperty("fedora.home");
            File serverDir = new File(new File(home), "server");
            if (args.length > 0) profile = args[0];
            new Rebuild(serverDir, profile);
        } catch (Throwable th) {
            String msg = th.getMessage();
            if (msg == null) msg = th.getClass().getName();
            fail(msg, false, false);
            th.printStackTrace();
        }
    }
    
}
