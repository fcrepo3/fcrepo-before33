// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPFaultSerializerGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriter;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriterFactory;
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
import com.sun.xml.rpc.processor.util.StringUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;
import sun.tools.java.ClassFile;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            GeneratorBase, GeneratorException, Names, GeneratorUtil, 
//            SOAPEncoding, FaultExceptionBuilderGenerator, SimpleToBoxedUtil

public class SOAPFaultSerializerGenerator extends GeneratorBase {

    private Port port;

    public SOAPFaultSerializerGenerator() {
    }

    public GeneratorBase getGenerator(Model model, Configuration config, Properties properties) {
        return new SOAPFaultSerializerGenerator(model, config, properties);
    }

    private SOAPFaultSerializerGenerator(Model model, Configuration config, Properties properties) {
        super(model, config, properties);
    }

    protected void preVisitPort(Port port) throws Exception {
        this.port = port;
    }

    protected void postVisitPort(Port port) throws Exception {
        this.port = null;
    }

    protected void postVisitOperation(Operation operation) throws Exception {
        if(needsFaultSerializer(operation))
            generateFaultSerializer(operation);
    }

    protected void visitFault(Fault fault) throws Exception {
        AbstractType type = fault.getBlock().getType();
        if(type.isSOAPType())
            ((SOAPType)type).accept(this);
    }

    private boolean needsFaultSerializer(Operation operation) {
        Iterator faults = operation.getFaults();
        Fault fault;
        boolean needsFaultSerializer;
        for(needsFaultSerializer = false; !needsFaultSerializer && faults.hasNext(); needsFaultSerializer = fault.getBlock().getType().isSOAPType())
            fault = (Fault)faults.next();

        return needsFaultSerializer;
    }

    private void generateFaultSerializer(Operation operation) {
        log("generating FaultHandler for: " + operation.getUniqueName());
        try {
            String className = Names.faultSerializerClassName(port, operation);
            File classFile = Names.sourceFileForClass(className, className, super.sourceDir, super.env);
            super.env.addGeneratedFile(classFile);
            IndentingWriter out = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(classFile)));
            GeneratorBase.writePackage(out, className);
            out.pln();
            writeImports(out);
            out.pln();
            writeClassDecl(out, className);
            writeMembers(out, operation);
            out.pln();
            writeClassConstructor(out, className);
            out.pln();
            writeInitialize(out, operation);
            out.pln();
            writeDeserializeDetail(out, operation);
            out.pln();
            writeSerializeDetail(out, operation);
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
        p.pln("import com.sun.xml.rpc.encoding.*;");
        p.pln("import com.sun.xml.rpc.encoding.soap.SOAPConstants;");
        p.pln("import com.sun.xml.rpc.soap.message.SOAPFaultInfo;");
        p.pln("import com.sun.xml.rpc.streaming.*;");
        p.pln("import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;");
        p.pln("import javax.xml.rpc.namespace.QName;");
    }

    private void writeClassDecl(IndentingWriter p, String className) throws IOException {
        p.plnI("public final class " + Names.mangleClass(className) + " extends SOAPFaultInfoSerializer {");
    }

    private void writeMembers(IndentingWriter p, Operation operation) throws IOException, GeneratorException {
        java.util.Set processedTypes = new TreeSet();
        Iterator faults;
        for(faults = operation.getFaults(); faults.hasNext();) {
            Fault fault = (Fault)faults.next();
            if(fault.getBlock().getType().isSOAPType()) {
                GeneratorUtil.writeQNameDeclaration(p, fault.getBlock().getName());
                SOAPEncoding.writeStaticSerializer(p, (SOAPType)fault.getBlock().getType(), processedTypes, super.writerFactory);
            }
        }

        faults = operation.getFaults();
        int i = 0;
        while(faults.hasNext())  {
            Fault fault = (Fault)faults.next();
            if(FaultExceptionBuilderGenerator.needsBuilder(fault)) {
                if(fault.getBlock().getType().isSOAPType()) {
                    JavaException javaException = fault.getJavaException();
                    p.p("private static final int ");
                    p.pln(Names.memberName(javaException.getPropertyName().toUpperCase() + "_INDEX") + " = " + i + ";");
                }
                i++;
            }
        }
    }

    private void writeClassConstructor(IndentingWriter p, String className) throws IOException {
        p.plnI("public " + Names.mangleClass(className) + "(boolean encodeType, " + "boolean isNullable) {");
        p.pln("super(encodeType, isNullable);");
        p.pOln("}");
    }

    private void writeInitialize(IndentingWriter p, Operation operation) throws IOException {
        Iterator faults = operation.getFaults();
        java.util.Set processedTypes = new TreeSet();
        p.plnI("public void initialize(InternalTypeMappingRegistry registry) throws Exception {");
        p.pln("super.initialize(registry);");
        while(faults.hasNext())  {
            Fault fault = (Fault)faults.next();
            AbstractType type = fault.getBlock().getType();
            if(type.isSOAPType()) {
                SerializerWriter writer = super.writerFactory.createWriter(type);
                writer.initializeSerializer(p, Names.getTypeQName(type.getName()), "registry");
            }
        }
        p.pOln("}");
    }

    private void writeDeserializeDetail(IndentingWriter p, Operation operation) throws IOException {
        Iterator faults = operation.getFaults();
        java.util.Set processedTypes = new TreeSet();
        String detailNames = "";
        p.plnI("protected Object deserializeDetail(SOAPDeserializationState state, XMLReader reader,");
        p.pln("SOAPDeserializationContext context, SOAPFaultInfo instance) throws Exception {");
        p.pln("boolean isComplete = true;");
        p.pln("QName elementName;");
        p.pln("QName elementType = null;");
        p.pln("Object detail = null;");
        p.pln("Object obj = null;");
        p.pln();
        p.pln("reader.nextElementContent();");
        p.pln("XMLReaderUtil.verifyReaderState(reader, XMLReader.START);");
        p.pln("elementName = reader.getName();");
        p.pln("elementType = getType(reader);");
        Fault fault;
        for(; faults.hasNext(); writeFaultDeserializer(p, fault, operation, "reader", faults.hasNext()))
            fault = (Fault)faults.next();

        p.plnI("throw new DeserializationException(\"xsd.unexpectedElementName\", ");
        p.pln("new Object[] {\"" + detailNames + "\", elementName.toString()});");
        p.pO();
        p.pOln("}");
    }

    private void writeSerializeDetail(IndentingWriter p, Operation operation) throws IOException {
        Iterator faults = operation.getFaults();
        java.util.Set processedTypes = new TreeSet();
        String detailNames = "";
        p.plnI("protected void serializeDetail(Object detail, XMLWriter writer, SOAPSerializationContext context)");
        p.pln("throws Exception {");
        p.plnI("if (detail == null) {");
        p.pln("throw new SerializationException(\"soap.unexpectedNull\");");
        p.pOln("}");
        p.pln("writer.startElement(DETAIL_QNAME);");
        p.pln();
        p.pln("boolean pushedEncodingStyle = false;");
        p.plnI("if (encodingStyle != null) {");
        p.pln("context.pushEncodingStyle(encodingStyle, writer);");
        p.pOln("}");
        Fault fault;
        for(; faults.hasNext(); writeFaultSerializer(p, fault, "writer", faults.hasNext()))
            fault = (Fault)faults.next();

        p.pln("writer.endElement();");
        p.plnI("if (pushedEncodingStyle) {");
        p.pln("context.popEncodingStyle();");
        p.pOln("}");
        p.pOln("}");
    }

    private void writeFaultDeserializer(IndentingWriter p, Fault fault, Operation operation, String reader, boolean hasNext) throws IOException {
        Block block = fault.getBlock();
        String memberConstName = "0";
        String memberQName = Names.getQNameName(block.getName());
        SOAPType type = (SOAPType)block.getType();
        SerializerWriter writer = super.writerFactory.createWriter(type);
        String serializer = writer.deserializerMemberName();
        boolean referenceable = type.isReferenceable();
        p.plnI("if (elementName.equals(" + memberQName + ") && ");
        p.pln("(elementType == null || (elementType != null && ");
        p.pln("elementType.equals(" + serializer + ".getXmlType()))) ) {");
        p.pln("obj = " + serializer + ".deserialize(" + memberQName + ", " + reader + ", context);");
        if(referenceable) {
            p.plnI("if (obj instanceof SOAPDeserializationState) {");
            String builderName = Names.faultBuilderClassName(port, operation);
            p.plnI("SOAPInstanceBuilder builder =");
            p.pln("new " + builderName + "();");
            p.pO();
            JavaException javaException = fault.getJavaException();
            String index = Names.memberName(javaException.getPropertyName().toUpperCase() + "_INDEX");
            p.pln("state = registerWithMemberState(instance, state, obj, " + index + ", builder);");
            p.pln("isComplete = false;");
            p.pOlnI("} else {");
            p.pln("detail = new " + Names.customExceptionClassName(fault) + "((" + type.getJavaType().getName() + ")obj);");
            p.pOln("}");
            p.pln("reader.nextElementContent();");
            p.pln("XMLReaderUtil.verifyReaderState(reader, XMLReader.END);");
            p.pln("return (isComplete ? (Object)detail : (Object)obj);");
        } else {
            String valueStr = null;
            String javaName = type.getJavaType().getName();
            if(SimpleToBoxedUtil.isPrimitive(javaName)) {
                String boxName = SimpleToBoxedUtil.getBoxedClassName(javaName);
                valueStr = SimpleToBoxedUtil.getUnboxedExpressionOfType("(" + boxName + ")obj", javaName);
            } else {
                valueStr = "(" + javaName + ")obj";
            }
            p.pln("detail = new " + Names.customExceptionClassName(fault) + "(" + valueStr + ");");
            p.pln("reader.nextElementContent();");
            p.pln("XMLReaderUtil.verifyReaderState(reader, XMLReader.END);");
            p.pln("return detail;");
        }
        if(hasNext)
            p.pO("} else ");
        else
            p.pOln("}");
    }

    private void writeFaultSerializer(IndentingWriter p, Fault fault, String writer, boolean hasNext) throws IOException {
        Block block = fault.getBlock();
        String memberQName = Names.getQNameName(block.getName());
        SOAPType type = (SOAPType)block.getType();
        String faultExceptionName = Names.customExceptionClassName(fault);
        String getter = "get" + StringUtils.capitalize(fault.getJavaException().getPropertyName()) + "()";
        p.plnI("if (detail instanceof " + faultExceptionName + ") {");
        SerializerWriter sWriter = super.writerFactory.createWriter(type);
        String serializer = sWriter.deserializerMemberName();
        p.pln(faultExceptionName + " fault = (" + faultExceptionName + ")detail;");
        p.pln(serializer + ".serialize(fault." + getter + ", " + memberQName + ", null, " + writer + ", context);");
        if(hasNext)
            p.pO("} else ");
        else
            p.pOln("}");
    }
}
