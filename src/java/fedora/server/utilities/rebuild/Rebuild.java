package fedora.server.utilities.rebuild;

import java.io.*;
import java.util.*;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.*;
import org.apache.log4j.xml.*;
import org.xml.sax.SAXException;

import fedora.server.config.*;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.StreamIOException;
import fedora.server.storage.translation.DODeserializer;
import fedora.server.storage.translation.DOTranslationUtility;
import fedora.server.storage.translation.FOXMLDODeserializer;
import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DigitalObject;

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
            "fedora.server.utilities.rebuild.NoOpRebuilder",
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
            if (options != null) {
                System.out.println();
                System.out.println("Rebuilding...");
                try {
                    rebuilder.start(options);
                    // fedora.server.storage.lowlevel.Configuration conf = fedora.server.storage.lowlevel.Configuration.getInstance();
                    // String objStoreBaseStr = conf.getObjectStoreBase();
                    String objStoreBaseStr = serverConfig.getParameter("object_store_base").getValue();
                    File dir = new File(objStoreBaseStr);
                    multiFromDirectory(dir, "DMO", rebuilder);
                    
                } 
                finally {
                    rebuilder.finish();
                }
                System.out.println("Finished.");
                System.out.println();
            }
        }
    }
     
    public void multiFromDirectory(File dir, String fTypes, Rebuilder rebuilder)
                                              throws Exception 
    {
        String tps=fTypes.toUpperCase();
        if (tps.indexOf("D")!=-1) 
        {
            rebuildAll(getFiles(dir, "FedoraBDefObject"), rebuilder);
        }
        if (tps.indexOf("M")!=-1) 
        {
            rebuildAll(getFiles(dir, "FedoraBMechObject"), rebuilder);
        }
        if (tps.indexOf("O")!=-1) 
        {
            rebuildAll(getFiles(dir, "FedoraObject"), rebuilder);
        }
    }
    
    
    private void rebuildAll(Set fileSet, Rebuilder rebuilder)
    {
        Iterator setIter = fileSet.iterator();
        while (setIter.hasNext())
        {
            File file = (File)(setIter.next());
            DigitalObject obj = new BasicDigitalObject();
            DODeserializer deserializer;
            try
            {
                System.out.println(file.getAbsoluteFile());
                deserializer = new FOXMLDODeserializer();
                deserializer.deserialize(new FileInputStream(file), obj, "UTF-8", 
                        DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL);
                rebuilder.addObject(obj);

            } 
            catch (UnsupportedEncodingException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
            catch (FactoryConfigurationError e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
            catch (ParserConfigurationException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
            catch (SAXException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
            catch (ObjectIntegrityException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
            catch (StreamIOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
            catch (FileNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private Set getFiles(File dir, String fTypeString)
                        throws Exception 
    {
        LinkedHashSet set = new LinkedHashSet();
        if (!dir.isDirectory()) 
        {
            throw new IOException("Not a directory: " + dir.getPath());
        }
        File[] files = dir.listFiles();
        for (int i=0; i<files.length; i++) 
        {
            if (files[i].isDirectory()) 
            {
                set.addAll(getFiles(files[i], fTypeString));
            }   
            else 
            {
                // if the file is a candidate, add it
                BufferedReader in = new BufferedReader(new FileReader(files[i]));
                boolean isCandidate = false;
                String line;
                while ( (line = in.readLine()) != null ) 
                {
                    if (line.indexOf(fTypeString)!=-1) 
                    {
                        isCandidate=true;
                        break;
                    }
                }
                if (isCandidate) 
                {
                    set.add(files[i]);
                }
            }
        }
        return(set);
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
