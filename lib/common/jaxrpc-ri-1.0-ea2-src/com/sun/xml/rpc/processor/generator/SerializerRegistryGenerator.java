// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SerializerRegistryGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.client.ServiceImpl;
import com.sun.xml.rpc.encoding.CombinedSerializer;
import com.sun.xml.rpc.encoding.ValueObjectSerializer;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriter;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriterFactory;
import com.sun.xml.rpc.processor.generator.writer.SimpleTypeSerializerWriter;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Message;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Parameter;
import com.sun.xml.rpc.processor.model.Request;
import com.sun.xml.rpc.processor.model.Response;
import com.sun.xml.rpc.processor.model.Service;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralFragmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralSimpleType;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.soap.SOAPAnyType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPCustomType;
import com.sun.xml.rpc.processor.model.soap.SOAPEnumerationType;
import com.sun.xml.rpc.processor.model.soap.SOAPSimpleType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.modeler.ModelerConstants;
import com.sun.xml.rpc.processor.modeler.rmi.RmiTypeModeler;
import com.sun.xml.rpc.processor.util.BatchEnvironment;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.rpc.encoding.SerializerFactory;
import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.rpc.encoding.TypeMappingRegistry;
import javax.xml.rpc.namespace.QName;
import sun.tools.java.ClassFile;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            GeneratorBase, GeneratorException, Names, SimpleToBoxedUtil

public class SerializerRegistryGenerator extends GeneratorBase {

    private static final String SERIALIZER_FACTORY = "serializerFactory";
    private static final String DESERIALIZER_FACTORY = "deserializerFactory";
    private static final String MAPPING = "mapping";
    private static final String LITERAL_MAPPING = "mapping2";
    private boolean haveCustom;
    private Set visitedTypes;

    public SerializerRegistryGenerator() {
        haveCustom = false;
    }

    public GeneratorBase getGenerator(Model model, Configuration config, Properties properties) {
        return new SerializerRegistryGenerator(model, config, properties);
    }

    private SerializerRegistryGenerator(Model model, Configuration config, Properties properties) {
        super(model, config, properties);
        haveCustom = false;
    }

    protected void preVisitService(Service service) throws Exception {
        visitedTypes = new HashSet();
    }

    protected void postVisitService(Service service) throws Exception {
        try {
            generateSerializerRegistry(service);
        }
        catch(IOException ioexception) {
            fail("generator.cant.write", service.getName().getLocalPart());
        }
        visitedTypes = null;
    }

    protected void visitParameter(Parameter param) throws Exception {
        AbstractType type = param.getType();
        if(type.isSOAPType())
            ((SOAPType)type).accept(this);
    }

    protected void preVisitResponse(Response response) throws Exception {
        for(Iterator iter = response.getParameters(); iter.hasNext(); ((Parameter)iter.next()).accept(this));
    }

    protected void preVisitRequest(Request request) throws Exception {
        for(Iterator iter = request.getParameters(); iter.hasNext(); ((Parameter)iter.next()).accept(this));
    }

    public void visit(Fault fault) throws Exception {
        if(fault.getBlock().getType().isSOAPType())
            ((SOAPType)fault.getBlock().getType()).accept(this);
        else
        if(fault.getBlock().getType().isLiteralType())
            ((LiteralType)fault.getBlock().getType()).accept(this);
    }

    public void visit(SOAPCustomType type) throws Exception {
        if(haveVisited(type)) {
            return;
        } else {
            typeVisited(type);
            haveCustom = true;
            return;
        }
    }

    public void visit(SOAPSimpleType type) throws Exception {
        if(haveVisited(type)) {
            return;
        } else {
            typeVisited(type);
            return;
        }
    }

    public void visit(SOAPAnyType type) throws Exception {
        if(haveVisited(type)) {
            return;
        } else {
            typeVisited(type);
            haveCustom = true;
            return;
        }
    }

    public void visit(SOAPEnumerationType type) throws Exception {
        if(haveVisited(type)) {
            return;
        } else {
            typeVisited(type);
            return;
        }
    }

    public void visit(SOAPArrayType type) throws Exception {
        if(haveVisited(type)) {
            return;
        } else {
            typeVisited(type);
            SOAPType elemType = type.getElementType();
            elemType.accept(this);
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

    }

    public void visit(LiteralSimpleType type) throws Exception {
        if(haveVisited(type)) {
            return;
        } else {
            typeVisited(type);
            return;
        }
    }

    public void visit(LiteralFragmentType type) throws Exception {
        if(haveVisited(type)) {
            return;
        } else {
            typeVisited(type);
            return;
        }
    }

    public void visit(LiteralSequenceType type) throws Exception {
        if(haveVisited(type))
            return;
        typeVisited(type);
        LiteralElementMember member;
        for(Iterator members = type.getElementMembers(); members.hasNext(); member.getType().accept(this))
            member = (LiteralElementMember)members.next();

    }

    private void addPrimitiveTypes() {
        for(Iterator types = RmiTypeModeler.getPrimitiveTypes().iterator(); types.hasNext();) {
            SOAPType type = (SOAPType)types.next();
            if(!haveVisited(type))
                typeVisited(type);
        }

    }

    private boolean haveVisited(AbstractType type) {
        return visitedTypes.contains(type);
    }

    private void typeVisited(AbstractType type) {
        visitedTypes.add(type);
    }

    private void generateSerializerRegistry(Service service) throws IOException {
        try {
            com.sun.xml.rpc.processor.model.java.JavaInterface intf = service.getJavaInterface();
            String className = Names.serializerRegistryClassName(intf);
            log("creating serializer registry: " + className);
            java.io.File classFile = Names.sourceFileForClass(className, className, super.sourceDir, super.env);
            super.env.addGeneratedFile(classFile);
            IndentingWriter out = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(classFile)));
            GeneratorBase.writePackage(out, className);
            writeImports(out);
            out.pln();
            writeClassDecl(out, className);
            writeConstructor(out, className);
            out.pln();
            writeGetRegistry(out);
            out.pln();
            writeStatics(out);
            out.pOln("}");
            out.close();
            super.env.parseFile(new ClassFile(classFile));
        }
        catch(Exception e) {
            throw new GeneratorException("generator.nestedGeneratorError", new LocalizableExceptionAdapter(e));
        }
    }

    private void writeImports(IndentingWriter p) throws IOException {
        p.pln("import com.sun.xml.rpc.client.ServiceImpl;");
        p.pln("import com.sun.xml.rpc.encoding.*;");
        p.pln("import com.sun.xml.rpc.encoding.simpletype.*;");
        p.pln("import com.sun.xml.rpc.encoding.soap.SOAPConstants;");
        p.pln("import com.sun.xml.rpc.encoding.literal.*;");
        p.pln("import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;");
        p.pln("import javax.xml.rpc.*;");
        p.pln("import javax.xml.rpc.encoding.*;");
        p.pln("import javax.xml.rpc.namespace.QName;");
    }

    private void writeClassDecl(IndentingWriter p, String className) throws IOException {
        p.plnI("public class " + Names.mangleClass(className) + " implements SerializerConstants {");
    }

    private void writeStatics(IndentingWriter p) throws IOException, Exception {
        p.plnI("private static void registerSerializer(TypeMapping mapping, Class javaType, QName xmlType,");
        p.pln("Serializer ser) {");
        p.plnI("mapping.register(javaType, xmlType, new SingletonSerializerFactory(ser),");
        p.pln("new SingletonDeserializerFactory((Deserializer)ser));");
        p.pO();
        p.pOln("}");
        p.pln();
        p.pln("private static final TypeMappingRegistry registry;");
        Set processedTypes = new TreeSet();
        p.pln();
        p.plnI("static {");
        p.pln("registry = ServiceImpl.createStandardTypeMappingRegistry();");
        p.pln("TypeMapping mapping = registry.getTypeMapping(SOAPConstants.NS_SOAP_ENCODING);");
        p.pln("TypeMapping mapping2 = registry.getTypeMapping(\"\");");
        TypeMappingRegistry registry = ServiceImpl.createStandardTypeMappingRegistry();
        TypeMapping mapping = registry.getTypeMapping("http://schemas.xmlsoap.org/soap/encoding/");
        Iterator types = visitedTypes.iterator();
        while(types.hasNext())  {
            AbstractType type = (AbstractType)types.next();
            if(type.getJavaType().getName().equals(ModelerConstants.VOID_CLASSNAME) || processedTypes.contains(type.getName() + ";" + type.getJavaType().getName()))
                continue;
            processedTypes.add(type.getName() + ";" + type.getJavaType().getName());
            SerializerWriter writer = super.writerFactory.createWriter(type);
            if(writer instanceof SimpleTypeSerializerWriter) {
                if(mappingExistsForType(mapping, type))
                    continue;
                warn("generator.serializerRegistryGenerator.warning.no.standard.simpletype.serialzer", new Object[] {
                    type.getName().toString(), type.getJavaType().getName()
                });
            }
            p.plnI("{");
            if(type.isSOAPType())
                writer.registerSerializer(p, super.encodeTypes, super.multiRefEncoding, "mapping");
            else
            if(type.isLiteralType())
                writer.registerSerializer(p, super.encodeTypes, super.multiRefEncoding, "mapping2");
            p.pOln("}");
        }
        p.pOln("}");
    }

    private boolean mappingExistsForType(TypeMapping mapping, AbstractType type) {
        try {
            Class cls = null;
            String javaName = type.getJavaType().getName();
            if(SimpleToBoxedUtil.isPrimitive(javaName))
                if(javaName.equals(Boolean.TYPE.toString()))
                    cls = Boolean.TYPE;
                else
                if(javaName.equals(ModelerConstants.BYTE_CLASSNAME))
                    cls = Byte.TYPE;
                else
                if(javaName.equals(ModelerConstants.DOUBLE_CLASSNAME))
                    cls = Double.TYPE;
                else
                if(javaName.equals(ModelerConstants.INT_CLASSNAME))
                    cls = Integer.TYPE;
                else
                if(javaName.equals(ModelerConstants.FLOAT_CLASSNAME))
                    cls = Float.TYPE;
                else
                if(javaName.equals(ModelerConstants.LONG_CLASSNAME))
                    cls = Long.TYPE;
                else
                if(javaName.equals(ModelerConstants.SHORT_CLASSNAME))
                    cls = Short.TYPE;
            if(cls == null)
                if(javaName.equals(ModelerConstants.BYTE_ARRAY_CLASSNAME))
                    cls = byte[].class;
                else
                    cls = Class.forName(javaName);
            SerializerFactory factory = mapping.getSerializer(cls, type.getName());
            javax.xml.rpc.encoding.Serializer ser = factory.getSerializerAs("http://java.sun.com/jax-rpc-ri/1.0/streaming/");
            if(!(ser instanceof CombinedSerializer) || !(((CombinedSerializer)ser).getInnermostSerializer() instanceof ValueObjectSerializer))
                return true;
        }
        catch(Exception exception) { }
        return false;
    }

    private void writeConstructor(IndentingWriter p, String className) throws IOException {
        p.plnI("public " + Names.mangleClass(className) + "() {");
        p.pOln("}");
    }

    private void writeGetRegistry(IndentingWriter p) throws IOException {
        p.plnI("public TypeMappingRegistry getRegistry() {");
        p.pln("return registry;");
        p.pOln("}");
    }
}
