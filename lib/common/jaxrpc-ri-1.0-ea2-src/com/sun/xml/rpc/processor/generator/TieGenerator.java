// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   TieGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.config.*;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriter;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriterFactory;
import com.sun.xml.rpc.processor.model.*;
import com.sun.xml.rpc.processor.model.java.*;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.modeler.ModelerConstants;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import java.io.IOException;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            StubTieGeneratorBase, GeneratorException, Names, GeneratorBase

public class TieGenerator extends StubTieGeneratorBase {

    private Set operationNames;

    public TieGenerator() {
    }

    public GeneratorBase getGenerator(Model model, Configuration config, Properties properties) {
        return new TieGenerator(model, config, properties);
    }

    private TieGenerator(Model model, Configuration config, Properties properties) {
        super(model, config, properties);
    }

    protected String getClassName() {
        return Names.tieFor(super.port.getJavaInterface().getName());
    }

    protected String getStateType() {
        return "StreamingHandlerState";
    }

    protected Message getMessageToDeserialize(Operation operation) {
        Message message = operation.getRequest();
        if(message.getBodyBlockCount() != 1)
            fail("generator.tie.cannot.dispatch", operation.getName().getLocalPart());
        return message;
    }

    protected String getStateGetRequestResponseString() {
        return "getRequest";
    }

    protected String getInitializeAccess() {
        return "private";
    }

    protected boolean superClassHasInitialize() {
        return false;
    }

    protected void writeImports(IndentingWriter p) throws IOException {
        super.writeImports(p);
        p.pln("import com.sun.xml.rpc.server.*;");
        p.pln("import javax.xml.rpc.handler.HandlerInfo;");
    }

    protected void preVisitPort(Port port) throws Exception {
        super.preVisitPort(port);
        operationNames = new HashSet();
    }

    protected void postVisitPort(Port port) throws Exception {
        operationNames = null;
        super.postVisitPort(port);
    }

    protected void preVisitOperation(Operation operation) throws Exception {
        String name = operation.getName().getLocalPart();
        if(operationNames.contains(name)) {
            throw new GeneratorException("generator.tie.operation.nameNotUnique", name);
        } else {
            operationNames.add(name);
            return;
        }
    }

    protected void writeClassDecl(IndentingWriter p, String tieClassName) throws IOException {
        p.plnI("public final class " + Names.mangleClass(tieClassName));
        p.pln("extends com.sun.xml.rpc.server.TieBase implements SerializerConstants {");
        p.pln();
    }

    protected void writeConstructor(IndentingWriter p, String tieClassName) throws IOException {
        JavaInterface intf = super.service.getJavaInterface();
        String serializerRegistryName = Names.serializerRegistryClassName(intf);
        p.plnI("public " + Names.mangleClass(tieClassName) + "() throws Exception {");
        p.pln("super(new " + serializerRegistryName + "().getRegistry());");
        p.pln("initialize(internalTypeMappingRegistry);");
        HandlerChainInfo portServiceHandlers = super.port.getServerHandlerChainInfo();
        Iterator eachHandler = portServiceHandlers.getHandlers();
        if(eachHandler.hasNext())
            p.pln();
        for(; eachHandler.hasNext(); p.pOln("}")) {
            HandlerInfo currentHandler = (HandlerInfo)eachHandler.next();
            Map properties = currentHandler.getProperties();
            String propertiesName = "null";
            p.plnI("{");
            if(properties.size() > 0) {
                propertiesName = "props";
                p.pln("java.util.Map " + propertiesName + " = new java.util.HashMap();");
                java.util.Map$Entry entry;
                for(Iterator entries = properties.entrySet().iterator(); entries.hasNext(); p.pln(propertiesName + ".put(\"" + (String)entry.getKey() + "\", \"" + (String)entry.getValue() + "\");"))
                    entry = (java.util.Map$Entry)entries.next();

            }
            p.pln("HandlerInfo handlerInfo = new HandlerInfo(" + currentHandler.getHandlerClassName() + ".class" + ", " + propertiesName + ");");
            p.pln("handlerChain.add(handlerInfo);");
        }

        p.pOln("}");
    }

    protected void writePeekFirstBodyElementMethod(IndentingWriter p) throws IOException {
        p.pln("/*");
        p.pln(" * This method must determine the opcode of the operation that has been invoked.");
        p.pln(" */");
        p.plnI("protected void peekFirstBodyElement(XMLReader bodyReader, SOAPDeserializationContext deserializationContext, StreamingHandlerState state) throws Exception {");
        Iterator operationsIter = super.operations.iterator();
        int j = 0;
        for(; operationsIter.hasNext(); p.pOln("}")) {
            Operation operation = (Operation)operationsIter.next();
            if(j++ > 0)
                p.p("else ");
            Message message = operation.getRequest();
            if(message.getBodyBlockCount() != 1)
                fail("generator.tie.cannot.dispatch", operation.getName().getLocalPart());
            Block bodyBlock = (Block)message.getBodyBlocks().next();
            p.plnI("if (bodyReader.getName().equals(" + Names.getBlockQNameName(operation, bodyBlock) + ")) {");
            if(operation.isOverloaded())
                p.pln("throw new SOAPProtocolViolationException(\"soap.operation.cannot.dispatch\", \"" + operation.getName().getLocalPart() + "\");");
            else
                p.pln("state.getRequest().setOperationCode(" + Names.getOPCodeName(operation.getUniqueName()) + ");");
        }

        if(j > 0)
            p.plnI("else {");
        p.pln("throw new SOAPProtocolViolationException(\"soap.operation.unrecognized\", bodyReader.getName().toString());");
        if(j > 0)
            p.pOln("}");
        p.pOln("}");
    }

    protected void writeProcessingHookMethod(IndentingWriter p) throws IOException {
        p.pln("/*");
        p.pln(" * This method must invoke the correct method on the servant based on the opcode.");
        p.pln(" */");
        p.plnI("protected void processingHook(StreamingHandlerState state) throws Exception {");
        Iterator operationsIter = super.operations.iterator();
        p.plnI("switch (state.getRequest().getOperationCode()) {");
        for(; operationsIter.hasNext(); p.pO()) {
            Operation operation = (Operation)operationsIter.next();
            p.plnI("case " + Names.getOPCodeName(operation.getUniqueName()) + ":");
            p.pln("invoke_" + operation.getUniqueName() + "(state);");
            p.pln("break;");
        }

        p.plnI("default:");
        p.pln("throw new SOAPProtocolViolationException(\"soap.operation.unrecognized\", Integer.toString(state.getRequest().getOperationCode()));");
        p.pO();
        p.pOln("}");
        p.pOln("}");
    }

    protected void writeRpcOperation(IndentingWriter p, String remoteClassName, Operation operation) throws IOException {
        String messageName = operation.getName().getLocalPart();
        JavaMethod javaMethod = operation.getJavaMethod();
        JavaType resultType = javaMethod.getReturnType();
        String requestObjType = null;
        String requestObjName = null;
        SOAPType requestBlockType = null;
        boolean hasFaults = operation.getFaults().hasNext();
        Message message = operation.getRequest();
        Iterator iterator = message.getBodyBlocks();
        Block requestBlock = null;
        while(iterator.hasNext())  {
            requestBlock = (Block)iterator.next();
            if(requestBlock.getName().getLocalPart().equals(messageName)) {
                requestBlockType = (SOAPType)requestBlock.getType();
                requestObjType = requestBlockType.getJavaType().getName();
                requestObjName = Names.getTypeMemberName(requestBlockType.getJavaType());
                break;
            }
        }
        writeInvokeMethodDecl(p, operation);
        declareRequestObjects(p, requestObjType, requestObjName);
        declareHolderHeaderObjects(p, requestBlock, operation);
        boolean hasRequestHeaders = false;
        iterator = operation.getRequest().getHeaderBlocks();
        hasRequestHeaders = iterator.hasNext();
        if(hasRequestHeaders)
            writeRequestHeaders(p, operation);
        if(hasFaults)
            p.plnI("try {");
        declareRpcReturnType(p, operation, resultType);
        p.p("((" + remoteClassName + ") getTarget())." + operation.getJavaMethod().getName() + "(");
        if(resultType != null && !resultType.getName().equals(ModelerConstants.VOID_CLASSNAME))
            p.pO();
        iterator = javaMethod.getParameters();
        message = operation.getRequest();
        for(int i = 0; iterator.hasNext(); i++) {
            if(i > 0)
                p.p(", ");
            JavaParameter javaParameter = (JavaParameter)iterator.next();
            Parameter parameter = javaParameter.getParameter();
            Block paramBlock = parameter.getBlock();
            if(paramBlock.getLocation() != 1) {
                if(javaParameter.isHolder())
                    p.p(javaParameter.getParameter().getName() + "_holder");
                else
                    p.p(parameter.getName());
            } else {
                JavaType javaObjType = paramBlock.getType().getJavaType();
                String javaObjName = Names.getTypeMemberName(javaObjType);
                JavaStructureMember javaMember = StubTieGeneratorBase.getJavaMember(parameter);
                if(javaParameter.isHolder())
                    p.p(javaParameter.getParameter().getName() + "_holder");
                else
                if(javaMember.isPublic())
                    p.p(javaObjName + "." + parameter.getName());
                else
                    p.p(javaObjName + "." + javaMember.getReadMethod() + "()");
            }
        }

        p.pln(");");
        declareRpcResponseObject(p, operation);
        for(Iterator faults = operation.getFaults(); faults.hasNext(); p.pln("state.getResponse().setBody(faultBlock);")) {
            Fault fault = (Fault)faults.next();
            p.pOlnI("} catch (" + Names.customExceptionClassName(fault) + " e) {");
            p.plnI("SOAPFaultInfo fault = new SOAPFaultInfo(com.sun.xml.rpc.encoding.soap.SOAPConstants.FAULT_CODE_SERVER,");
            p.pln("\"" + Names.customExceptionClassName(fault) + "\", null, e);");
            p.pO();
            p.pln("SOAPBlockInfo faultBlock = new SOAPBlockInfo(com.sun.xml.rpc.encoding.soap.SOAPConstants.QNAME_SOAP_FAULT);");
            p.pln("faultBlock.setValue(fault);");
            p.pln("faultBlock.setSerializer(" + Names.getClassMemberName(Names.faultSerializerClassName(super.port, operation)) + ");");
        }

        if(hasFaults)
            p.pOln("}");
        p.pOln("}");
    }

    private void writeInvokeMethodDecl(IndentingWriter p, Operation operation) throws IOException {
        String messageName = operation.getName().getLocalPart();
        p.pln("/*");
        p.pln(" * This method does the actual method invocation for operation: " + messageName);
        p.pln(" */");
        p.plnI("private void invoke_" + operation.getUniqueName() + "(StreamingHandlerState state) throws Exception {");
        p.pln();
    }

    private void declareRequestObjects(IndentingWriter p, String requestObjType, String requestObjName) throws IOException {
        String requestObjMemberName = requestObjName + "Obj";
        p.pln(requestObjType + " " + requestObjName + " = null;");
        p.plnI("Object " + requestObjMemberName + " =");
        p.pln("state.getRequest().getBody().getValue();");
        p.pO();
        p.pln();
        p.plnI("if (" + requestObjMemberName + " instanceof SOAPDeserializationState) {");
        p.p(requestObjName + " =");
        p.pln("(" + requestObjType + ")((SOAPDeserializationState)" + requestObjMemberName + ").getInstance();");
        p.pOlnI("} else {");
        p.p(requestObjName + " =");
        p.pln("(" + requestObjType + ")" + requestObjMemberName + ";");
        p.pOln("}");
        p.pln();
    }

    private void declareHolderHeaderObjects(IndentingWriter p, Block requestBlock, Operation operation) throws IOException {
        SOAPType requestBlockType = (SOAPType)requestBlock.getType();
        String requestObjType = requestBlockType.getJavaType().getName();
        String requestObjName = Names.getTypeMemberName(requestBlockType.getJavaType());
        String requestObjMemberName = requestObjName + "Obj";
        JavaMethod javaMethod = operation.getJavaMethod();
        Iterator iterator = javaMethod.getParameters();
        boolean declaredHeaderObj = false;
        int i = 0;
        while(iterator.hasNext())  {
            JavaParameter javaParameter = (JavaParameter)iterator.next();
            if(javaParameter.isHolder()) {
                String holderClassName = Names.holderClassName(super.port, javaParameter.getType());
                p.plnI(holderClassName + " " + javaParameter.getParameter().getName() + "_holder =");
                p.pln("new " + holderClassName + "();");
                p.pO();
                if(javaParameter.getParameter().getLinkedParameter() != null) {
                    JavaStructureMember javaMember = StubTieGeneratorBase.getJavaMember(javaParameter.getParameter());
                    p.pln(" " + javaParameter.getParameter().getName() + "_holder.value = " + requestObjName + "." + javaMember.getReadMethod() + "();");
                }
            } else
            if(javaParameter.getParameter().getBlock().getLocation() == 2) {
                if(!declaredHeaderObj) {
                    p.pln("Object _headerObj;");
                    declaredHeaderObj = true;
                }
                AbstractType paramType = javaParameter.getParameter().getType();
                String initValue = javaParameter.getType().getInitString();
                p.pln(paramType.getJavaType().getName() + " " + javaParameter.getParameter().getName() + " = " + initValue + ";");
            }
        }
    }

    private void declareRpcReturnType(IndentingWriter p, Operation operation, JavaType resultType) throws IOException {
        Message message = operation.getResponse();
        Iterator iterator = message.getBodyBlocks();
        if(resultType != null && !resultType.getName().equals(ModelerConstants.VOID_CLASSNAME)) {
            iterator = message.getParameters();
            if(iterator.hasNext()) {
                Parameter parameter = (Parameter)iterator.next();
                p.plnI(parameter.getType().getJavaType().getName() + " " + parameter.getName() + " = ");
            }
        }
    }

    private void writeRequestHeaders(IndentingWriter p, Operation operation) throws IOException {
        p.pln("Iterator headers = state.getRequest().headers();");
        p.pln("SOAPHeaderBlockInfo curHeader;");
        p.plnI("while (headers.hasNext()) {");
        p.pln("curHeader = (SOAPHeaderBlockInfo)headers.next();");
        Iterator iterator = operation.getRequest().getParameters();
        boolean startedHeaders = false;
        while(iterator.hasNext())  {
            Parameter parameter = (Parameter)iterator.next();
            if(parameter.getBlock().getLocation() == 2) {
                if(startedHeaders)
                    p.p(" else ");
                startedHeaders = true;
                String paramName = parameter.getName();
                String paramType = parameter.getType().getJavaType().getName();
                String qname = Names.getBlockQNameName(null, parameter.getBlock());
                p.plnI("if (curHeader.getName().equals(" + qname + ")) {");
                p.pln("_headerObj = (" + paramType + ")curHeader.getValue();");
                p.plnI("if (_headerObj instanceof SOAPDeserializationState) {");
                p.pln(paramName + " = (" + paramType + ")((SOAPDeserializationState)" + "_headerObj).getInstance();");
                p.pOlnI("} else {");
                p.pln(paramName + " = (" + paramType + ")_headerObj;");
                p.pOln("}");
                p.pO("}");
            }
        }
        if(startedHeaders)
            p.pln();
        p.pOln("}");
        p.pln();
    }

    private void declareRpcResponseObject(IndentingWriter p, Operation operation) throws IOException {
        String messageName = operation.getName().getLocalPart();
        Message message = operation.getResponse();
        Iterator iterator = message.getBodyBlocks();
        Block responseBlock = null;
        SOAPType responseBlockType = null;
        String responseObjType = null;
        String responseObjName = null;
        for(; iterator.hasNext(); responseBlock = null) {
            responseBlock = (Block)iterator.next();
            if(!responseBlock.getName().getLocalPart().equals(messageName + "Response"))
                continue;
            responseBlockType = (SOAPType)responseBlock.getType();
            responseObjType = responseBlockType.getJavaType().getName();
            responseObjName = Names.getTypeMemberName(responseBlockType.getJavaType());
            break;
        }

        p.plnI(responseObjType + " " + responseObjName + " =");
        p.pln("new " + responseObjType + "();");
        p.pO();
        message = operation.getResponse();
        Iterator iterator2 = message.getParameters();
        p.pln("SOAPHeaderBlockInfo headerInfo;");
        for(int i = 0; iterator2.hasNext(); i++) {
            Parameter parameter = (Parameter)iterator2.next();
            Block block = parameter.getBlock();
            if(block.getLocation() == 1) {
                JavaStructureMember javaMember = StubTieGeneratorBase.getJavaMember(parameter);
                JavaParameter javaParameter = parameter.getJavaParameter();
                String memberName;
                if(parameter.getLinkedParameter() != null || javaParameter != null && javaParameter.isHolder())
                    memberName = parameter.getName() + "_holder.value";
                else
                    memberName = parameter.getName();
                if(javaMember != null)
                    if(javaMember.isPublic())
                        p.pln(responseObjName + "." + javaMember.getName() + " = " + memberName + ";");
                    else
                        p.pln(responseObjName + "." + javaMember.getWriteMethod() + "(" + memberName + ");");
            } else {
                JavaParameter javaParameter = parameter.getJavaParameter();
                String qname = Names.getBlockQNameName(null, block);
                String memberName;
                if(parameter.getLinkedParameter() != null || javaParameter != null && javaParameter.isHolder())
                    memberName = parameter.getName() + "_holder.value";
                else
                    memberName = parameter.getName();
                p.pln("headerInfo = new SOAPHeaderBlockInfo(" + qname + ", null, false);");
                p.pln("headerInfo.setValue(" + memberName + ");");
                p.pln("headerInfo.setSerializer(" + super.writerFactory.createWriter((SOAPType)block.getType()).serializerMemberName() + ");");
                p.pln("state.getResponse().add(headerInfo);");
            }
        }

        p.pln();
        p.pln("SOAPBlockInfo bodyBlock = new SOAPBlockInfo(" + Names.getBlockQNameName(operation, responseBlock) + ");");
        p.pln("bodyBlock.setValue(" + responseObjName + ");");
        p.pln("bodyBlock.setSerializer(" + super.writerFactory.createWriter(responseBlockType).serializerMemberName() + ");");
        p.pln("state.getResponse().setBody(bodyBlock);");
    }

    protected void writeDocumentOperation(IndentingWriter p, String remoteClassName, Operation operation) throws IOException {
        String messageName = operation.getName().getLocalPart();
        JavaMethod javaMethod = operation.getJavaMethod();
        JavaType resultType = javaMethod.getReturnType();
        String requestObjType = null;
        String requestObjName = null;
        String responseObjType = null;
        String responseObjName = null;
        LiteralType responseBlockType = null;
        LiteralType requestBlockType = null;
        Block responseBlock = null;
        Message requestMessage = operation.getRequest();
        int embeddedParameterCount = 0;
        int nonEmbeddedParameterCount = 0;
        Iterator iterator;
        for(iterator = requestMessage.getParameters(); iterator.hasNext();) {
            Parameter parameter = (Parameter)iterator.next();
            Block paramBlock = parameter.getBlock();
            if(paramBlock.getLocation() == 1) {
                if(parameter.isEmbedded())
                    embeddedParameterCount++;
                else
                    nonEmbeddedParameterCount++;
            } else {
                throw new GeneratorException("generator.internal.error.should.not.happen", "stub.generator.001");
            }
        }

        if(nonEmbeddedParameterCount > 1 || nonEmbeddedParameterCount > 0 && embeddedParameterCount > 0)
            throw new GeneratorException("generator.internal.error.should.not.happen", "stub.generator.002");
        iterator = requestMessage.getBodyBlocks();
        Block requestBlock = null;
        if(iterator.hasNext()) {
            requestBlock = (Block)iterator.next();
            requestBlockType = (LiteralType)requestBlock.getType();
            requestObjType = requestBlockType.getJavaType().getName();
            requestObjName = Names.getTypeMemberName(requestBlockType.getJavaType());
        }
        writeInvokeMethodDecl(p, operation);
        declareRequestObjects(p, requestObjType, requestObjName);
        Message message = operation.getResponse();
        iterator = message.getBodyBlocks();
        if(iterator.hasNext()) {
            responseBlock = (Block)iterator.next();
            responseBlockType = (LiteralType)responseBlock.getType();
            responseObjType = responseBlockType.getJavaType().getName();
            responseObjName = "_response";
        }
        boolean resultIsEmbedded = false;
        if(resultType != null && !resultType.getName().equals(ModelerConstants.VOID_CLASSNAME)) {
            iterator = message.getParameters();
            if(iterator.hasNext()) {
                Parameter parameter = (Parameter)iterator.next();
                if(parameter.isEmbedded()) {
                    resultIsEmbedded = true;
                    p.p(resultType.getName() + " _result = ");
                } else {
                    p.p(responseObjType + " " + responseObjName + " = ");
                }
            }
        }
        p.p("((" + remoteClassName + ") getTarget())." + operation.getJavaMethod().getName() + "(");
        if(nonEmbeddedParameterCount > 0) {
            p.p(requestObjName);
        } else {
            int count = 0;
            for(iterator = requestMessage.getParameters(); iterator.hasNext();) {
                Parameter parameter = (Parameter)iterator.next();
                JavaStructureMember javaMember = StubTieGeneratorBase.getJavaMember(parameter);
                if(count > 0)
                    p.p(", ");
                if(javaMember.isPublic())
                    p.p(requestObjName + "." + parameter.getName());
                else
                    p.p(requestObjName + "." + javaMember.getReadMethod() + "()");
                count++;
            }

        }
        p.pln(");");
        p.pln();
        if(resultIsEmbedded) {
            p.pln(responseObjType + " " + responseObjName + " = new " + responseObjType + "();");
            iterator = message.getParameters();
            Parameter parameter = (Parameter)iterator.next();
            JavaStructureMember javaMember = StubTieGeneratorBase.getJavaMember(parameter);
            if(javaMember.isPublic())
                p.pln(responseObjName + "." + parameter.getName() + " = _result;");
            else
                p.pln(responseObjName + "." + javaMember.getWriteMethod() + "(_result);");
        }
        p.pln();
        p.pln("SOAPBlockInfo bodyBlock = new SOAPBlockInfo(" + Names.getBlockQNameName(operation, responseBlock) + ");");
        p.pln("bodyBlock.setValue(" + responseObjName + ");");
        String serializer = super.writerFactory.createWriter(responseBlockType).serializerMemberName();
        p.pln("bodyBlock.setSerializer(" + serializer + ");");
        p.pln("state.getResponse().setBody(bodyBlock);");
        p.pOln("}");
    }

    protected void writeReadFirstBodyElementDefault(IndentingWriter p, String opCode) throws IOException {
        p.pln("throw new SOAPProtocolViolationException(\"soap.operation.unrecognized\", Integer.toString(" + opCode + "));");
    }

    protected void writeStaticMembers(IndentingWriter p, Map headerMap) throws IOException {
        super.writeStaticMembers(p, headerMap);
    }
}
