// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   FaultExceptionBuilderGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.java.JavaException;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.BatchEnvironment;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import sun.tools.java.ClassFile;
import sun.tools.java.Environment;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            GeneratorBase, Names

public class FaultExceptionBuilderGenerator extends GeneratorBase {

    private Port port;
    private Set operations;
    private ArrayList soapBuilders;

    public FaultExceptionBuilderGenerator() {
        soapBuilders = null;
    }

    public GeneratorBase getGenerator(Model model, Configuration config, Properties properties) {
        return new FaultExceptionBuilderGenerator(model, config, properties);
    }

    private FaultExceptionBuilderGenerator(Model model, Configuration config, Properties properties) {
        super(model, config, properties);
        soapBuilders = new ArrayList();
    }

    protected void preVisitModel(Model model) throws Exception {
        operations = new HashSet();
    }

    protected void postVisitModel(Model model) throws Exception {
        operations = null;
    }

    protected void preVisitPort(Port port) throws Exception {
        this.port = port;
    }

    protected void postVisitPort(Port port) throws Exception {
        this.port = null;
    }

    protected void visitOperation(Operation operation) throws Exception {
        if(!isRegistered(operation))
            registerFault(operation);
    }

    private boolean isRegistered(Operation operation) {
        return operations.contains(operation.getName());
    }

    private void registerFault(Operation operation) throws Exception {
        operations.add(operation.getName());
        generateBuilderForOperation(operation);
    }

    private void generateBuilderForOperation(Operation operation) throws IOException {
        if(needsBuilder(operation))
            writeBuilderForOperation(operation);
    }

    public static boolean needsBuilder(Operation operation) {
        Iterator faults = operation.getFaults();
        Fault fault;
        boolean needsBuilder;
        for(needsBuilder = false; !needsBuilder && faults.hasNext(); needsBuilder = needsBuilder(fault))
            fault = (Fault)faults.next();

        return needsBuilder;
    }

    public static boolean needsBuilder(Fault fault) {
        if(fault.getBlock().getType().isSOAPType())
            return ((SOAPType)fault.getBlock().getType()).isReferenceable();
        else
            return false;
    }

    private void writeBuilderForOperation(Operation operation) throws IOException {
        String className = Names.faultBuilderClassName(port, operation);
        File classFile = Names.sourceFileForClass(className, className, super.sourceDir, super.env);
        try {
            super.env.addGeneratedFile(classFile);
            IndentingWriter out = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(classFile)));
            writeObjectBuilderCode(out, operation, className);
            out.close();
            log("wrote file: " + classFile.getPath());
            super.env.parseFile(new ClassFile(classFile));
        }
        catch(IOException ioexception) {
            super.env.error(0L, "cant.write", classFile.toString());
        }
    }

    private void writeObjectBuilderCode(IndentingWriter p, Operation operation, String className) throws IOException {
        log("writing object builder for: " + operation.getName());
        GeneratorBase.writePackage(p, className);
        writeImports(p);
        p.pln();
        writeObjectClassDecl(p, className);
        writeMembers(p, operation);
        p.pln();
        writeMemberGateTypeMethod(p);
        p.pln();
        writeConstructMethod(p);
        p.pln();
        writeSetMemberMethod(p, operation);
        p.pln();
        writeInitializeMethod(p);
        p.pln();
        writeGetSetInstanceMethods(p);
        p.pOln("}");
    }

    private void writeImports(IndentingWriter p) throws IOException {
        p.pln("import com.sun.xml.rpc.encoding.*;");
        p.pln("import com.sun.xml.rpc.soap.message.SOAPFaultInfo;");
        p.pln("import java.lang.IllegalArgumentException;");
    }

    private void writeObjectClassDecl(IndentingWriter p, String className) throws IOException {
        p.plnI("public final class " + Names.mangleClass(className) + " implements SOAPInstanceBuilder {");
    }

    private void writeMembers(IndentingWriter p, Operation operation) throws IOException {
        p.pln("private SOAPFaultInfo instance = null;");
        p.pln("private Object detail;");
        Iterator faults = operation.getFaults();
        int i = 0;
        while(faults.hasNext())  {
            Fault fault = (Fault)faults.next();
            if(needsBuilder(fault)) {
                p.pln("private static final int " + Names.memberName(fault.getJavaException().getPropertyName().toUpperCase() + "_INDEX") + " = " + i + ";");
                i++;
            }
        }
    }

    private void writeMemberGateTypeMethod(IndentingWriter p) throws IOException {
        p.plnI("public int memberGateType(int memberIndex) {");
        p.pln("return GATES_INITIALIZATION + REQUIRES_CREATION;");
        p.pOln("}");
    }

    private void writeConstructMethod(IndentingWriter p) throws IOException {
        p.plnI("public void construct() {");
        p.pOln("}");
    }

    private void writeSetMemberMethod(IndentingWriter p, Operation operation) throws IOException {
        p.plnI("public void setMember(int index, Object memberValue) {");
        p.plnI("switch(index) {");
        for(Iterator faults = operation.getFaults(); faults.hasNext();) {
            Fault fault = (Fault)faults.next();
            if(needsBuilder(fault)) {
                String caseIdx = Names.memberName(fault.getJavaException().getPropertyName().toUpperCase() + "_INDEX");
                String typeName = fault.getJavaException().getPropertyType().getName();
                String name = fault.getJavaException().getPropertyName();
                p.plnI("case " + caseIdx + ":");
                p.pln("detail = new " + Names.customExceptionClassName(fault) + "((" + typeName + ")memberValue);");
                p.pln("break;");
                p.pO();
            }
        }

        p.plnI("default:");
        p.pln("throw new IllegalArgumentException();");
        p.pO();
        p.pOln("}");
        p.pOln("}");
    }

    private void writeInitializeMethod(IndentingWriter p) throws IOException {
        p.plnI("public void initialize() {");
        p.pln("instance.setDetail(detail);");
        p.pOln("}");
    }

    private void writeGetSetInstanceMethods(IndentingWriter p) throws IOException {
        p.plnI("public void setInstance(Object instance) {");
        p.pln("this.instance = (SOAPFaultInfo)instance;");
        p.pOln("}");
        p.pln();
        p.plnI("public Object getInstance() {");
        p.pln("return instance;");
        p.pOln("}");
    }
}
