package fedora.utilities;

import java.io.*;
import java.util.*;
import java.util.jar.*;
import com.twmacinta.util.MD5;
import com.twmacinta.util.MD5InputStream;

/**
 * Makes a patch of Fedora client/server distribution.
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
public class MakePatch {

    private boolean m_beVerbose;
    private boolean diffPrompt=false;
    private BufferedReader br=new BufferedReader(new InputStreamReader(System.in));

    public MakePatch(File originBinDir, File originSrcDir, String originVer,
            File targetBinDir, File targetSrcDir, String targetVer, 
            File destDir, boolean beVerbose, boolean diffPrompt) 
            throws Exception {
        MD5.initNativeLibrary(true); // never check for native libs
        m_beVerbose=beVerbose;
        this.diffPrompt=diffPrompt;
        print("Building patch...");
        print("File 1/3 - Client");
        ArrayList clientPatchList=new ArrayList();
        File targetClientDir=new File(targetBinDir, "client");
        getDiffs(new File(originBinDir, "client"), 
                targetClientDir, clientPatchList, "");
        makeJar(new File(destDir, "fedora-" + originVer + "-client-to-" + targetVer + "-patch.jar"), targetClientDir, clientPatchList);
        print("File 2/3 - Server");
        ArrayList serverPatchList=new ArrayList();
        File targetServerDir=new File(targetBinDir, "server");
        getDiffs(new File(originBinDir, "server"), targetServerDir, 
                serverPatchList, "");
        makeJar(new File(destDir, "fedora-" + originVer + "-server-to-" + targetVer + "-patch.jar"), targetServerDir, serverPatchList);
        print("File 3/3 - Source");
        ArrayList sourcePatchList=new ArrayList();
        getDiffs(originSrcDir, targetSrcDir, sourcePatchList, "");
        makeJar(new File(destDir, "fedora-" + originVer + "-src-to-" + targetVer + "-patch.jar"), targetSrcDir, sourcePatchList);
        print("Done.");
    }

    public void makeJar(File outFile, File inDir, List fList) throws Exception {
        JarOutputStream out=new JarOutputStream(new FileOutputStream(outFile));
        byte[] buf = new byte[8192];
        for (int i=0; i<fList.size(); i++) {
            String name=(String) fList.get(i);
            File f=new File(inDir, name);
            JarEntry entry=new JarEntry(name);
            entry.setSize(getSize(f));
            out.putNextEntry(entry);
            FileInputStream in=new FileInputStream(f);
            int len;
            while ( ( len = in.read( buf ) ) != -1 ) {
                out.write( buf, 0, len );
            }
            in.close();
            out.closeEntry();
        }
        out.close();
    }

    public long getSize(File f) throws Exception {
        long size=0;
        FileInputStream s=new FileInputStream(f);
        byte[] buf = new byte[8192];
        int len;
        while ( ( len = s.read( buf ) ) != -1 ) {
            size+=len;
        }
        s.close();
        return size;
    }

    // Put list of filenames of files that are
    // new or different in "to" into "out".
    public void getDiffs(File from, File to, ArrayList out, String prependPath) 
            throws Exception {
        verbose("Getting diffs for ./" + prependPath);
        String[] toNames=to.list();
        boolean fromIsDir=from.isDirectory();
        for (int i=0; i<toNames.length; i++) {
            File toFile=new File(to, toNames[i]);
            if (toFile.isDirectory()) {
                getDiffs(new File(from, toNames[i]), toFile, out, prependPath + toNames[i] + "/");
            } else {
                if (!fromIsDir) {
                    // all files must be added since its a new dir
                    String a=prependPath + toNames[i];
                    verbose("Adding file because its dir is new: " + a);
                    out.add(a);
                } else {
                    // check for existence of each file
                    File fromFile=new File(from, toNames[i]);
                    if (!fromFile.exists()) {
                        String a=prependPath + toNames[i];
                        verbose("Adding file because it is new: " + a);
                        out.add(a);
                    } else {
                        // both exist, so check for diffs
                        if (filesDiffer(fromFile, toFile)) {
                            String a=prependPath + toNames[i];
                            String r=null;
                            if (diffPrompt) {
                            System.out.println("File " + a + " has changed.  Include in patch? [y]es/no ");
                            r=br.readLine(); 
                            }
                            if (!diffPrompt || r.equals("") || r.toUpperCase().startsWith("y")) {
                                verbose("Adding file because it differs: " + a);
                                out.add(a);
                            } else {
                                verbose("Skipped " + a + " even though it differs.");
                            }
                        } else {
                       /*     String a=prependPath + toNames[i];
                            verbose("Unchanged, skipping: " + a); */
                        }
                    }
                }
            }
        }
    }

    public boolean filesDiffer(File a, File b) 
            throws Exception {
        return (!getChecksum(a).equals(getChecksum(b)));
    }

    public String getChecksum(File f) 
            throws Exception {
        MD5InputStream s=new MD5InputStream(new FileInputStream(f));
        byte[] buf = new byte[8192];
        int len;
        while ( ( len = s.read( buf ) ) != -1 ) {
        }
        String out=MD5.asHex(s.hash());
        s.close();
        return out;
    }

    public void verbose(String msg) {
        if (m_beVerbose) System.out.println(msg);
    }

    public void print(String msg) {
        System.out.println(msg);
    }

    public static String getFedoraVersion(File dir, boolean binDist)
            throws Exception {
        if (binDist) {
            if (!dir.exists()) throw new IOException("Provided directory, " + dir.getAbsolutePath() + " does not exist!");
            if (!dir.isDirectory()) throw new IOException("Provided directory, " + dir.getAbsolutePath() + " is not a directory!");
            String[] dirNames=dir.list();
            boolean foundClient=false;
            boolean foundServer=false;
            for (int i=0; i<dirNames.length; i++) {
                if (dirNames[i].equals("client")) foundClient=true;
                if (dirNames[i].equals("server")) foundServer=true;
            }
            if (!foundClient) throw new IOException("Cannot find client dir in " + dir.getAbsolutePath());
            if (!foundServer) throw new IOException("Cannot find server dir in " + dir.getAbsolutePath());
        }
        Properties props=new Properties();
        if (binDist) {
            props.load(new FileInputStream(new File(dir, "server/tomcat41/webapps/fedora/WEB-INF/classes/fedora/server/resources/Server.properties")));
        } else {
            props.load(new FileInputStream(new File(dir, "src/properties/server/fedora/server/resources/Server.properties")));
        }
        String major=props.getProperty("version.major");
        String minor=props.getProperty("version.minor");
        return major + "." + minor; 
    }

    public static void main(String[] args) {
        try {
            System.out.println("----------------------------------------------");
            System.out.println("MakePatch - Build a Fedora distribution patch.");
            System.out.println("----------------------------------------------");
        System.out.println("This program will create patch files to go from one complete");
        System.out.println("Fedora distribution to another.\n");
            System.out.println("Before continuing, you should have two complete");
            System.out.println("Fedora distributions (client, server, and full source for each)");
            System.out.println("unpacked in directories that you can provide to this program.\n");
            System.out.println("Do you have this information? [y]es/no ");
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            String r=br.readLine();
            System.out.println();
            if (r.equals("") || r.toUpperCase().startsWith("Y")) {
                //
                // Get origin information
                //
                System.out.println("Enter the FEDORA_HOME directory of the origin binary distribution.");
                System.out.println("(this is the directory with the client/ and server/ directories)");
                System.out.println("Origin Binary Distribution Directory: ");
                File originBinDir=new File(br.readLine());
                String originBinVer=MakePatch.getFedoraVersion(originBinDir, true);
                System.out.println("Appears to be a Fedora v" + originBinVer + " binary distribution, ok.\n");
                System.out.println("Enter the installation directory of the origin source distribution.");
                System.out.println("(this is the directory with the build.xml file)");
                System.out.println("Origin Source Distribution Directory: ");
                File originSrcDir=new File(br.readLine());
                String originSrcVer=MakePatch.getFedoraVersion(originSrcDir, false);
                if (!originSrcVer.equals(originBinVer)) {
                    throw new IOException("Origin binary version is " + originBinVer + ", but origin source version is " + originSrcVer);
                }
                System.out.println("Origin source and binary distributions match, good.\n");
                //
                // Get target information
                //
                System.out.println("Target Binary Distribution Directory: ");
                File targetBinDir=new File(br.readLine());
                String targetBinVer=MakePatch.getFedoraVersion(targetBinDir, true);
                if (originBinVer.equals(targetBinVer)) {
                    throw new IOException("Target binary version can't be the same as origin binary version.");
                }
                System.out.println("Appears to be a Fedora v" + targetBinVer + " binary distribution, ok.\n");
                System.out.println("Target Source Distribution Directory: ");
                File targetSrcDir=new File(br.readLine());
                String targetSrcVer=MakePatch.getFedoraVersion(targetSrcDir, false);
                if (!targetSrcVer.equals(targetBinVer)) {
                    throw new IOException("Target binary version is " + targetBinVer + ", but target source version is " + targetSrcVer);
                }
                System.out.println("Target source and binary distributions match, good.\n");
                //
                // Get output dir
                //
                System.out.println("Enter the directory where the patch files should be written.");
                System.out.println("(if it doesn't exist, it will be created)");
                System.out.println("Output Directory: ");
                File outputDir=new File(br.readLine());
                if (!outputDir.exists()) {
                    if (outputDir.mkdirs()) {
                        System.out.println("Created output directory " + outputDir.getAbsolutePath());
                    } else {
                        throw new IOException("Unable to create output directory.");
                    }
                }
                //
                // Confirm and start
                //
                System.out.println("\nAbout to create patch files for Fedora v" 
                        + originSrcVer + " to v" + targetSrcVer 
                        + " upgrade.");
                System.out.println("Want to be prompted before adding items that differ? [y]es/no ");
                r=br.readLine();
                boolean doPrompt=false;
                if (r.equals("") || r.toUpperCase().startsWith("Y")) {
                    doPrompt=true;
                }
                System.out.println("Want verbose output? [y]es/no/cancel ");
                r=br.readLine();
                boolean beVerbose=false;
                if (r.equals("") || r.toUpperCase().startsWith("Y")) {
                    beVerbose=true;
                }
                if (r.toUpperCase().startsWith("C")) {
                    System.out.println("Patch canceled.");
                } else {
                    MakePatch p=new MakePatch(originBinDir, originSrcDir, 
                            originSrcVer, targetBinDir, targetSrcDir, 
                            targetSrcVer, outputDir, beVerbose, doPrompt);
                }
            } else {
                System.out.println("Re-run this program when you have the information.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
