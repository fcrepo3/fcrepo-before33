// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPObjectBuilderGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.BatchEnvironment;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import javax.xml.rpc.namespace.QName;
import sun.tools.java.ClassFile;
import sun.tools.java.Environment;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            GeneratorBase, Names

public class SOAPObjectBuilderGenerator extends GeneratorBase {

    private ArrayList soapBuilders;

    public SOAPObjectBuilderGenerator() {
        soapBuilders = null;
    }

    public GeneratorBase getGenerator(Model model, Configuration config, Properties properties) {
        return new SOAPObjectBuilderGenerator(model, config, properties);
    }

    private SOAPObjectBuilderGenerator(Model model, Configuration config, Properties properties) {
        super(model, config, properties);
        soapBuilders = new ArrayList();
    }

    protected void preVisitFault(Fault fault) throws Exception {
        if(fault.getBlock().getType().isSOAPType())
            ((SOAPType)fault.getBlock().getType()).accept(this);
    }

    protected void preVisitSOAPStructureType(SOAPStructureType structureType) throws Exception {
        if(hasObjectBuilder(structureType))
            return;
        try {
            generateObjectBuilderForType(structureType);
        }
        catch(IOException ioexception) {
            fail("generator.cant.write", structureType.getName().getLocalPart());
        }
    }

    private void generateObjectBuilderForType(SOAPStructureType type) throws IOException {
        addObjectBuilder(type);
        if(needBuilder(type))
            writeObjectBuilderForType(type);
    }

    public static boolean needBuilder(SOAPStructureType type) {
        for(Iterator members = type.getMembers(); members.hasNext();) {
            SOAPStructureMember member = (SOAPStructureMember)members.next();
            if(member.getType().isReferenceable())
                return true;
        }

        return false;
    }

    private void writeObjectBuilderForType(SOAPStructureType type) throws IOException {
        JavaType javaType = type.getJavaType();
        if(javaType == null)
            fail("generator.invalid.model.state.no.javatype", type.getName().getLocalPart());
        String className = Names.typeObjectBuilderClassName(type);
        File classFile = Names.sourceFileForClass(className, className, super.sourceDir, super.env);
        try {
            super.env.addGeneratedFile(classFile);
            IndentingWriter out = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(classFile)));
            writeObjectBuilderCode(out, type);
            out.close();
            log("wrote file: " + classFile.getPath());
            super.env.parseFile(new ClassFile(classFile));
        }
        catch(IOException ioexception) {
            super.env.error(0L, "cant.write", classFile.toString());
        }
    }

    private void writeObjectBuilderCode(IndentingWriter p, SOAPStructureType type) throws IOException {
        log("writing object builder for: " + type.getName().getLocalPart());
        String className = Names.typeObjectBuilderClassName(type);
        GeneratorBase.writePackage(p, className);
        writeImports(p);
        p.pln();
        writeObjectClassDecl(p, className);
        writeMembers(p, type);
        p.pln();
        writeMemberGateTypeMethod(p, type);
        p.pln();
        writeConstructMethod(p, type);
        p.pln();
        writeSetMemberMethod(p, type);
        p.pln();
        writeInitializeMethod(p, type);
        p.pln();
        writeGetSetInstanceMethods(p, type);
        p.pOln("}");
    }

    private void writeImports(IndentingWriter p) throws IOException {
        p.pln("import com.sun.xml.rpc.encoding.*;");
        p.pln("import java.lang.IllegalArgumentException;");
    }

    private void writeObjectClassDecl(IndentingWriter p, String className) throws IOException {
        p.plnI("public final class " + Names.mangleClass(className) + " implements SOAPInstanceBuilder {");
    }

    private void writeMembers(IndentingWriter p, SOAPStructureType type) throws IOException {
        JavaStructureType javaStructure = (JavaStructureType)type.getJavaType();
        p.pln("private " + Names.stripQualifier(javaStructure.getName()) + " _instance;");
        Iterator iterator;
        for(iterator = javaStructure.getMembers(); iterator.hasNext();) {
            JavaStructureMember javaMember = (JavaStructureMember)iterator.next();
            boolean referenceable = ((SOAPStructureMember)javaMember.getOwner()).getType().isReferenceable();
            if(!javaMember.isPublic() && referenceable)
                p.pln("private " + javaMember.getType().getName() + " " + javaMember.getName() + ";");
        }

        iterator = javaStructure.getMembers();
        for(int i = 0; iterator.hasNext(); i++) {
            JavaStructureMember javaMember = (JavaStructureMember)iterator.next();
            SOAPStructureMember member = (SOAPStructureMember)javaMember.getOwner();
            p.p("private static final int ");
            p.pln(Names.memberName(member.getName().getLocalPart().toUpperCase() + "_INDEX") + " = " + i + ";");
        }

    }

    private void writeMemberGateTypeMethod(IndentingWriter p, SOAPStructureType type) throws IOException {
        p.plnI("public int memberGateType(int memberIndex) {");
        p.plnI("switch (memberIndex) {");
        JavaStructureType javaStructure = (JavaStructureType)type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        for(int i = 0; iterator.hasNext(); i++) {
            JavaStructureMember javaMember = (JavaStructureMember)iterator.next();
            SOAPStructureMember member = (SOAPStructureMember)javaMember.getOwner();
            boolean referenceable = member.getType().isReferenceable();
            if(referenceable) {
                p.plnI("case " + Names.memberName(member.getName().getLocalPart().toUpperCase() + "_INDEX") + ":");
                p.pln("return GATES_INITIALIZATION + REQUIRES_CREATION;");
                p.pO();
            }
        }

        p.plnI("default:");
        p.pln("throw new IllegalArgumentException();");
        p.pO();
        p.pOln("}");
        p.pOln("}");
    }

    private void writeConstructMethod(IndentingWriter p, SOAPStructureType type) throws IOException {
        p.plnI("public void construct() {");
        p.pOln("}");
    }

    private void writeSetMemberMethod(IndentingWriter p, SOAPStructureType type) throws IOException {
        p.plnI("public void setMember(int index, Object memberValue) {");
        p.plnI("switch(index) {");
        JavaStructureType javaStructure = (JavaStructureType)type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        for(int i = 0; iterator.hasNext(); i++) {
            JavaStructureMember javaMember = (JavaStructureMember)iterator.next();
            SOAPStructureMember member = (SOAPStructureMember)javaMember.getOwner();
            boolean referenceable = member.getType().isReferenceable();
            if(referenceable) {
                p.plnI("case " + Names.memberName(member.getName().getLocalPart().toUpperCase() + "_INDEX") + ":");
                p.p("_instance.");
                if(javaMember.isPublic())
                    p.pln(javaMember.getName() + " = (" + javaMember.getType().getName() + ")memberValue;");
                else
                    p.pln(javaMember.getWriteMethod() + "((" + javaMember.getType().getName() + ")memberValue);");
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

    private void writeInitializeMethod(IndentingWriter p, SOAPStructureType type) throws IOException {
        JavaStructureType javaStructure = (JavaStructureType)type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        p.plnI("public void initialize() {");
        p.pOln("}");
    }

    private void writeGetSetInstanceMethods(IndentingWriter p, SOAPStructureType type) throws IOException {
        p.plnI("public void setInstance(Object instance) {");
        p.pln("_instance = (" + Names.stripQualifier(type.getJavaType().getName()) + ")instance;");
        p.pOln("}");
        p.pln();
        p.plnI("public Object getInstance() {");
        p.pln("return _instance;");
        p.pOln("}");
    }

    private boolean hasObjectBuilder(SOAPType type) {
        return soapBuilders.contains(type);
    }

    private void addObjectBuilder(SOAPType type) throws IOException {
        log("adding builder for: " + type.getName().getLocalPart());
        if(soapBuilders.contains(type))
            fail("Internal error: attempting to add duplicate SOAP serializer");
        soapBuilders.add(type);
    }
}
