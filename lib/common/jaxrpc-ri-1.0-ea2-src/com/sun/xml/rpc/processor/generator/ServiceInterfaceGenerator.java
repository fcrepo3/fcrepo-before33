// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ServiceInterfaceGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.ProcessorAction;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Service;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.processor.util.BatchEnvironment;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Properties;
import sun.tools.java.ClassFile;
import sun.tools.java.Environment;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            GeneratorException, Names, GeneratorBase

public class ServiceInterfaceGenerator
    implements ProcessorAction {

    private File sourceDir;
    private BatchEnvironment env;

    public ServiceInterfaceGenerator() {
    }

    public void perform(Model model, Configuration config, Properties options) {
        try {
            Names.resetPrefixFactory();
            String key = "sourceDirectory";
            String dirPath = options.getProperty(key);
            sourceDir = new File(dirPath);
            env = config.getEnvironment();
            Service service;
            for(Iterator iter = model.getServices(); iter.hasNext(); process(service))
                service = (Service)iter.next();

        }
        finally {
            sourceDir = null;
            env = null;
        }
    }

    private void process(Service service) {
        try {
            JavaInterface intf = service.getJavaInterface();
            String className = Names.customJavaTypeClassName(intf);
            log("creating service interface: " + className);
            File classFile = Names.sourceFileForClass(className, className, sourceDir, env);
            env.addGeneratedFile(classFile);
            IndentingWriter out = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(classFile)));
            GeneratorBase.writePackage(out, className);
            out.pln("import javax.xml.rpc.*;");
            out.pln();
            out.plnI("public interface " + Names.mangleClass(className) + " extends javax.xml.rpc.Service {");
            String portClass;
            for(Iterator ports = service.getPorts(); ports.hasNext(); out.pln("public " + portClass + " get" + Names.stripQualifier(portClass) + "();")) {
                Port port = (Port)ports.next();
                portClass = port.getJavaInterface().getName();
            }

            out.pOln("}");
            out.close();
            env.parseFile(new ClassFile(classFile));
        }
        catch(Exception e) {
            throw new GeneratorException("generator.nestedGeneratorError", new LocalizableExceptionAdapter(e));
        }
    }

    private void log(String msg) {
        if(env.verbose())
            System.out.println("[" + Names.stripQualifier(getClass().getName()) + ": " + msg + "]");
    }
}
