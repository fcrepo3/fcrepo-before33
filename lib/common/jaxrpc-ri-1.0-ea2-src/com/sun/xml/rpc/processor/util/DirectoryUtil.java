// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   DirectoryUtil.java

package com.sun.xml.rpc.processor.util;

import java.io.File;
import sun.tools.java.*;

// Referenced classes of package com.sun.xml.rpc.processor.util:
//            BatchEnvironment

public class DirectoryUtil
    implements Constants {

    public DirectoryUtil() {
    }

    public static File getOutputDirectoryFor(String theClass, File rootDir, BatchEnvironment env) {
        return getOutputDirectoryFor(Identifier.lookup(theClass), rootDir, env);
    }

    public static File getOutputDirectoryFor(Identifier theClass, File rootDir, BatchEnvironment env) {
        File outputDir = null;
        String className = theClass.getFlatName().toString().replace('.', '$');
        String qualifiedClassName = className;
        String packagePath = null;
        String packageName = theClass.getQualifier().toString();
        if(packageName.length() > 0) {
            qualifiedClassName = packageName + "." + className;
            packagePath = packageName.replace('.', File.separatorChar);
        }
        if(rootDir != null) {
            if(packagePath != null) {
                outputDir = new File(rootDir, packagePath);
                ensureDirectory(outputDir, env);
            } else {
                outputDir = rootDir;
            }
        } else {
            String workingDirPath = System.getProperty("user.dir");
            File workingDir = new File(workingDirPath);
            if(packagePath == null) {
                outputDir = workingDir;
            } else {
                outputDir = new File(workingDir, packagePath);
                ensureDirectory(outputDir, env);
            }
        }
        return outputDir;
    }

    private static void ensureDirectory(File dir, BatchEnvironment env) {
        if(!dir.exists()) {
            dir.mkdirs();
            if(!dir.exists()) {
                env.error(0L, "xrpcc.cannot.create.dir", dir.getAbsolutePath());
                throw new InternalError();
            }
        }
    }
}
