// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   HolderGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Port;
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
//            GeneratorBase, Names

public class HolderGenerator extends GeneratorBase {

    private Set types;
    private Port port;

    public HolderGenerator() {
    }

    public GeneratorBase getGenerator(Model model, Configuration config, Properties properties) {
        return new HolderGenerator(model, config, properties);
    }

    private HolderGenerator(Model model, Configuration config, Properties properties) {
        super(model, config, properties);
    }

    protected void preVisitModel(Model model) throws Exception {
        types = new HashSet();
    }

    protected void postVisitModel(Model model) throws Exception {
        types = null;
    }

    protected void preVisitPort(Port port) throws Exception {
        this.port = port;
    }

    protected void postVisitPort(Port port) throws Exception {
        port = null;
    }

    public void visit(SOAPCustomType type) throws Exception {
        if(isRegistered(type))
            return;
        registerType(type);
        if(type.getJavaType().isHolder())
            generateHolder(type);
    }

    public void visit(SOAPSimpleType type) throws Exception {
        if(isRegistered(type))
            return;
        registerType(type);
        if(type.getJavaType().isHolder())
            generateHolder(type);
    }

    public void visit(SOAPAnyType type) throws Exception {
        if(isRegistered(type))
            return;
        registerType(type);
        if(type.getJavaType().isHolder())
            generateHolder(type);
    }

    public void visit(SOAPEnumerationType type) throws Exception {
        if(isRegistered(type))
            return;
        registerType(type);
        if(type.getJavaType().isHolder())
            generateHolder(type);
    }

    protected void visitSOAPArrayType(SOAPArrayType type) throws Exception {
        if(isRegistered(type))
            return;
        registerType(type);
        if(type.getJavaType().isHolder())
            generateHolder(type);
        super.visitSOAPArrayType(type);
    }

    protected void visitSOAPStructureType(SOAPStructureType type) throws Exception {
        if(isRegistered(type))
            return;
        registerType(type);
        if(type.getJavaType().isHolder())
            generateHolder(type);
        super.visitSOAPStructureType(type);
    }

    private boolean isRegistered(SOAPType type) {
        return types.contains(type);
    }

    private void registerType(SOAPType type) {
        types.add(type);
    }

    private void generateHolder(SOAPType type) {
        log("generating Holder for: " + Names.holderClassName(port, type));
        try {
            String className = Names.holderClassName(port, type);
            if(className.startsWith("javax.xml.rpc.holders."))
                return;
            File classFile = Names.sourceFileForClass(className, className, super.sourceDir, super.env);
            super.env.addGeneratedFile(classFile);
            IndentingWriter out = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(classFile)));
            GeneratorBase.writePackage(out, className);
            out.pln();
            writeImports(out);
            out.pln();
            writeClassDecl(out, className);
            writeMembers(out, type);
            out.pln();
            writeClassConstructor(out, className, type);
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
        p.pln("import javax.xml.rpc.holders.Holder;");
    }

    private void writeClassDecl(IndentingWriter p, String className) throws IOException {
        p.plnI("public final class " + Names.mangleClass(className) + " implements Holder {");
    }

    private void writeMembers(IndentingWriter p, SOAPType type) throws IOException {
        p.pln("public " + type.getJavaType().getName() + " value;");
    }

    private void writeClassConstructor(IndentingWriter p, String className, SOAPType type) throws IOException {
        p.pln("public " + Names.mangleClass(className) + "() {");
        p.pln("}");
        p.pln();
        p.plnI("public " + Names.mangleClass(className) + "(" + type.getJavaType().getName() + " " + Names.getTypeMemberName(type) + ") {");
        p.pln("this.value = " + Names.getTypeMemberName(type) + ";");
        p.pOln("}");
    }
}
