// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy

// Source File Name:   LiteralObjectSerializerGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriter;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriterFactory;
import com.sun.xml.rpc.processor.generator.writer.SimpleTypeSerializerWriter;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.LiteralAllType;
import com.sun.xml.rpc.processor.model.literal.LiteralAttributeMember;
import com.sun.xml.rpc.processor.model.literal.LiteralAttributeOwningType;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralFragmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralSimpleType;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
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
//            GeneratorBase, Names, GeneratorUtil, LiteralEncoding,
//            SimpleToBoxedUtil

public class LiteralObjectSerializerGenerator extends GeneratorBase {

    private Set visitedTypes;

    public LiteralObjectSerializerGenerator() {
    }

    public GeneratorBase getGenerator(Model model, Configuration config, Properties properties) {
        return new LiteralObjectSerializerGenerator(model, config, properties);
    }

    private LiteralObjectSerializerGenerator(Model model, Configuration config, Properties properties) {
        super(model, config, properties);
    }

    protected void preVisitModel(Model model) throws Exception {
        visitedTypes = new HashSet();
    }

    protected void postVisitModel(Model model) throws Exception {
        visitedTypes = null;
    }

    protected void preVisitLiteralSimpleType(LiteralSimpleType type) throws Exception {
        if(haveVisited(type)) {
            return;
        } else {
            typeVisited(type);
            return;
        }
    }

    protected void preVisitLiteralSequenceType(LiteralSequenceType type) throws Exception {
        if(haveVisited(type))
            return;
        typeVisited(type);
        LiteralAttributeMember attribute;
        for(Iterator attributes = type.getAttributeMembers(); attributes.hasNext(); attribute.getType().accept(this))
            attribute = (LiteralAttributeMember)attributes.next();

        LiteralElementMember element;
        for(Iterator elements = type.getElementMembers(); elements.hasNext(); element.getType().accept(this))
            element = (LiteralElementMember)elements.next();

        try {
            generateObjectSerializerForType(type);
        }
        catch(IOException ioexception) {
            fail("generator.cant.write", type.getName().getLocalPart());
        }
    }

    protected void preVisitLiteralAllType(LiteralAllType type) throws Exception {
        if(haveVisited(type))
            return;
        typeVisited(type);
        LiteralAttributeMember attribute;
        for(Iterator attributes = type.getAttributeMembers(); attributes.hasNext(); attribute.getType().accept(this))
            attribute = (LiteralAttributeMember)attributes.next();

        LiteralElementMember element;
        for(Iterator elements = type.getElementMembers(); elements.hasNext(); element.getType().accept(this))
            element = (LiteralElementMember)elements.next();

        try {
            generateObjectSerializerForType(type);
        }
        catch(IOException ioexception) {
            fail("generator.cant.write", type.getName().getLocalPart());
        }
    }

    protected void preVisitLiteralFragmentType(LiteralFragmentType type) throws Exception {
        if(haveVisited(type)) {
            return;
        } else {
            typeVisited(type);
            return;
        }
    }

    private boolean haveVisited(AbstractType type) {
        return visitedTypes.contains(type);
    }

    private void typeVisited(AbstractType type) {
        visitedTypes.add(type);
    }

    private void generateObjectSerializerForType(LiteralStructuredType type) throws IOException {
        writeObjectSerializerForType(type);
    }

    private void writeObjectSerializerForType(LiteralStructuredType type) throws IOException {
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

    private void writeObjectSerializerCode(IndentingWriter p, LiteralStructuredType type) throws IOException {
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
        writeDoSerializeAttributesMethod(p, type);
        writeDoSerializeMethod(p, type);
        p.pOln("}");
    }

    private void writeImports(IndentingWriter p) throws IOException {
        p.pln("import com.sun.xml.rpc.encoding.*;");
        p.pln("import com.sun.xml.rpc.encoding.literal.*;");
        p.pln("import com.sun.xml.rpc.encoding.simpletype.*;");
        p.pln("import com.sun.xml.rpc.encoding.soap.SOAPConstants;");
        p.pln("import com.sun.xml.rpc.streaming.*;");
        p.pln("import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;");
        p.pln("import javax.xml.rpc.namespace.QName;");
        p.pln("import java.util.List;");
        p.pln("import java.util.ArrayList;");
    }

    private void writeClassDecl(IndentingWriter p, String className) throws IOException {
        p.plnI("public final class " + Names.mangleClass(className) + " extends LiteralObjectSerializerBase implements Initializable {");
    }

    private void writeMembers(IndentingWriter p, LiteralStructuredType type) throws IOException {
        JavaStructureType javaStructure = (JavaStructureType)type.getJavaType();
        Set processedTypes = new TreeSet();
        LiteralAttributeMember member;
        for(Iterator iter = type.getAttributeMembers(); iter.hasNext(); GeneratorUtil.writeQNameDeclaration(p, member.getName())) {
            member = (LiteralAttributeMember)iter.next();
            JavaStructureMember javaMember = member.getJavaStructureMember();
        }

        LiteralElementMember member2;
        for(Iterator iter = type.getElementMembers(); iter.hasNext(); LiteralEncoding.writeStaticSerializer(p, member2.getType(), processedTypes, super.writerFactory)) {
            member2 = (LiteralElementMember)iter.next();
            JavaStructureMember javaMember = member2.getJavaStructureMember();
            GeneratorUtil.writeQNameDeclaration(p, member2.getName());
        }

    }

    private void writeConstructor(IndentingWriter p, String className) throws IOException {
        p.plnI("public " + Names.mangleClass(className) + "(QName type, String encodingStyle) {");
        p.pln("super(type, true, encodingStyle);");
        p.pOln("}");
    }

    private void writeInitialize(IndentingWriter p, LiteralStructuredType type) throws IOException {
        JavaStructureType javaStructure = (JavaStructureType)type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        Set processedTypes = new HashSet();
        p.plnI("public void initialize(InternalTypeMappingRegistry registry) throws Exception {");
        for(Iterator iter = type.getElementMembers(); iter.hasNext();) {
            LiteralElementMember member = (LiteralElementMember)iter.next();
            JavaStructureMember javaMember = member.getJavaStructureMember();
            AbstractType memType = member.getType();
            if(!processedTypes.contains(memType.getName() + ";" + memType.getJavaType().getName())) {
                SerializerWriter writer = super.writerFactory.createWriter(memType);
                writer.initializeSerializer(p, Names.getTypeQName(memType.getName()), "registry");
                processedTypes.add(member.getType().getName() + ";" + memType.getJavaType().getName());
            }
        }

        p.pOln("}");
    }

    private void writeDoDeserializeMethod(IndentingWriter p, LiteralStructuredType type) throws IOException {
        String unqualifiedClassName = Names.stripQualifier(type.getJavaType().getName());
        p.plnI("public Object doDeserialize(XMLReader reader,");
        p.pln("SOAPDeserializationContext context) throws Exception {");
        p.pln(unqualifiedClassName + " instance = new " + unqualifiedClassName + "();");
        p.pln("Object member;");
        p.pln("QName elementName;");
        p.pln("List values;");
        p.pln("Object value;");
        p.pln();
        if(type.getAttributeMembersCount() > 0) {
            writeDeserializeAttributes(p, type, "reader");
            p.pln();
        }
        p.pln("reader.nextElementContent();");
        if(type.getElementMembersCount() > 0) {
            writeDeserializeElements(p, type, "reader");
            p.pln();
        }
        p.pln("XMLReaderUtil.verifyReaderState(reader, XMLReader.END);");
        p.pln("return (Object)instance;");
        p.pOln("}");
    }

    private void writeDeserializeAttributes(IndentingWriter p, LiteralStructuredType type, String reader) throws IOException {
        p.pln("Attributes attributes = reader.getAttributes();");
        p.pln("String attribute = null;");
        for(Iterator iterator = type.getAttributeMembers(); iterator.hasNext();) {
            LiteralAttributeMember member = (LiteralAttributeMember)iterator.next();
            JavaStructureMember javaMember = member.getJavaStructureMember();
            String memberConstName = member.getName().getLocalPart().toUpperCase();
            String memberQName = Names.getQNameName(member.getName());
            p.pln("attribute = attributes.getValue(" + memberQName + ");");
            p.plnI("if (attribute != null) {");
            String encoder = SimpleTypeSerializerWriter.getTypeEncoder(member.getType());
            p.pln("member = " + encoder + ".getInstance().stringToObject(attribute, reader);");
            String javaName = javaMember.getType().getName();
            String valueStr;
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
            p.pOln("}");
            if(member.isRequired()) {
                p.plnI("else {");
                p.pln("throw new DeserializationException(\"literal.missinRequiredAttribute\", new Object[] {" + memberQName + "});");
                p.pOln("}");
            }
        }

    }

    private void writeDeserializeElements(IndentingWriter p, LiteralStructuredType type, String reader) throws IOException {
        JavaStructureType javaStructure = (JavaStructureType)type.getJavaType();
        if(type instanceof LiteralSequenceType) {
            for(Iterator iterator = type.getElementMembers(); iterator.hasNext();) {
                LiteralElementMember elementMember = (LiteralElementMember)iterator.next();
                p.pln("elementName = " + reader + ".getName();");
                if(elementMember.isRepeated())
                    writeArrayElementMemberDeserializer(p, type, elementMember, reader, false);
                else
                    writeScalarElementMemberDeserializer(p, type, elementMember, reader, false);
            }

        } else
        if(type.getElementMembersCount() > 0) {
            p.pln("elementName = " + reader + ".getName();");
            p.plnI("while (reader.getState() == XMLReader.START) {");
            Iterator iterator = type.getElementMembers();
            for(boolean gotOne = false; iterator.hasNext(); gotOne = true) {
                if(gotOne)
                    p.p("else ");
                LiteralElementMember elementMember = (LiteralElementMember)iterator.next();
                if(elementMember.isRepeated())
                    writeArrayElementMemberDeserializer(p, type, elementMember, reader, true);
                else
                    writeScalarElementMemberDeserializer(p, type, elementMember, reader, true);
            }

            p.plnI("else {");
            p.pln("throw new DeserializationException(\"literal.unexpectedElementName\", new Object[] { elementName });");
            p.pOln("}");
            p.pOln("}");
        }
    }

    private void writeScalarElementMemberDeserializer(IndentingWriter p, LiteralStructuredType type, LiteralElementMember member, String reader, boolean isAllType) throws IOException {
        JavaStructureMember javaMember = member.getJavaStructureMember();
        String memberConstName = Names.memberName(member.getName().getLocalPart().toUpperCase());
        String memberQName = Names.getQNameName(member.getName());
        if(!isAllType)
            p.plnI("if (" + reader + ".getState() == XMLReader.START) {");
        p.plnI("if (elementName.equals(" + memberQName + ")) {");
        SerializerWriter writer = super.writerFactory.createWriter(member.getType());
        String serializer = writer.deserializerMemberName();
        p.pln("member = " + serializer + ".deserialize(" + memberQName + ", " + reader + ", context);");
        if(!member.isNillable()) {
            p.plnI("if (member == null) {");
            p.pln("throw new DeserializationException(\"literal.unexpectedNull\");");
            p.pOln("}");
        }
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
        p.pln(reader + ".nextElementContent();");
        p.pO("}");
        if(!isAllType && member.isRequired()) {
            p.plnI(" else {");
            p.pln("throw new DeserializationException(\"literal.unexpectedElementName\", new Object[] { " + memberQName + " });");
            p.pOln("}");
        } else {
            p.pln();
        }
        if(!isAllType) {
            p.pOln("}");
            if(member.isRequired()) {
                p.plnI("else {");
                p.pln("throw new DeserializationException(\"literal.expectedElementName\", " + reader + ".getName().toString());");
                p.pOln("}");
            }
        }
    }

    private void writeArrayElementMemberDeserializer(IndentingWriter p, LiteralStructuredType type, LiteralElementMember member, String reader, boolean isAllType) throws IOException {
        JavaStructureMember javaMember = member.getJavaStructureMember();
        String memberConstName = Names.memberName(member.getName().getLocalPart().toUpperCase());
        String memberQName = Names.getQNameName(member.getName());
        p.plnI("if ((" + reader + ".getState() == XMLReader.START) && (elementName.equals(" + memberQName + "))) {");
        p.pln("values = new ArrayList();");
        p.plnI("for(;;) {");
        p.plnI("if ((" + reader + ".getState() == XMLReader.START) && (elementName.equals(" + memberQName + "))) {");
        SerializerWriter writer = super.writerFactory.createWriter(member.getType());
        String serializer = writer.deserializerMemberName();
        p.pln("value = " + serializer + ".deserialize(" + memberQName + ", " + reader + ", context);");
        if(!member.isNillable()) {
            p.plnI("if (value == null) {");
            p.pln("throw new DeserializationException(\"literal.unexpectedNull\");");
            p.pOln("}");
        }
        p.pln("values.add(value);");
        String valueStr = null;
        String javaName = member.getType().getJavaType().getName();
        p.pln(reader + ".nextElementContent();");
        p.pO("}");
        p.plnI(" else {");
        p.pln("break;");
        p.pOln("}");
        p.pOln("}");
        p.pln("member = new " + javaName + "[values.size()];");
        if(SimpleToBoxedUtil.isPrimitive(javaName)) {
            String boxName = SimpleToBoxedUtil.getBoxedClassName(javaName);
            p.plnI("for (int i = 0; i < values.size(); ++i) {");
            p.pln("((" + javaName + "[]) member)[i] = " + SimpleToBoxedUtil.getUnboxedExpressionOfType("(" + boxName + ")(values.get(i))", javaName) + ";");
            p.pOln("}");
        } else {
            p.pln("member = values.toArray((Object[]) member);");
        }
        valueStr = "(" + javaName + "[])member";
        if(javaMember.isPublic())
            p.pln("instance." + javaMember.getName() + " = " + valueStr + ";");
        else
            p.pln("instance." + javaMember.getWriteMethod() + "(" + valueStr + ");");
        p.pOln("}");
        if(member.isRequired()) {
            p.plnI("else {");
            p.pln("throw new DeserializationException(\"literal.expectedElementName\", " + reader + ".getName().toString());");
            p.pOln("}");
        }
    }

    private void writeDoSerializeAttributesMethod(IndentingWriter p, LiteralStructuredType type) throws IOException {
        p.plnI("public void doSerializeAttributes(Object obj, XMLWriter writer, SOAPSerializationContext context) throws Exception {");
        p.pln(type.getJavaType().getName() + " instance = (" + type.getJavaType().getName() + ")obj;");
        p.pln();
        JavaStructureType javaStructure = (JavaStructureType)type.getJavaType();
        String memberQName;
        String valueStr;
        String encoder;
        for(Iterator iterator = type.getAttributeMembers(); iterator.hasNext(); p.pln("writer.writeAttribute(" + memberQName + ", " + encoder + ".getInstance().objectToString(" + valueStr + ", writer));")) {
            LiteralAttributeMember member = (LiteralAttributeMember)iterator.next();
            JavaStructureMember javaMember = member.getJavaStructureMember();
            String memberConstName = member.getName().getLocalPart().toUpperCase();
            memberQName = Names.getQNameName(member.getName());
            SerializerWriter writer = super.writerFactory.createWriter(member.getType());
            String serializer = writer.serializerMemberName();
            valueStr = null;
            String javaName = javaMember.getType().getName();
            if(javaMember.isPublic()) {
                valueStr = "instance." + javaMember.getName();
            } else {
                String methName = javaMember.getReadMethod();
                valueStr = "instance." + methName + "()";
            }
            if(SimpleToBoxedUtil.isPrimitive(javaName))
                valueStr = SimpleToBoxedUtil.getBoxedExpressionOfType(valueStr, javaName);
            encoder = SimpleTypeSerializerWriter.getTypeEncoder(member.getType());
        }

        p.pOln("}");
    }

    private void writeDoSerializeMethod(IndentingWriter p, LiteralStructuredType type) throws IOException {
        p.plnI("public void doSerialize(Object obj, XMLWriter writer, SOAPSerializationContext context) throws Exception {");
        p.pln(type.getJavaType().getName() + " instance = (" + type.getJavaType().getName() + ")obj;");
        p.pln();
        JavaStructureType javaStructure = (JavaStructureType)type.getJavaType();
        for(Iterator iterator = type.getElementMembers(); iterator.hasNext();) {
            LiteralElementMember member = (LiteralElementMember)iterator.next();
            JavaStructureMember javaMember = member.getJavaStructureMember();
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
            if(member.isRepeated()) {
                String javaElementName = member.getType().getJavaType().getName();
                p.plnI("if (" + valueStr + " != null) {");
                p.plnI("for (int i = 0; i < " + valueStr + ".length; ++i) {");
                if(SimpleToBoxedUtil.isPrimitive(javaElementName))
                    p.pln(serializer + ".serialize(" + SimpleToBoxedUtil.getBoxedExpressionOfType(valueStr + "[i]", javaElementName) + ", " + memberQName + ", null, writer, context);");
                else
                    p.pln(serializer + ".serialize(" + valueStr + "[i], " + memberQName + ", null, writer, context);");
                p.pOln("}");
                p.pOln("}");
            } else {
                p.pln(serializer + ".serialize(" + valueStr + ", " + memberQName + ", null, writer, context);");
            }
        }

        p.pOln("}");
    }
}
