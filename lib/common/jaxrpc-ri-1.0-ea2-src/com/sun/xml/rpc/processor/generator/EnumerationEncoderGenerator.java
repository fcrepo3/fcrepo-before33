// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   EnumerationEncoderGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.generator.writer.SimpleTypeSerializerWriter;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.java.JavaEnumerationType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.soap.SOAPAnyType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPCustomType;
import com.sun.xml.rpc.processor.model.soap.SOAPEnumerationType;
import com.sun.xml.rpc.processor.model.soap.SOAPSimpleType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.BatchEnvironment;
import com.sun.xml.rpc.processor.util.IndentingWriter;
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
//            GeneratorBase, Names, SimpleToBoxedUtil

public class EnumerationEncoderGenerator extends GeneratorBase {

    private Set types;

    public EnumerationEncoderGenerator() {
    }

    public GeneratorBase getGenerator(Model model, Configuration config, Properties properties) {
        return new EnumerationEncoderGenerator(model, config, properties);
    }

    private EnumerationEncoderGenerator(Model model, Configuration config, Properties properties) {
        super(model, config, properties);
    }

    protected void preVisitModel(Model model) throws Exception {
        types = new HashSet();
    }

    protected void postVisitModel(Model model) throws Exception {
        types = null;
    }

    public void visit(SOAPCustomType type) throws Exception {
        if(isRegistered(type)) {
            return;
        } else {
            registerType(type);
            return;
        }
    }

    public void visit(SOAPSimpleType type) throws Exception {
        if(isRegistered(type)) {
            return;
        } else {
            registerType(type);
            return;
        }
    }

    public void visit(SOAPAnyType type) throws Exception {
        if(isRegistered(type)) {
            return;
        } else {
            registerType(type);
            return;
        }
    }

    public void visit(SOAPEnumerationType type) throws Exception {
        if(isRegistered(type)) {
            return;
        } else {
            registerType(type);
            generateEnumerationSerializer(type);
            return;
        }
    }

    protected void visitSOAPArrayType(SOAPArrayType type) throws Exception {
        if(isRegistered(type)) {
            return;
        } else {
            registerType(type);
            super.visitSOAPArrayType(type);
            return;
        }
    }

    protected void visitSOAPStructureType(SOAPStructureType type) throws Exception {
        if(isRegistered(type)) {
            return;
        } else {
            registerType(type);
            super.visitSOAPStructureType(type);
            return;
        }
    }

    private boolean isRegistered(SOAPType type) {
        return types.contains(type);
    }

    private void registerType(SOAPType type) {
        types.add(type);
    }

    private void generateEnumerationSerializer(SOAPEnumerationType type) {
        log("generating Enumeration for: " + Names.typeObjectSerializerClassName(type));
        try {
            String className = type.getJavaType().getName() + "_Encoder";
            File classFile = Names.sourceFileForClass(className, className, super.sourceDir, super.env);
            super.env.addGeneratedFile(classFile);
            IndentingWriter out = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(classFile)));
            GeneratorBase.writePackage(out, className);
            out.pln();
            writeImports(out);
            out.pln();
            writeClassDecl(out, className);
            out.pln();
            writeMembers(out, type, className);
            out.pln();
            writeConstructor(out, className);
            out.pln();
            writeGetInstance(out);
            out.pln();
            writeObjectToString(out, type);
            out.pln();
            writeStringToObject(out, type);
            out.pln();
            writeGenericMethods(out, type);
            out.pOln("}");
            out.close();
            log("wrote file: " + classFile.getPath());
            super.env.parseFile(new ClassFile(classFile));
        }
        catch(Exception e) {
            fail(e);
        }
    }

    private void writeImports(IndentingWriter p) throws IOException {
        p.pln("import com.sun.xml.rpc.encoding.simpletype.*;");
        p.pln("import javax.xml.rpc.namespace.QName;");
        p.pln("import com.sun.xml.rpc.streaming.*;");
    }

    private void writeClassDecl(IndentingWriter p, String className) throws IOException {
        p.plnI("public class " + Names.mangleClass(className) + " implements SimpleTypeEncoder {");
    }

    private void writeMembers(IndentingWriter p, SOAPEnumerationType type, String className) throws IOException {
        String encoder = SimpleTypeSerializerWriter.getTypeEncoder(type.getBaseType());
        p.pln("private static final SimpleTypeEncoder encoder = " + encoder + ".getInstance();");
        p.pln("private static final " + Names.stripQualifier(className) + " instance = new " + Names.stripQualifier(className) + "();");
    }

    private void writeConstructor(IndentingWriter p, String className) throws IOException {
        p.plnI("private " + Names.stripQualifier(className) + "() {");
        p.pOln("}");
    }

    private void writeGetInstance(IndentingWriter p) throws IOException {
        p.plnI("public static SimpleTypeEncoder getInstance() {");
        p.pln("return instance;");
        p.pOln("}");
    }

    private void writeObjectToString(IndentingWriter p, SOAPEnumerationType type) throws IOException {
        JavaEnumerationType javaEnum = (JavaEnumerationType)type.getJavaType();
        String baseTypeStr = javaEnum.getBaseType().getName();
        String className = Names.stripQualifier(type.getJavaType().getName());
        p.plnI("public String objectToString(Object obj, XMLWriter writer) throws Exception {");
        p.pln(baseTypeStr + " value = ((" + className + ")obj).getValue();");
        String valueExp = "value";
        if(SimpleToBoxedUtil.isPrimitive(baseTypeStr))
            valueExp = SimpleToBoxedUtil.getBoxedExpressionOfType(valueExp, baseTypeStr);
        p.pln("return encoder.objectToString(" + valueExp + ", writer);");
        p.pOln("}");
    }

    private void writeStringToObject(IndentingWriter p, SOAPEnumerationType type) throws IOException {
        JavaEnumerationType javaEnum = (JavaEnumerationType)type.getJavaType();
        String baseTypeStr = javaEnum.getBaseType().getName();
        String className = Names.stripQualifier(type.getJavaType().getName());
        p.plnI("public Object stringToObject(String str, XMLReader reader) throws Exception {");
        String objectExp = "(" + SimpleToBoxedUtil.getBoxedClassName(baseTypeStr) + ")encoder.stringToObject(str, reader)";
        if(SimpleToBoxedUtil.isPrimitive(baseTypeStr))
            objectExp = SimpleToBoxedUtil.getUnboxedExpressionOfType(objectExp, baseTypeStr);
        p.pln("return " + className + ".fromValue(" + objectExp + ");");
        p.pOln("}");
    }

    private void writeGenericMethods(IndentingWriter p, SOAPEnumerationType type) throws IOException {
        p.pln("public void writeAdditionalNamespaceDeclarations(Object obj, XMLWriter writer) throws Exception {");
        p.pln("}");
    }

    private void writeEquals(IndentingWriter p, SOAPEnumerationType type) throws IOException {
        String className = Names.stripQualifier(type.getJavaType().getName());
        p.plnI("public boolean equals(Object obj) {");
        p.plnI("if (!obj instanceof " + className + ") {");
        p.pln("return false;");
        p.pOln("}");
        p.pln("((" + className + ")obj).value.equals(value);");
        p.pOln("}");
    }

    private void writeHashCode(IndentingWriter p, SOAPEnumerationType type) throws IOException {
        p.plnI("public int hashCode() {");
        p.pln("return value.hashCode();");
        p.pOln("}");
    }
}
