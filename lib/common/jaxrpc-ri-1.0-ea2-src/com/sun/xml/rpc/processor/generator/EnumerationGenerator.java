// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   EnumerationGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.java.JavaEnumerationEntry;
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
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import sun.tools.java.ClassFile;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            GeneratorBase, Names, SimpleToBoxedUtil

public class EnumerationGenerator extends GeneratorBase {

    private Set types;

    public EnumerationGenerator() {
    }

    public GeneratorBase getGenerator(Model model, Configuration config, Properties properties) {
        return new EnumerationGenerator(model, config, properties);
    }

    private EnumerationGenerator(Model model, Configuration config, Properties properties) {
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
            generateEnumeration(type);
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

    private void generateEnumeration(SOAPEnumerationType type) {
        log("generating Enumeration for: " + type.getJavaType().getName());
        try {
            String className = type.getJavaType().getName();
            File classFile = Names.sourceFileForClass(className, className, super.sourceDir, super.env);
            super.env.addGeneratedFile(classFile);
            IndentingWriter out = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(classFile)));
            GeneratorBase.writePackage(out, className);
            out.pln();
            writeClassDecl(out, className);
            writeMembers(out, type);
            out.pln();
            writeClassConstructor(out, className, type);
            out.pln();
            writeGetValue(out, type);
            out.pln();
            writeFromValue(out, type);
            out.pln();
            writeFromString(out, type);
            out.pln();
            writeToString(out, type);
            out.pln();
            writeEquals(out, type);
            out.pln();
            writeHashCode(out, type);
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
        p.plnI("public class " + Names.mangleClass(className) + " implements java.io.Serializable {");
    }

    private void writeMembers(IndentingWriter p, SOAPEnumerationType type) throws IOException {
        JavaEnumerationType javaEnum = (JavaEnumerationType)type.getJavaType();
        String baseTypeStr = javaEnum.getBaseType().getName();
        String className = Names.stripQualifier(type.getJavaType().getName());
        p.pln("private " + baseTypeStr + " value;");
        Iterator enums = javaEnum.getEntries();
        if(!SimpleToBoxedUtil.isPrimitive(baseTypeStr)) {
            JavaEnumerationEntry entry;
            for(enums = javaEnum.getEntries(); enums.hasNext(); p.pln("public static final String _" + entry.getName() + "String = \"" + entry.getLiteralValue() + "\";"))
                entry = (JavaEnumerationEntry)enums.next();

            p.pln();
        }
        for(enums = javaEnum.getEntries(); enums.hasNext();) {
            JavaEnumerationEntry entry = (JavaEnumerationEntry)enums.next();
            if(SimpleToBoxedUtil.isPrimitive(baseTypeStr)) {
                if(baseTypeStr.equals("long"))
                    p.pln("public static final " + baseTypeStr + " _" + entry.getName() + " = " + entry.getLiteralValue() + "L;");
                else
                    p.pln("public static final " + baseTypeStr + " _" + entry.getName() + " = (" + baseTypeStr + ")" + entry.getLiteralValue() + ";");
            } else {
                p.pln("public static final " + baseTypeStr + " _" + entry.getName() + " = new " + baseTypeStr + "(_" + entry.getName() + "String);");
            }
        }

        p.pln();
        JavaEnumerationEntry entry;
        for(enums = javaEnum.getEntries(); enums.hasNext(); p.pln("public static final " + className + " " + entry.getName() + " = new " + className + "(_" + entry.getName() + ");"))
            entry = (JavaEnumerationEntry)enums.next();

    }

    private void writeClassConstructor(IndentingWriter p, String className, SOAPType type) throws IOException {
        JavaEnumerationType javaEnum = (JavaEnumerationType)type.getJavaType();
        String baseTypeStr = javaEnum.getBaseType().getName();
        p.plnI("protected " + Names.mangleClass(className) + "(" + baseTypeStr + " value) {");
        p.pln("this.value = value;");
        p.pOln("}");
    }

    private void writeGetValue(IndentingWriter p, SOAPEnumerationType type) throws IOException {
        JavaEnumerationType javaEnum = (JavaEnumerationType)type.getJavaType();
        String baseTypeStr = javaEnum.getBaseType().getName();
        p.plnI("public " + baseTypeStr + " getValue() {");
        p.pln("return value;");
        p.pOln("}");
    }

    private void writeFromValue(IndentingWriter p, SOAPEnumerationType type) throws IOException {
        JavaEnumerationType javaEnum = (JavaEnumerationType)type.getJavaType();
        String baseTypeStr = javaEnum.getBaseType().getName();
        String className = Names.stripQualifier(type.getJavaType().getName());
        p.plnI("public static " + className + " fromValue(" + baseTypeStr + " value)");
        p.pln("throws java.lang.IllegalStateException {");
        Iterator enums = javaEnum.getEntries();
        for(int i = 0; enums.hasNext(); i++) {
            JavaEnumerationEntry entry = (JavaEnumerationEntry)enums.next();
            if(i > 0)
                p.p(" else ");
            if(SimpleToBoxedUtil.isPrimitive(baseTypeStr))
                p.plnI("if (" + entry.getName() + ".value == value) {");
            else
                p.plnI("if (" + entry.getName() + ".value.equals(value)) {");
            p.pln("return " + entry.getName() + ";");
            p.pO("}");
        }

        p.pln();
        p.pln("throw new IllegalArgumentException();");
        p.pOln("}");
    }

    private void writeFromString(IndentingWriter p, SOAPEnumerationType type) throws IOException {
        JavaEnumerationType javaEnum = (JavaEnumerationType)type.getJavaType();
        String baseTypeStr = javaEnum.getBaseType().getName();
        String className = Names.stripQualifier(type.getJavaType().getName());
        p.plnI("public static " + className + " fromString(String value)");
        p.pln("throws java.lang.IllegalStateException {");
        Iterator enums = javaEnum.getEntries();
        for(int i = 0; enums.hasNext(); i++) {
            JavaEnumerationEntry entry = (JavaEnumerationEntry)enums.next();
            if(i > 0)
                p.p(" else ");
            if(SimpleToBoxedUtil.isPrimitive(baseTypeStr))
                p.plnI("if (value.equals(\"" + entry.getLiteralValue() + "\")) {");
            else
                p.plnI("if (value.equals(_" + entry.getName() + "String)) {");
            p.pln("return " + entry.getName() + ";");
            p.pO("}");
        }

        p.pln();
        p.pln("throw new IllegalArgumentException();");
        p.pOln("}");
    }

    private void writeToString(IndentingWriter p, SOAPEnumerationType type) throws IOException {
        JavaEnumerationType javaEnum = (JavaEnumerationType)type.getJavaType();
        String baseTypeStr = javaEnum.getBaseType().getName();
        String className = Names.stripQualifier(type.getJavaType().getName());
        p.plnI("public String toString() {");
        String exp = "value";
        if(SimpleToBoxedUtil.isPrimitive(baseTypeStr))
            exp = SimpleToBoxedUtil.getBoxedExpressionOfType(exp, baseTypeStr);
        p.pln("return " + exp + ".toString();");
        p.pOln("}");
    }

    private void writeEquals(IndentingWriter p, SOAPEnumerationType type) throws IOException {
        JavaEnumerationType javaEnum = (JavaEnumerationType)type.getJavaType();
        String baseTypeStr = javaEnum.getBaseType().getName();
        String className = Names.stripQualifier(type.getJavaType().getName());
        p.plnI("public boolean equals(Object obj) {");
        p.plnI("if (!(obj instanceof " + className + ")) {");
        p.pln("return false;");
        p.pOln("}");
        if(SimpleToBoxedUtil.isPrimitive(baseTypeStr))
            p.pln("return ((" + className + ")obj).value == value;");
        else
            p.pln("return ((" + className + ")obj).value.equals(value);");
        p.pOln("}");
    }

    private void writeHashCode(IndentingWriter p, SOAPEnumerationType type) throws IOException {
        JavaEnumerationType javaEnum = (JavaEnumerationType)type.getJavaType();
        String baseTypeStr = javaEnum.getBaseType().getName();
        p.plnI("public int hashCode() {");
        if(SimpleToBoxedUtil.isPrimitive(baseTypeStr)) {
            String boxedExp = SimpleToBoxedUtil.getBoxedExpressionOfType("value", baseTypeStr);
            p.pln("return " + boxedExp + ".toString().hashCode();");
        } else {
            p.pln("return value.hashCode();");
        }
        p.pOln("}");
    }
}
