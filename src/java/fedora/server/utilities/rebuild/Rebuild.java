package fedora.server.utilities.rebuild;

import java.io.*;
import java.util.*;

import fedora.server.config.*;

/**
 * Entry-point for rebuilding various aspects of the repository.
 *
 * @version $Id$
 */
public class Rebuild {

    /**
     * Rebuilders that the rebuild utility knows about.
     */
    public static String[] REBUILDERS = new String[] {
            "fedora.server.resourceIndex.ResourceIndexRebuilder" };

    public Rebuild(File serverDir, String profile) throws Exception {
        ServerConfiguration serverConfig = getServerConfig(serverDir,
                                                           profile);
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
//            ManagedContentFinder finder = getManagedContentFinder(serverConfig);
            Map options = getOptions(rebuilder.init(serverDir, serverConfig));
            if (options != null) {
                System.out.println();
                System.out.println("Rebuilding...");
                try {
                    rebuilder.start(options);
                    addAllObjects(rebuilder, serverDir, serverConfig);
                    // TODO: Get all the objects from the filesystem
                    //       and call rebuilder.addObject()
                } finally {
                    rebuilder.finish();
                }
                System.out.println("Finished.");
                System.out.println();
            }
        }
    }

    private void addAllObjects(Rebuilder rebuilder,
                              File serverDir,
                              ServerConfiguration serverConfig) {
        // Determine which deserializers are supported, and get an
        // instance of each
    }

    private Map getOptions(Map descs) throws IOException {
        Map options = new HashMap();
        Iterator iter = descs.keySet().iterator();
        boolean hasAtLeastOneOption = false;
        while (iter.hasNext()) {
            hasAtLeastOneOption = true;
            String name = (String) iter.next();
            String desc = (String) descs.get(name);
            options.put(name, getOptionValue(name, desc));
        }
        if (hasAtLeastOneOption) {
            int c = getChoice("Start rebuilding with the above options?", new String[] {"Yes", "No, let me re-enter the options.", "No, exit."});
            if (c == 0) return options;
            if (c == 1) {
                System.out.println();
                return getOptions(descs);
            }
        } else {
            int c = getChoice("Start rebuilding?", new String[] {"Yes", "No, exit."});
            if (c == 0) return options;
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

    private Rebuilder getRebuilder(File serverDir, ServerConfiguration serverConfig) throws Exception {
        String[] labels = new String[REBUILDERS.length + 1];
        Rebuilder[] rebuilders = new Rebuilder[REBUILDERS.length];
        int i = 0;
        for (i = 0; i < REBUILDERS.length; i++) {
            Rebuilder r = (Rebuilder) Class.forName(REBUILDERS[i]).newInstance();
            labels[i] = r.getAction();
            rebuilders[i] = r;
        }
        labels[i] = "Exit";
        int choiceNum = getChoice("What do you want to do?", labels);
        if (choiceNum == i) {
            return null;
        } else {
            return rebuilders[i-1];
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
                                                String profile) throws IOException {
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
                // System.out.println(param.getName() + " was '" + param.getValue() + "', now '" + profileValue + "'.");
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
//            th.printStackTrace();
        }
    }

}