// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   BatchEnvironment.java

package com.sun.xml.rpc.processor.util;

import com.sun.xml.rpc.processor.ProcessorNotificationListener;
import com.sun.xml.rpc.util.localization.Localizable;
import java.io.File;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import sun.tools.java.ClassPath;
import sun.tools.java.Environment;

public class BatchEnvironment extends sun.tools.javac.BatchEnvironment {

    private ProcessorNotificationListener _listener;
    private Vector generatedFiles;

    public static ClassPath createClassPath(String classPathString) {
        ClassPath paths[] = sun.tools.javac.BatchEnvironment.classPaths(null, classPathString, null, null);
        return paths[1];
    }

    public BatchEnvironment(OutputStream out, ClassPath path, ProcessorNotificationListener listener) {
        super(out, path);
        generatedFiles = new Vector();
        _listener = listener;
    }

    public BatchEnvironment(OutputStream out, ClassPath sourcePath, ClassPath path, ProcessorNotificationListener listener) {
        super(out, sourcePath, path);
        generatedFiles = new Vector();
        _listener = listener;
    }

    public ClassPath getClassPath() {
        return super.binaryPath;
    }

    public void addGeneratedFile(File file) {
        generatedFiles.addElement(file);
    }

    public void deleteGeneratedFiles() {
        synchronized(generatedFiles) {
            File file;
            for(Enumeration enum = generatedFiles.elements(); enum.hasMoreElements(); file.delete())
                file = (File)enum.nextElement();

            generatedFiles.removeAllElements();
        }
    }

    public void shutdown() {
        _listener = null;
        generatedFiles = null;
        super.shutdown();
    }

    public void error(Localizable msg) {
        if(_listener != null)
            _listener.onError(msg);
        super.nerrors++;
        super.flags |= 0x10000;
    }

    public void warn(Localizable msg) {
        if(warnings()) {
            super.nwarnings++;
            if(_listener != null)
                _listener.onWarning(msg);
        }
    }

    public void info(Localizable msg) {
        if(_listener != null)
            _listener.onInfo(msg);
    }
}
