// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   CustomClassGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.LiteralAllType;
import com.sun.xml.rpc.processor.model.literal.LiteralAttributeMember;
import com.sun.xml.rpc.processor.model.literal.LiteralAttributeOwningType;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.BatchEnvironment;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.processor.util.StringUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import javax.xml.rpc.namespace.QName;
import sun.tools.java.ClassFile;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            GeneratorBase, Names

public class CustomClassGenerator extends GeneratorBase {

    private Set types;

    public CustomClassGenerator() {
    }

    public GeneratorBase getGenerator(Model model, Configuration config, Properties properties) {
        return new CustomClassGenerator(model, config, properties);
    }

    private CustomClassGenerator(Model model, Configuration config, Properties properties) {
        super(model, config, properties);
    }

    protected void preVisitModel(Model model) throws Exception {
        types = new HashSet();
    }

    protected void postVisitModel(Model model) throws Exception {
        types = null;
    }

    public void visit(SOAPArrayType type) throws Exception {
        if(isRegistered(type)) {
            return;
        } else {
            registerType(type);
            SOAPType elemType = type.getElementType();
            elemType.accept(this);
            return;
        }
    }

    public void visit(SOAPStructureType type) throws Exception {
        if(isRegistered(type))
            return;
        registerType(type);
        if(!type.getJavaType().isPresent())
            generateJavaClass(type);
        SOAPStructureMember member;
        for(Iterator members = type.getMembers(); members.hasNext(); member.getType().accept(this))
            member = (SOAPStructureMember)members.next();

    }

    public void visit(LiteralSequenceType type) throws Exception {
        if(isRegistered(type))
            return;
        registerType(type);
        if(!type.getJavaType().isPresent())
            generateJavaClass(type);
        LiteralAttributeMember attribute;
        for(Iterator attributes = type.getAttributeMembers(); attributes.hasNext(); attribute.getType().accept(this))
            attribute = (LiteralAttributeMember)attributes.next();

        LiteralElementMember element;
        for(Iterator elements = type.getElementMembers(); elements.hasNext(); element.getType().accept(this))
            element = (LiteralElementMember)elements.next();

    }

    public void visit(LiteralAllType type) throws Exception {
        if(isRegistered(type))
            return;
        registerType(type);
        if(!type.getJavaType().isPresent())
            generateJavaClass(type);
        LiteralAttributeMember attribute;
        for(Iterator attributes = type.getAttributeMembers(); attributes.hasNext(); attribute.getType().accept(this))
            attribute = (LiteralAttributeMember)attributes.next();

        LiteralElementMember element;
        for(Iterator elements = type.getElementMembers(); elements.hasNext(); element.getType().accept(this))
            element = (LiteralElementMember)elements.next();

    }

    private boolean isRegistered(AbstractType type) {
        return types.contains(type);
    }

    private void registerType(AbstractType type) {
        types.add(type);
    }

    private void generateJavaClass(SOAPStructureType type) {
        log("generating JavaClass for: " + type.getName().getLocalPart());
        try {
            String className = Names.customJavaTypeClassName(type);
            File classFile = Names.sourceFileForClass(className, className, super.sourceDir, super.env);
            super.env.addGeneratedFile(classFile);
            IndentingWriter out = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(classFile)));
            GeneratorBase.writePackage(out, className);
            out.pln();
            writeClassDecl(out, className);
            JavaStructureType javaStructure = (JavaStructureType)type.getJavaType();
            for(Iterator members = javaStructure.getMembers(); members.hasNext();) {
                JavaStructureMember member = (JavaStructureMember)members.next();
                JavaType javaType = member.getType();
                String typeName = member.getType().getName();
                if(member.isPublic())
                    out.pln("public " + typeName + " " + member.getName() + ";");
                else
                    out.pln("private " + typeName + " " + member.getName() + ";");
            }

            out.pln();
            writeClassConstructor(out, className, javaStructure);
            for(Iterator members = javaStructure.getMembers(); members.hasNext(); out.pOln("}")) {
                out.pln();
                JavaStructureMember member = (JavaStructureMember)members.next();
                JavaType javaType = member.getType();
                out.plnI("public " + member.getType().getName() + " get" + StringUtils.capitalize(member.getName()) + "() {");
                out.pln("return " + member.getName() + ";");
                out.pOln("}");
                out.pln();
                out.plnI("public void set" + StringUtils.capitalize(member.getName()) + "(" + member.getType().getName() + " " + member.getName() + ") {");
                out.pln("this." + member.getName() + " = " + member.getName() + ";");
            }

            out.pOln("}");
            out.close();
            log("wrote file: " + classFile.getPath());
            super.env.parseFile(new ClassFile(classFile));
        }
        catch(Exception e) {
            fail(e);
        }
    }

    private void generateJavaClass(LiteralStructuredType type) {
        log("generating JavaClass for: " + type.getName().getLocalPart());
        try {
            String className = Names.customJavaTypeClassName(type);
            File classFile = Names.sourceFileForClass(className, className, super.sourceDir, super.env);
            super.env.addGeneratedFile(classFile);
            IndentingWriter out = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(classFile)));
            GeneratorBase.writePackage(out, className);
            out.pln();
            writeClassDecl(out, className);
            JavaStructureType javaStructure = (JavaStructureType)type.getJavaType();
            for(Iterator members = javaStructure.getMembers(); members.hasNext();) {
                JavaStructureMember member = (JavaStructureMember)members.next();
                JavaType javaType = member.getType();
                String typeName = member.getType().getName();
                if(member.isPublic())
                    out.pln("public " + typeName + " " + member.getName() + ";");
                else
                    out.pln("private " + typeName + " " + member.getName() + ";");
            }

            out.pln();
            writeClassConstructor(out, className, javaStructure);
            for(Iterator members = javaStructure.getMembers(); members.hasNext(); out.pOln("}")) {
                out.pln();
                JavaStructureMember member = (JavaStructureMember)members.next();
                JavaType javaType = member.getType();
                out.plnI("public " + member.getType().getName() + " get" + StringUtils.capitalize(member.getName()) + "() {");
                out.pln("return " + member.getName() + ";");
                out.pOln("}");
                out.pln();
                out.plnI("public void set" + StringUtils.capitalize(member.getName()) + "(" + member.getType().getName() + " " + member.getName() + ") {");
                out.pln("this." + member.getName() + " = " + member.getName() + ";");
            }

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
        p.plnI("public final class " + Names.mangleClass(className) + " {");
    }

    private void writeClassConstructor(IndentingWriter p, String className, JavaStructureType javaStructure) throws IOException {
        p.pln("public " + Names.mangleClass(className) + "() {");
        p.pln("}");
        Iterator members = javaStructure.getMembers();
        if(members.hasNext()) {
            p.pln();
            p.p("public " + Names.mangleClass(className) + "(");
            for(int i = 0; members.hasNext(); i++) {
                if(i != 0)
                    p.p(", ");
                JavaStructureMember member = (JavaStructureMember)members.next();
                JavaType javaType = member.getType();
                p.p(member.getType().getName() + " " + member.getName());
            }

            p.plnI(") {");
            members = javaStructure.getMembers();
            for(int i = 0; members.hasNext(); i++) {
                JavaStructureMember member = (JavaStructureMember)members.next();
                JavaType javaType = member.getType();
                p.pln("this." + member.getName() + " = " + member.getName() + ";");
            }

            p.pOln("}");
        }
    }
}
