// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   StubTieGeneratorBase.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriter;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriterFactory;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Message;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Parameter;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.LiteralFragmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPEnumerationType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.BatchEnvironment;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.wsdl.document.soap.SOAPStyle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.rpc.namespace.QName;
import sun.tools.java.ClassFile;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            GeneratorBase, GeneratorException, Names, GeneratorUtil, 
//            SOAPEncoding, LiteralEncoding

public abstract class StubTieGeneratorBase extends GeneratorBase {

    protected Port port;
    protected HashSet operations;
    protected Set types;
    protected Map portTypes;
    private String prefix;
    protected File srcFile;

    public StubTieGeneratorBase() {
        operations = null;
    }

    protected String getPrefix() {
        return "";
    }

    protected abstract String getClassName();

    protected abstract String getStateType();

    protected abstract Message getMessageToDeserialize(Operation operation);

    protected abstract String getStateGetRequestResponseString();

    protected abstract String getInitializeAccess();

    protected abstract boolean superClassHasInitialize();

    protected StubTieGeneratorBase(Model model, Configuration config, Properties properties) {
        super(model, config, properties);
        operations = null;
        prefix = getPrefix();
        srcFile = null;
    }

    protected void preVisitModel(Model model) throws Exception {
        types = new HashSet();
    }

    protected void postVisitModel(Model model) throws Exception {
        types = null;
    }

    protected void preVisitPort(Port port) throws Exception {
        operations = new HashSet();
        portTypes = new HashMap();
        this.port = port;
    }

    protected void postVisitPort(Port port) throws Exception {
        writeClass();
        this.port = null;
        portTypes = null;
        operations = null;
    }

    protected void postVisitOperation(Operation operation) throws Exception {
        operations.add(operation);
    }

    protected void responseBodyBlock(Block block) throws Exception {
        registerBlock(block);
    }

    protected void responseHeaderBlock(Block block) throws Exception {
        registerBlock(block);
    }

    protected void requestBodyBlock(Block block) throws Exception {
        registerBlock(block);
    }

    protected void requestHeaderBlock(Block block) throws Exception {
        registerBlock(block);
    }

    protected void preVisitSOAPEnumerationType(SOAPEnumerationType type) throws Exception {
        if(isRegistered(type)) {
            return;
        } else {
            registerType(type);
            return;
        }
    }

    protected void preVisitSOAPArrayType(SOAPArrayType type) throws Exception {
        if(isRegistered(type)) {
            return;
        } else {
            registerType(type);
            return;
        }
    }

    protected void preVisitSOAPStructureType(SOAPStructureType type) throws Exception {
        if(isRegistered(type)) {
            return;
        } else {
            registerType(type);
            return;
        }
    }

    protected void preVisitLiteralFragmentType(LiteralFragmentType type) throws Exception {
        if(isRegistered(type)) {
            return;
        } else {
            registerType(type);
            return;
        }
    }

    private void registerBlock(Block block) {
        String key = null;
        if(block.getType().isSOAPType())
            key = block.getType().getJavaType().getName();
        else
        if(block.getType().isLiteralType())
            key = block.getType().getName().toString() + block.getType().getJavaType().getName();
        if(!portTypes.containsKey(key))
            portTypes.put(key, block);
    }

    private boolean isRegistered(AbstractType type) {
        return types.contains(type);
    }

    private void registerType(AbstractType type) {
        types.add(type);
    }

    protected void writeClass() {
        String remoteClassName = port.getJavaInterface().getName();
        String className = getClassName();
        srcFile = Names.sourceFileForClass(className, className, super.sourceDir, super.env);
        super.env.addGeneratedFile(srcFile);
        try {
            super.out = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(srcFile)));
            GeneratorBase.writePackage(super.out, className);
            writeImports(super.out);
            super.out.pln();
            writeClassDecl(super.out, className);
            super.out.pln();
            writeConstructor(super.out, className);
            super.out.pln();
            writeOperations(super.out, remoteClassName);
            super.out.pln();
            writePeekFirstBodyElementMethod(super.out);
            super.out.pln();
            writeReadFirstBodyElement(super.out);
            super.out.pln();
            Map headerMap = writeReadHeaderElementMethod(super.out);
            super.out.pln();
            writeHeaderDeserializeMethods(super.out, headerMap.values().iterator());
            super.out.pln();
            writeOperationDeserializeMethods(super.out);
            super.out.pln();
            writeReadBodyFaultElement(super.out);
            super.out.pln();
            writeProcessingHookMethod(super.out);
            super.out.pln();
            writeGenericMethods(super.out);
            super.out.pln();
            writeGetNamespaceDeclarationsMethod(super.out);
            super.out.pln();
            writeInitialize(super.out);
            super.out.pln();
            writeStaticMembers(super.out, headerMap);
            closeSrcFile();
        }
        catch(IOException ioexception) {
            fail("generator.cant.write", port.getName().getLocalPart());
        }
    }

    protected void writeImports(IndentingWriter p) throws IOException {
        p.pln("import com.sun.xml.rpc.streaming.*;");
        p.pln("import com.sun.xml.rpc.encoding.*;");
        p.pln("import com.sun.xml.rpc.encoding.soap.SOAPConstants;");
        p.pln("import com.sun.xml.rpc.encoding.literal.*;");
        p.pln("import com.sun.xml.rpc.soap.streaming.*;");
        p.pln("import com.sun.xml.rpc.soap.message.*;");
        p.pln("import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;");
        p.pln("import javax.xml.rpc.namespace.QName;");
        p.pln("import java.rmi.RemoteException;");
        p.pln("import java.util.Iterator;");
    }

    protected void writeClassDecl(IndentingWriter p, String className) throws IOException {
        p.pln("public final class " + Names.mangleClass(className));
        p.pln(" {");
        p.pln();
    }

    protected void writeConstructor(IndentingWriter p, String stubClassName) throws IOException {
        p.pln("/*");
        p.pln(" *  public constructor");
        p.pln(" */");
        p.plnI("public " + Names.mangleClass(stubClassName) + "() {");
        p.pOln("}");
    }

    protected void writeOperations(IndentingWriter p, String remoteClassName) throws IOException {
        Iterator iter = operations.iterator();
        for(int i = 0; iter.hasNext(); i++) {
            if(i > 0)
                p.pln();
            Operation operation = (Operation)iter.next();
            if(operation.getStyle() == SOAPStyle.DOCUMENT)
                writeDocumentOperation(p, remoteClassName, operation);
            else
                writeRpcOperation(p, remoteClassName, operation);
        }

    }

    protected void writeRpcOperation(IndentingWriter indentingwriter, String s, Operation operation1) throws IOException, GeneratorException {
    }

    protected void writeDocumentOperation(IndentingWriter indentingwriter, String s, Operation operation1) throws IOException, GeneratorException {
    }

    protected void writePeekFirstBodyElementMethod(IndentingWriter indentingwriter) throws IOException {
    }

    protected void writeReadFirstBodyElement(IndentingWriter p) throws IOException {
        String stateType = getStateType();
        p.pln("/*");
        p.pln(" *  this method deserializes the response structure in the body");
        p.pln(" */");
        p.plnI("protected void " + prefix + "readFirstBodyElement(XMLReader bodyReader, SOAPDeserializationContext deserializationContext, " + stateType + "  state) throws Exception {");
        p.pln("int opcode = state.getRequest().getOperationCode();");
        p.plnI("switch (opcode) {");
        for(Iterator operationsIter = operations.iterator(); operationsIter.hasNext(); p.pO()) {
            Operation operation = (Operation)operationsIter.next();
            p.plnI("case " + Names.getOPCodeName(operation.getUniqueName()) + ":");
            p.pln(prefix + "deserialize_" + operation.getUniqueName() + "(bodyReader, deserializationContext, state);");
            p.pln("break;");
        }

        p.plnI("default:");
        writeReadFirstBodyElementDefault(p, "opcode");
        p.pO();
        p.pOln("}");
        p.pOln("}");
    }

    protected void writeReadFirstBodyElementDefault(IndentingWriter indentingwriter, String s) throws IOException {
    }

    private void writeOperationDeserializeMethods(IndentingWriter p) throws IOException {
        Iterator operationsIter = operations.iterator();
        for(int i = 0; operationsIter.hasNext(); i++) {
            if(i > 0)
                p.pln();
            Operation operation = (Operation)operationsIter.next();
            writeOperationDeserializeMethod(p, operation);
        }

    }

    private void writeOperationDeserializeMethod(IndentingWriter p, Operation operation) throws IOException {
        String stateType = getStateType();
        String messageName = operation.getName().getLocalPart();
        Message message = getMessageToDeserialize(operation);
        p.pln("/*");
        p.pln(" * This method deserializes the body of the " + messageName + " operation.");
        p.pln(" */");
        p.plnI("private void " + prefix + "deserialize_" + operation.getUniqueName() + "(XMLReader bodyReader, SOAPDeserializationContext deserializationContext, " + stateType + " state) throws Exception {");
        Block bodyBlock = (Block)message.getBodyBlocks().next();
        AbstractType type = bodyBlock.getType();
        String objName = Names.getTypeMemberName(type.getJavaType());
        SerializerWriter writer = super.writerFactory.createWriter(type);
        String serializer = writer.serializerMemberName();
        p.plnI("Object " + objName + "Obj =");
        p.plnI(serializer + ".deserialize(" + Names.getBlockQNameName(operation, bodyBlock) + ",");
        p.pln("bodyReader, deserializationContext);");
        p.pO();
        p.pO();
        objName = Names.getTypeMemberName(type.getJavaType()) + "Obj";
        p.pln();
        p.pln("SOAPBlockInfo bodyBlock = new SOAPBlockInfo(" + Names.getBlockQNameName(operation, bodyBlock) + ");");
        p.pln("bodyBlock.setValue(" + objName + ");");
        p.pln("state." + getStateGetRequestResponseString() + "().setBody(bodyBlock);");
        p.pOln("}");
    }

    protected void writeReadBodyFaultElement(IndentingWriter indentingwriter) throws IOException {
    }

    protected Map writeReadHeaderElementMethod(IndentingWriter p) throws IOException {
        Iterator ops = operations.iterator();
        Iterator headers;
        boolean hasHeaders;
        for(hasHeaders = false; !hasHeaders && ops.hasNext(); hasHeaders = hasHeaders || !headers.hasNext() ? hasHeaders : true) {
            Operation operation = (Operation)ops.next();
            Message message = operation.getRequest();
            headers = message.getHeaderBlocks();
            hasHeaders = hasHeaders || !headers.hasNext() ? hasHeaders : true;
            message = operation.getResponse();
            headers = message.getHeaderBlocks();
        }

        Map headerMap = new HashMap();
        if(!hasHeaders)
            return headerMap;
        String stateType = getStateType();
        p.pln("/*");
        p.pln(" * This method must deserialize headers. It dispatches to a read method based on the name");
        p.pln(" * of the header.");
        p.pln(" */");
        p.plnI("protected boolean " + prefix + "readHeaderElement(SOAPHeaderBlockInfo headerInfo, XMLReader headerReader, SOAPDeserializationContext deserializationContext, " + stateType + " state) throws Exception {");
        ops = operations.iterator();
        Message message;
        for(boolean first = true; ops.hasNext(); first = !first || !message.getHeaderBlocks().hasNext() ? first : false) {
            Operation operation = (Operation)ops.next();
            message = operation.getRequest();
            writeHeaderChecks(p, message.getHeaderBlocks(), first, headerMap);
            first = !first || !message.getHeaderBlocks().hasNext() ? first : false;
            message = operation.getResponse();
            writeHeaderChecks(p, message.getHeaderBlocks(), first, headerMap);
        }

        p.pln();
        p.pln("return false;");
        p.pOln("}");
        return headerMap;
    }

    private void writeHeaderChecks(IndentingWriter p, Iterator headers, boolean first, Map headerMap) throws IOException {
        while(headers.hasNext())  {
            Block header = (Block)headers.next();
            if(!headerMap.containsKey(header.getName())) {
                headerMap.put(header.getName(), header);
                if(!first)
                    p.p(" else ");
                first = false;
                String qname = Names.getBlockQNameName(null, header);
                String uname = Names.getBlockUniqueName(null, header);
                p.plnI("if (headerInfo.getName().equals(" + qname + ")) {");
                p.pln(prefix + "deserialize_" + uname + "(headerInfo, headerReader, deserializationContext, state);");
                p.pln("return true;");
                p.pO("}");
            }
        }
    }

    private void writeHeaderDeserializeMethods(IndentingWriter p, Iterator headers) throws IOException {
        for(int i = 0; headers.hasNext(); i++) {
            if(i > 0)
                p.pln();
            Block header = (Block)headers.next();
            writeHeaderDeserializeMethod(p, header);
        }

    }

    private void writeHeaderDeserializeMethod(IndentingWriter p, Block header) throws IOException {
        String javaType = header.getType().getJavaType().getName();
        String serializer = super.writerFactory.createWriter((SOAPType)header.getType()).serializerMemberName();
        String qname = Names.getBlockQNameName(null, header);
        String uname = Names.getBlockUniqueName(null, header);
        String stateType = getStateType();
        p.pln("/*");
        p.pln(" *  This method does the actual deserialization for the header: " + header.getName().getLocalPart() + ".");
        p.pln(" */");
        p.plnI("private void " + prefix + "deserialize_" + uname + "(SOAPHeaderBlockInfo headerInfo, XMLReader bodyReader, SOAPDeserializationContext deserializationContext, " + stateType + " state) throws Exception {");
        p.pln("QName elementName = bodyReader.getName();");
        p.plnI("if (elementName.equals(" + qname + ")) {");
        p.plnI(javaType + " obj =");
        p.pln("(" + javaType + ")" + serializer + ".deserialize(" + qname + ", bodyReader, deserializationContext);");
        p.pOln("headerInfo.setValue(obj);");
        p.pln("state." + getStateGetRequestResponseString() + "().add(headerInfo);");
        p.pOlnI("} else {");
        p.pln("// the QName of the header is not what we expected and not a fault either");
        p.pln("throw new SOAPProtocolViolationException(\"soap.unexpectedHeaderBlock\", elementName.getLocalPart());");
        p.pOln("}");
        p.pOln("}");
    }

    protected void writeProcessingHookMethod(IndentingWriter indentingwriter) throws IOException {
    }

    public void writeGenericMethods(IndentingWriter indentingwriter) throws IOException {
    }

    private void writeGetNamespaceDeclarationsMethod(IndentingWriter p) throws IOException {
        p.pln("/*");
        p.pln(" * This method returns an array containing (prefix, nsURI) pairs.");
        p.pln(" */");
        p.plnI("protected String[] " + prefix + "getNamespaceDeclarations() {");
        p.pln("return myNamespace_declarations;");
        p.pOln("}");
    }

    private void writeInitialize(IndentingWriter p) throws IOException {
        Iterator types = portTypes.entrySet().iterator();
        String access = getInitializeAccess();
        p.plnI(access + " void " + prefix + "initialize(InternalTypeMappingRegistry registry) throws Exception {");
        if(superClassHasInitialize())
            p.pln("super." + prefix + "initialize(registry);");
        AbstractType type;
        SerializerWriter writer;
        for(; types.hasNext(); writer.initializeSerializer(p, Names.getTypeQName(type.getName()), "registry")) {
            java.util.Map$Entry entry = (java.util.Map$Entry)types.next();
            Block block = (Block)entry.getValue();
            type = block.getType();
            writer = super.writerFactory.createWriter(type);
        }

        Iterator operationsIter = operations.iterator();
        for(int i = 0; operationsIter.hasNext(); i++) {
            Operation operation = (Operation)operationsIter.next();
            if(operation.getFaults().hasNext()) {
                String serName = Names.getClassMemberName(Names.faultSerializerClassName(port, operation));
                p.pln("((Initializable)" + serName + ").initialize(registry);");
            }
        }

        p.pOln("}");
    }

    protected void writeStaticMembers(IndentingWriter p, Map headerMap) throws IOException {
        ArrayList list = new ArrayList();
        ArrayList visited = new ArrayList();
        Iterator operationsIter = operations.iterator();
        p.p("private static final QName " + prefix + "portName = ");
        GeneratorUtil.writeNewQName(p, port.getName());
        p.pln(";");
        for(int i = 0; operationsIter.hasNext(); i++) {
            Operation operation = (Operation)operationsIter.next();
            p.pln("private static final int " + Names.getOPCodeName(operation.getUniqueName()) + " = " + i + ";");
        }

        operationsIter = operations.iterator();
        for(int i = 0; operationsIter.hasNext(); i++) {
            Operation operation = (Operation)operationsIter.next();
            Iterator faults = operation.getFaults();
            if(faults.hasNext())
                declareStaticFaultSerializerForOperation(p, port, operation, super.encodeTypes, super.multiRefEncoding);
            for(; faults.hasNext(); collectNamespaces(((Fault)faults.next()).getBlock().getType(), list, visited));
        }

        Set processedTypes = new TreeSet();
        operationsIter = operations.iterator();
        Iterator blocks = headerMap.values().iterator();
        declareBlockTypes(p, null, blocks, processedTypes, list, visited);
        for(int i = 0; operationsIter.hasNext(); i++) {
            Operation operation = (Operation)operationsIter.next();
            blocks = operation.getRequest().getBodyBlocks();
            declareBlockTypes(p, operation, blocks, processedTypes, list, visited);
            blocks = operation.getResponse().getHeaderBlocks();
            declareBlockTypes(p, operation, blocks, processedTypes, list, visited);
            blocks = operation.getResponse().getBodyBlocks();
            declareBlockTypes(p, operation, blocks, processedTypes, list, visited);
        }

        list.remove("http://www.w3.org/2001/XMLSchema");
        list.remove("http://schemas.xmlsoap.org/soap/encoding/");
        Iterator namespaces = list.iterator();
        p.plnI("private static final String[] myNamespace_declarations =");
        p.pI(8);
        p.plnI("new String[] {");
        for(int j = 0; namespaces.hasNext(); j++) {
            if(j > 0)
                p.pln(",");
            p.p("\"ns" + j + "\", ");
            p.p("\"" + (String)namespaces.next() + "\"");
        }

        p.pln();
        p.pOln("};");
        p.pO(8);
        p.pO();
    }

    private void declareStaticFaultSerializerForOperation(IndentingWriter p, Port port, Operation operation, boolean encodeTypesNow, boolean multiRefEncodingNow) throws IOException {
        String nillable = "NOT_NULLABLE";
        String referenceable = "REFERENCEABLE";
        String multiRef = multiRefEncodingNow ? "SERIALIZE_AS_REF" : "DONT_SERIALIZE_AS_REF";
        String encodeType = encodeTypesNow ? "ENCODE_TYPE" : "DONT_ENCODE_TYPE";
        String serializerClassName = Names.faultSerializerClassName(port, operation);
        String memberName = Names.getClassMemberName(serializerClassName);
        p.plnI("private static final CombinedSerializer " + memberName + " = new ReferenceableSerializerImpl(" + multiRef + ",");
        p.pln("new " + serializerClassName + "(" + encodeType + ", " + nillable + "));");
        p.pO();
    }

    private void declareBlockTypes(IndentingWriter p, Operation operation, Iterator blocks, Set processedTypes, List list, List visited) throws IOException {
        while(blocks.hasNext())  {
            Block block = (Block)blocks.next();
            collectNamespaces(block.getType(), list, visited);
            if(!processedTypes.contains(Names.getBlockQNameName(operation, block))) {
                GeneratorUtil.writeBlockQNameDeclaration(p, operation, block);
                processedTypes.add(Names.getBlockQNameName(operation, block));
            }
            if(block.getType().isSOAPType())
                SOAPEncoding.writeStaticSerializer(p, (SOAPType)block.getType(), processedTypes, super.writerFactory);
            else
            if(block.getType().isLiteralType())
                LiteralEncoding.writeStaticSerializer(p, (LiteralType)block.getType(), processedTypes, super.writerFactory);
        }
    }

    private static void collectNamespaces(AbstractType type, List list, List visited) {
        if(visited.contains(type.getJavaType().getName()))
            return;
        visited.add(type.getJavaType().getName());
        if(type.getName().getNamespaceURI().length() > 0 && !list.contains(type.getName().getNamespaceURI()))
            list.add(type.getName().getNamespaceURI());
        if(type instanceof SOAPStructureType) {
            SOAPStructureMember member;
            for(Iterator members = ((SOAPStructureType)type).getMembers(); members.hasNext(); collectNamespaces(((AbstractType) (member.getType())), list, visited)) {
                member = (SOAPStructureMember)members.next();
                if(member.getName().getNamespaceURI().length() > 0 && !list.contains(member.getName().getNamespaceURI()))
                    list.add(member.getName().getNamespaceURI());
            }

        } else
        if(type instanceof SOAPArrayType)
            collectNamespaces(((AbstractType) (((SOAPArrayType)type).getElementType())), list, visited);
    }

    public static JavaStructureMember getJavaMember(Parameter parameter) {
        Block block = parameter.getBlock();
        JavaType type = block.getType().getJavaType();
        if(type instanceof JavaStructureType)
            return ((JavaStructureType)type).getMemberByName(parameter.getName());
        else
            return null;
    }

    private void closeSrcFile() throws IOException {
        if(super.out != null) {
            super.out.pOln("}");
            super.out.close();
            super.out = null;
            super.env.parseFile(new ClassFile(srcFile));
        }
    }
}
