// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   RemoteInterfaceGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.ProcessorAction;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Service;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.processor.model.java.JavaMethod;
import com.sun.xml.rpc.processor.model.java.JavaParameter;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.util.BatchEnvironment;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Properties;
import sun.tools.java.ClassFile;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            GeneratorException, Names, GeneratorBase

public class RemoteInterfaceGenerator
    implements ProcessorAction {

    private File sourceDir;
    private BatchEnvironment env;

    public RemoteInterfaceGenerator() {
    }

    public void perform(Model model, Configuration config, Properties options) {
        try {
            Names.resetPrefixFactory();
            String key = "sourceDirectory";
            String dirPath = options.getProperty(key);
            sourceDir = new File(dirPath);
            env = config.getEnvironment();
            for(Iterator iter = model.getServices(); iter.hasNext();) {
                Service service = (Service)iter.next();
                Port port;
                for(Iterator iter2 = service.getPorts(); iter2.hasNext(); generateClassFor(port))
                    port = (Port)iter2.next();

            }

        }
        finally {
            sourceDir = null;
            env = null;
        }
    }

    private void generateClassFor(Port port) {
        JavaInterface intf = port.getJavaInterface();
        try {
            String className = Names.customJavaTypeClassName(intf);
            File classFile = Names.sourceFileForClass(className, className, sourceDir, env);
            env.addGeneratedFile(classFile);
            IndentingWriter out = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(classFile)));
            GeneratorBase.writePackage(out, className);
            out.plnI("public interface " + Names.mangleClass(className) + " extends java.rmi.Remote {");
            for(Iterator iter = intf.getMethods(); iter.hasNext(); out.pO()) {
                JavaMethod method = (JavaMethod)iter.next();
                out.p("public ");
                if(method.getReturnType() == null)
                    out.p("void");
                else
                    out.p(method.getReturnType().getName());
                out.p(" ");
                out.p(method.getName());
                out.p("(");
                boolean first = true;
                for(Iterator iter2 = method.getParameters(); iter2.hasNext();) {
                    JavaParameter parameter = (JavaParameter)iter2.next();
                    if(!first)
                        out.p(", ");
                    if(parameter.isHolder())
                        out.p(Names.holderClassName(port, parameter.getType()));
                    else
                        out.p(Names.typeClassName(parameter.getType()));
                    out.p(" ");
                    out.p(parameter.getName());
                    first = false;
                }

                out.plnI(") throws ");
                String exception;
                for(Iterator exceptions = method.getExceptions(); exceptions.hasNext(); out.p(exception + ", "))
                    exception = (String)exceptions.next();

                out.pln(" java.rmi.RemoteException;");
            }

            out.pOln("}");
            out.close();
            env.parseFile(new ClassFile(classFile));
        }
        catch(Exception e) {
            throw new GeneratorException("generator.nestedGeneratorError", new LocalizableExceptionAdapter(e));
        }
    }
}
