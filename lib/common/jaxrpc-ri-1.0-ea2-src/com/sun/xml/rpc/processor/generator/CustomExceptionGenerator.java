// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   CustomExceptionGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.java.JavaException;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.util.BatchEnvironment;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.processor.util.StringUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import sun.tools.java.ClassFile;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            GeneratorBase, Names

public class CustomExceptionGenerator extends GeneratorBase {

    private Set faults;

    public CustomExceptionGenerator() {
    }

    public GeneratorBase getGenerator(Model model, Configuration config, Properties properties) {
        return new CustomExceptionGenerator(model, config, properties);
    }

    private CustomExceptionGenerator(Model model, Configuration config, Properties properties) {
        super(model, config, properties);
    }

    protected void preVisitModel(Model model) throws Exception {
        faults = new HashSet();
    }

    protected void postVisitModel(Model model) throws Exception {
        faults = null;
    }

    protected void preVisitFault(Fault fault) throws Exception {
        if(isRegistered(fault)) {
            return;
        } else {
            registerFault(fault);
            return;
        }
    }

    private boolean isRegistered(Fault fault) {
        return faults.contains(fault.getJavaException().getName());
    }

    private void registerFault(Fault fault) {
        faults.add(fault.getJavaException().getName());
        generateCustomException(fault);
    }

    private void generateCustomException(Fault fault) {
        if(fault.getJavaException().isPresent())
            return;
        log("generating CustomException for: " + fault.getJavaException().getName());
        try {
            String className = Names.customExceptionClassName(fault);
            File classFile = Names.sourceFileForClass(className, className, super.sourceDir, super.env);
            super.env.addGeneratedFile(classFile);
            IndentingWriter out = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(classFile)));
            GeneratorBase.writePackage(out, className);
            out.pln();
            writeClassDecl(out, className);
            writeMembers(out, fault);
            out.pln();
            writeClassConstructor(out, className, fault);
            out.pln();
            writeGetter(out, fault);
            out.pOln("}");
            out.close();
            log("wrote file: " + classFile.getPath());
            super.env.parseFile(new ClassFile(classFile));
        }
        catch(Exception e) {
            fail(e);
        }
    }

    private void writeClassDecl(IndentingWriter p, String className) throws IOException {
        p.plnI("public final class " + Names.mangleClass(className) + " extends Exception {");
    }

    private void writeMembers(IndentingWriter p, Fault fault) throws IOException {
        p.pln("private " + fault.getJavaException().getPropertyType().getName() + " " + fault.getJavaException().getPropertyName() + ";");
    }

    private void writeClassConstructor(IndentingWriter p, String className, Fault fault) throws IOException {
        String type = fault.getJavaException().getPropertyType().getName();
        String name = fault.getJavaException().getPropertyName();
        p.p("public " + Names.mangleClass(className) + "(");
        p.plnI(type + " " + name + ") {");
        p.pln("this." + name + " = " + name + ";");
        p.pOln("}");
    }

    private void writeGetter(IndentingWriter p, Fault fault) throws IOException {
        String type = fault.getJavaException().getPropertyType().getName();
        String name = fault.getJavaException().getPropertyName();
        p.plnI("public " + type + " get" + StringUtils.capitalize(name) + "() {");
        p.pln("return " + name + ";");
        p.pOln("}");
    }
}
