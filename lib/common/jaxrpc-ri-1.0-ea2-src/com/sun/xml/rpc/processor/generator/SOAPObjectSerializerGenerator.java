// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy

// Source File Name:   SOAPObjectSerializerGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriter;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriterFactory;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.soap.RPCResponseStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPAnyType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPEnumerationType;
import com.sun.xml.rpc.processor.model.soap.SOAPOrderedStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPSimpleType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.model.soap.SOAPUnorderedStructureType;
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
import java.util.TreeSet;
import javax.xml.rpc.namespace.QName;
import sun.tools.java.ClassFile;
import sun.tools.java.Environment;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            GeneratorBase, Names, GeneratorUtil, SOAPEncoding,
//            SOAPObjectBuilderGenerator, SimpleToBoxedUtil

public class SOAPObjectSerializerGenerator extends GeneratorBase {

    private Set visitedTypes;

    public SOAPObjectSerializerGenerator() {
    }

    public GeneratorBase getGenerator(Model model, Configuration config, Properties properties) {
        return new SOAPObjectSerializerGenerator(model, config, properties);
    }

    private SOAPObjectSerializerGenerator(Model model, Configuration config, Properties properties) {
        super(model, config, properties);
    }

    protected void preVisitModel(Model model) throws Exception {
        visitedTypes = new HashSet();
    }

    protected void postVisitModel(Model model) throws Exception {
        visitedTypes = null;
    }

    protected void preVisitFault(Fault fault) throws Exception {
        if(fault.getBlock().getType().isSOAPType())
            ((SOAPType)fault.getBlock().getType()).accept(this);
    }

    protected void preVisitSOAPSimpleType(SOAPSimpleType type) throws Exception {
        if(haveVisited(type)) {
            return;
        } else {
            typeVisited(type);
            return;
        }
    }

    protected void preVisitSOAPAnyType(SOAPAnyType type) throws Exception {
        if(haveVisited(type)) {
            return;
        } else {
            typeVisited(type);
            return;
        }
    }

    protected void preVisitSOAPEnumerationType(SOAPEnumerationType type) throws Exception {
        if(haveVisited(type)) {
            return;
        } else {
            typeVisited(type);
            return;
        }
    }

    protected void preVisitSOAPArrayType(SOAPArrayType type) throws Exception {
        if(haveVisited(type)) {
            return;
        } else {
            typeVisited(type);
            return;
        }
    }

    public void visit(SOAPStructureType type) throws Exception {
        if(haveVisited(type))
            return;
        typeVisited(type);
        SOAPStructureMember member;
        for(Iterator members = type.getMembers(); members.hasNext(); member.getType().accept(this))
            member = (SOAPStructureMember)members.next();

        try {
            generateObjectSerializerForType(type);
        }
        catch(IOException ioexception) {
            fail("generator.cant.write", type.getName().getLocalPart());
        }
    }

    private boolean haveVisited(SOAPType type) {
        return visitedTypes.contains(type);
    }

    private void typeVisited(SOAPType type) {
        visitedTypes.add(type);
    }

    private void generateObjectSerializerForType(SOAPStructureType type) throws IOException {
        writeObjectSerializerForType(type);
    }

    private void writeObjectSerializerForType(SOAPStructureType type) throws IOException {
        JavaType javaType = type.getJavaType();
        if(javaType == null)
            fail("generator.invalid.model.state.no.javatype", type.getName().getLocalPart());
        String className = Names.typeObjectSerializerClassName(type);
        File classFile = Names.sourceFileForClass(className, className, super.sourceDir, super.env);
        try {
            super.env.addGeneratedFile(classFile);
            IndentingWriter out = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(classFile)));
            writeObjectSerializerCode(out, type);
            out.close();
            log("wrote file: " + classFile.getPath());
            super.env.parseFile(new ClassFile(classFile));
        }
        catch(IOException ioexception) {
            super.env.error(0L, "cant.write", classFile.toString());
        }
    }

    private void writeObjectSerializerCode(IndentingWriter p, SOAPStructureType type) throws IOException {
        log("writing  serializer/deserializer for: " + type.getName().getLocalPart());
        String className = Names.typeObjectSerializerClassName(type);
        GeneratorBase.writePackage(p, className);
        writeImports(p);
        p.pln();
        writeClassDecl(p, className);
        writeMembers(p, type);
        p.pln();
        writeConstructor(p, className);
        p.pln();
        writeInitialize(p, type);
        p.pln();
        writeDoDeserializeMethod(p, type);
        p.pln();
        writeDoSerializeInstanceMethod(p, type);
        if(type instanceof RPCResponseStructureType)
            writeVerifyNameOverrideMethod(p, type);
        p.pOln("}");
    }

    private void writeImports(IndentingWriter p) throws IOException {
        p.pln("import com.sun.xml.rpc.encoding.*;");
        p.pln("import com.sun.xml.rpc.encoding.soap.SOAPConstants;");
        p.pln("import com.sun.xml.rpc.streaming.*;");
        p.pln("import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;");
        p.pln("import javax.xml.rpc.namespace.QName;");
    }

    private void writeClassDecl(IndentingWriter p, String className) throws IOException {
        p.plnI("public final class " + Names.mangleClass(className) + " extends ObjectSerializerBase implements Initializable {");
    }

    private void writeMembers(IndentingWriter p, SOAPStructureType type) throws IOException {
        JavaStructureType javaStructure = (JavaStructureType)type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        Set processedTypes = new TreeSet();
        SOAPStructureMember member;
        for(; iterator.hasNext(); SOAPEncoding.writeStaticSerializer(p, member.getType(), processedTypes, super.writerFactory)) {
            JavaStructureMember javaMember = (JavaStructureMember)iterator.next();
            member = (SOAPStructureMember)javaMember.getOwner();
            GeneratorUtil.writeQNameDeclaration(p, member.getName());
        }

        iterator = javaStructure.getMembers();
        for(int i = 0; iterator.hasNext(); i++) {
            JavaStructureMember javaMember = (JavaStructureMember)iterator.next();
            SOAPStructureMember member2 = (SOAPStructureMember)javaMember.getOwner();
            p.p("private static final int ");
            p.pln(Names.memberName(member2.getName().getLocalPart().toUpperCase() + "_INDEX") + " = " + i + ";");
        }

    }

    private void writeConstructor(IndentingWriter p, String className) throws IOException {
        p.plnI("public " + Names.mangleClass(className) + "(QName type, boolean encodeType, " + "boolean isNullable, String encodingStyle) {");
        p.pln("super(type, encodeType, isNullable, encodingStyle);");
        p.pOln("}");
    }

    private void writeInitialize(IndentingWriter p, SOAPStructureType type) throws IOException {
        JavaStructureType javaStructure = (JavaStructureType)type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        Set processedTypes = new HashSet();
        p.plnI("public void initialize(InternalTypeMappingRegistry registry) throws Exception {");
        while(iterator.hasNext())  {
            JavaStructureMember javaMember = (JavaStructureMember)iterator.next();
            SOAPStructureMember member = (SOAPStructureMember)javaMember.getOwner();
            AbstractType memType = member.getType();
            if(!processedTypes.contains(memType.getName() + ";" + memType.getJavaType().getName())) {
                SerializerWriter writer = super.writerFactory.createWriter(memType);
                writer.initializeSerializer(p, Names.getTypeQName(memType.getName()), "registry");
                processedTypes.add(member.getType().getName() + ";" + memType.getJavaType().getName());
            }
        }
        p.pOln("}");
    }

    private void writeDoDeserializeMethod(IndentingWriter p, SOAPStructureType type) throws IOException {
        String unqualifiedClassName = Names.stripQualifier(type.getJavaType().getName());
        p.plnI("public Object doDeserialize(SOAPDeserializationState state, XMLReader reader,");
        p.pln("SOAPDeserializationContext context) throws Exception {");
        p.pln(unqualifiedClassName + " instance = new " + unqualifiedClassName + "();");
        if(SOAPObjectBuilderGenerator.needBuilder(type))
            p.pln(Names.stripQualifier(Names.typeObjectBuilderClassName(type)) + " builder = null;");
        p.pln("Object member;");
        p.pln("boolean isComplete = true;");
        p.pln("QName elementName;");
        p.pln();
        p.pln("reader.nextElementContent();");
        if(type.getMembersCount() > 0) {
            if(type instanceof SOAPOrderedStructureType)
                writeDeserializeElements(p, (SOAPOrderedStructureType)type, "reader");
            else
            if(type instanceof RPCResponseStructureType)
                writeDeserializeElements(p, (RPCResponseStructureType)type, "reader");
            else
            if(type instanceof SOAPUnorderedStructureType)
                writeDeserializeElements(p, (SOAPUnorderedStructureType)type, "reader");
            p.pln();
        }
        p.pln("XMLReaderUtil.verifyReaderState(reader, XMLReader.END);");
        p.pln("return (isComplete ? (Object)instance : (Object)state);");
        p.pOln("}");
    }

    private void writeDeserializeElements(IndentingWriter p, SOAPOrderedStructureType type, String reader) throws IOException {
        JavaStructureType javaStructure = (JavaStructureType)type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        for(int i = 0; iterator.hasNext(); i++) {
            JavaStructureMember javaMember = (JavaStructureMember)iterator.next();
            p.pln("elementName = " + reader + ".getName();");
            writeMemberDeserializer(p, type, javaMember, reader, true, true, false, false);
        }

    }

    private void writeDeserializeElements(IndentingWriter p, RPCResponseStructureType type, String reader) throws IOException {
        JavaStructureType javaStructure = (JavaStructureType)type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        int memberCnt = javaStructure.getMembersCount();
        for(int i = 0; iterator.hasNext(); i++) {
            JavaStructureMember javaMember = (JavaStructureMember)iterator.next();
            writeMemberDeserializer(p, type, javaMember, reader, i > 0, i > 0, memberCnt > 2 && i > 0, !iterator.hasNext());
            if(i == 0) {
                if(memberCnt > 2)
                    p.plnI("for (int i=0; i<" + (memberCnt - 1) + "; i++) {");
                p.pln("elementName = " + reader + ".getName();");
            }
        }

        if(memberCnt > 2)
            p.pOln("}");
    }

    private void writeDeserializeElements(IndentingWriter p, SOAPUnorderedStructureType type, String reader) throws IOException {
        JavaStructureType javaStructure = (JavaStructureType)type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        int memberCnt = javaStructure.getMembersCount();
        if(memberCnt > 1) {
            p.plnI("for (int i=0; i<" + memberCnt + "; i++) {");
            p.pln("elementName = " + reader + ".getName();");
            p.plnI("if (" + reader + ".getState() == XMLReader.END) {");
            p.pln("break;");
            p.pOln("}");
        } else {
            p.pln("elementName = " + reader + ".getName();");
        }
        for(int i = 0; iterator.hasNext(); i++) {
            JavaStructureMember javaMember = (JavaStructureMember)iterator.next();
            writeMemberDeserializer(p, type, javaMember, reader, true, true, memberCnt > 1, !iterator.hasNext());
        }

        if(memberCnt > 1)
            p.pOln("}");
    }

    private void writeMemberDeserializer(IndentingWriter p, SOAPStructureType type, JavaStructureMember javaMember, String reader, boolean preCheckElementName, boolean checkElementName, boolean unOrdered,
            boolean writeThrow) throws IOException {
        SOAPStructureMember member = (SOAPStructureMember)javaMember.getOwner();
        String memberConstName = Names.memberName(member.getName().getLocalPart().toUpperCase());
        String memberQName = Names.getQNameName(member.getName());
        if(!checkElementName)
            memberQName = "null";
        if(!unOrdered)
            p.plnI("if (" + reader + ".getState() == XMLReader.START) {");
        if(preCheckElementName && checkElementName)
            p.plnI("if (elementName.equals(" + memberQName + ")) {");
        SerializerWriter writer = super.writerFactory.createWriter(member.getType());
        String serializer = writer.deserializerMemberName();
        boolean referenceable = member.getType().isReferenceable();
        p.pln("member = " + serializer + ".deserialize(" + memberQName + ", " + reader + ", context);");
        if(referenceable) {
            p.plnI("if (member instanceof SOAPDeserializationState) {");
            p.plnI("if (builder == null) {");
            p.pln("builder = new " + Names.stripQualifier(Names.typeObjectBuilderClassName(type)) + "();");
            p.pOln("}");
            p.pln("state = registerWithMemberState(instance, state, member, " + memberConstName + "_INDEX, builder);");
            p.pln("isComplete = false;");
            p.pOlnI("} else {");
            if(javaMember.isPublic())
                p.pln("instance." + javaMember.getName() + " = (" + javaMember.getType().getName() + ")member;");
            else
                p.pln("instance." + javaMember.getWriteMethod() + "((" + javaMember.getType().getName() + ")member);");
            p.pOln("}");
        } else {
            String valueStr = null;
            String javaName = javaMember.getType().getName();
            if(SimpleToBoxedUtil.isPrimitive(javaName)) {
                String boxName = SimpleToBoxedUtil.getBoxedClassName(javaName);
                valueStr = SimpleToBoxedUtil.getUnboxedExpressionOfType("(" + boxName + ")member", javaName);
            } else {
                valueStr = "(" + javaName + ")member";
            }
            if(javaMember.isPublic())
                p.pln("instance." + javaMember.getName() + " = " + valueStr + ";");
            else
                p.pln("instance." + javaMember.getWriteMethod() + "(" + valueStr + ");");
        }
        p.pln(reader + ".nextElementContent();");
        if(unOrdered)
            p.pln("continue;");
        if(preCheckElementName && checkElementName) {
            p.pO("}");
            if(writeThrow) {
                p.plnI(" else {");
                p.pln("throw new DeserializationException(\"soap.unexpectedElementName\", " + reader + ".getName().toString());");
                p.pOln("}");
            } else {
                p.pln();
            }
        }
        if(!unOrdered)
            p.pOln("}");
    }

    private void writeDoSerializeInstanceMethod(IndentingWriter p, SOAPStructureType type) throws IOException {
        p.plnI("public void doSerializeInstance(Object obj, XMLWriter writer, SOAPSerializationContext context) throws Exception {");
        p.pln(type.getJavaType().getName() + " instance = (" + type.getJavaType().getName() + ")obj;");
        p.pln();
        JavaStructureType javaStructure = (JavaStructureType)type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        for(int i = 0; iterator.hasNext(); i++) {
            JavaStructureMember javaMember = (JavaStructureMember)iterator.next();
            SOAPStructureMember member = (SOAPStructureMember)javaMember.getOwner();
            String memberConstName = member.getName().getLocalPart().toUpperCase();
            String memberQName = Names.getQNameName(member.getName());
            SerializerWriter writer = super.writerFactory.createWriter(member.getType());
            String serializer = writer.serializerMemberName();
            String valueStr = null;
            String javaName = javaMember.getType().getName();
            if(javaMember.isPublic()) {
                valueStr = "instance." + javaMember.getName();
            } else {
                String methName = javaMember.getReadMethod();
                valueStr = "instance." + methName + "()";
            }
            if(SimpleToBoxedUtil.isPrimitive(javaName))
                valueStr = SimpleToBoxedUtil.getBoxedExpressionOfType(valueStr, javaName);
            p.pln(serializer + ".serialize(" + valueStr + ", " + memberQName + ", null, writer, context);");
        }

        p.pOln("}");
    }

    private void writeVerifyNameOverrideMethod(IndentingWriter p, SOAPStructureType type) throws IOException {
        p.plnI("protected void verifyName(XMLReader reader, QName expectedName) throws Exception {");
        p.pOln("}");
    }
}
