// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy

// Source File Name:   StubGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.config.Configuration;
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

public class StubGenerator extends StubTieGeneratorBase {

    private static final String prefix = "_";

    public StubGenerator() {
    }

    public GeneratorBase getGenerator(Model model, Configuration config, Properties properties) {
        return new StubGenerator(model, config, properties);
    }

    private StubGenerator(Model model, Configuration config, Properties properties) {
        super(model, config, properties);
    }

    protected String getClassName() {
        return Names.stubFor(super.port.getJavaInterface().getName());
    }

    protected String getPrefix() {
        return "_";
    }

    protected String getStateType() {
        return "StreamingSenderState";
    }

    protected Message getMessageToDeserialize(Operation operation) {
        return operation.getResponse();
    }

    protected String getStateGetRequestResponseString() {
        return "getResponse";
    }

    protected String getInitializeAccess() {
        return "public";
    }

    protected boolean superClassHasInitialize() {
        return true;
    }

    protected void writeImports(IndentingWriter p) throws IOException {
        super.writeImports(p);
        p.pln("import com.sun.xml.rpc.client.SenderException;");
        p.pln("import com.sun.xml.rpc.client.*;");
        p.pln("import com.sun.xml.rpc.client.http.*;");
        p.pln("import javax.xml.rpc.handler.*;");
        p.pln("import javax.xml.rpc.JAXRPCException;");
        p.pln("import javax.xml.rpc.soap.SOAPFaultException;");
    }

    protected void writeClassDecl(IndentingWriter p, String stubClassName) throws IOException {
        JavaInterface javaInterface = super.port.getJavaInterface();
        p.plnI("public final class " + Names.mangleClass(stubClassName));
        p.pln("extends com.sun.xml.rpc.client.StubBase");
        p.p("implements " + javaInterface.getName());
        Iterator remoteInterfaces = javaInterface.getInterfaces();
        if(remoteInterfaces.hasNext())
            for(; remoteInterfaces.hasNext(); p.p((String)remoteInterfaces.next()))
                p.p(", ");

        p.pln(" {");
        p.pln();
    }

    protected void writeConstructor(IndentingWriter p, String stubClassName) throws IOException {
        p.pln("/*");
        p.pln(" *  public constructor");
        p.pln(" */");
        p.plnI("public " + Names.mangleClass(stubClassName) + "(HandlerChain handlerChain) {");
        p.pln("super(handlerChain);");
        String address = super.port.getAddress();
        if(address != null && address.length() > 0)
            p.pln("_setProperty(ENDPOINT_ADDRESS_PROPERTY, \"" + address + "\");");
        p.pOln("}");
    }

    protected void writeRpcOperation(IndentingWriter p, String remoteClassName, Operation operation) throws IOException, GeneratorException {
        JavaMethod javaMethod = operation.getJavaMethod();
        JavaType resultType = javaMethod.getReturnType();
        declareOperationMethod(p, operation);
        Iterator iterator = javaMethod.getParameters();
        for(int i = 0; iterator.hasNext(); i++) {
            JavaParameter javaParameter = (JavaParameter)iterator.next();
            if(javaParameter.isHolder()) {
                p.plnI("if (" + javaParameter.getName() + " == null) {");
                p.pln("throw new IllegalArgumentException(\"" + javaParameter.getName() + " cannot be null\");");
                p.pOln("}");
            }
        }

        p.plnI("try {");
        Message message = operation.getRequest();
        Block block = null;
        iterator = message.getBodyBlocks();
        if(iterator.hasNext())
            block = (Block)iterator.next();
        SOAPType type = (SOAPType)block.getType();
        String objType = type.getJavaType().getName();
        String objName = "_" + Names.getTypeMemberName(type.getJavaType());
        p.pln();
        QName name = super.port.getName();
        p.pln("StreamingSenderState _state = _start(_handlerChain);");
        p.pln();
        p.pln("InternalSOAPMessage _request = _state.getRequest();");
        p.pln("_request.setOperationCode(" + Names.getOPCodeName(operation.getUniqueName()) + ");");
        p.plnI(objType + " " + objName + " =");
        p.pln("new " + objType + "();");
        p.pO();
        p.pln();
        iterator = message.getParameters();
        boolean declaredHeaderBlockInfo = false;
        while(iterator.hasNext())  {
            Parameter parameter = (Parameter)iterator.next();
            Block paramBlock = parameter.getBlock();
            if(paramBlock.getLocation() == 1) {
                JavaStructureMember javaMember = StubTieGeneratorBase.getJavaMember(parameter);
                String memberName;
                if(parameter.getJavaParameter() != null && parameter.getJavaParameter().isHolder())
                    memberName = parameter.getName() + ".value";
                else
                    memberName = parameter.getName();
                if(javaMember.isPublic())
                    p.pln(objName + "." + javaMember.getName() + " = " + memberName + ";");
                else
                    p.pln(objName + "." + javaMember.getWriteMethod() + "(" + memberName + ");");
            } else {
                if(!declaredHeaderBlockInfo) {
                    p.pln("SOAPHeaderBlockInfo _headerInfo;");
                    declaredHeaderBlockInfo = true;
                }
                JavaParameter javaParameter = parameter.getJavaParameter();
                String qname = Names.getBlockQNameName(null, paramBlock);
                String memberName;
                if(parameter.getLinkedParameter() != null || javaParameter != null && javaParameter.isHolder())
                    memberName = parameter.getName() + ".value";
                else
                    memberName = parameter.getName();
                String serializer = super.writerFactory.createWriter((SOAPType)paramBlock.getType()).serializerMemberName();
                p.pln("_headerInfo = new SOAPHeaderBlockInfo(" + qname + ", null, false);");
                p.pln("_headerInfo.setValue(" + memberName + ");");
                p.pln("_headerInfo.setSerializer(" + serializer + ");");
                p.pln("_request.add(_headerInfo);");
            }
        }
        p.pln();
        p.pln("SOAPBlockInfo _bodyBlock = new SOAPBlockInfo(" + Names.getBlockQNameName(operation, block) + ");");
        p.pln("_bodyBlock.setValue(" + objName + ");");
        p.pln("_bodyBlock.setSerializer(" + super.writerFactory.createWriter(type).serializerMemberName() + ");");
        p.pln("_request.setBody(_bodyBlock);");
        p.pln();
        p.pln("_state.getMessageContext().setProperty(HttpClientTransport.HTTP_SOAPACTION_PROPERTY, \"" + operation.getSOAPAction() + "\");");
        p.pln();
        p.pln("_send((String) _getProperty(ENDPOINT_ADDRESS_PROPERTY), _state);");
        p.pln();
        message = operation.getResponse();
        iterator = message.getBodyBlocks();
        objName = null;
        objType = null;
        block = null;
        while(iterator.hasNext())  {
            block = (Block)iterator.next();
            if(block.getName().getLocalPart().equals(Names.getResponseName(operation.getName().getLocalPart()))) {
                type = (SOAPType)block.getType();
                objType = type.getJavaType().getName();
                objName = "_" + Names.getTypeMemberName(type.getJavaType());
                break;
            }
        }
        p.pln(objType + " " + objName + " = null;");
        String objMemberName = "_responseObj";
        p.pln("Object " + objMemberName + " = _state.getResponse().getBody().getValue();");
        p.plnI("if (" + objMemberName + " instanceof SOAPDeserializationState) {");
        p.plnI(objName + " =");
        p.pln("(" + objType + ")((SOAPDeserializationState)" + objMemberName + ").getInstance();");
        p.pO();
        p.pOlnI("} else {");
        p.plnI(objName + " =");
        p.pln("(" + objType + ")" + objMemberName + ";");
        p.pO();
        p.pOln("}");
        p.pln();
        iterator = message.getParameters();
        boolean hasReturn = resultType != null && !resultType.getName().equals(ModelerConstants.VOID_CLASSNAME);
        while(iterator.hasNext())  {
            Parameter parameter = (Parameter)iterator.next();
            JavaParameter javaParameter = parameter.getJavaParameter();
            Block paramBlock = parameter.getBlock();
            if(javaParameter != null && javaParameter.isHolder() && paramBlock.getLocation() == 1) {
                JavaStructureMember javaMember = StubTieGeneratorBase.getJavaMember(parameter);
                p.plnI(javaParameter.getName() + ".value =");
                if(javaMember.isPublic())
                    p.pln(objName + "." + javaMember.getName() + ";");
                else
                    p.pln(objName + "." + javaMember.getReadMethod() + "();");
                p.pO();
            }
        }
        boolean hasResponseHeaders = false;
        iterator = operation.getResponse().getHeaderBlocks();
        hasResponseHeaders = iterator.hasNext();
        if(hasResponseHeaders) {
            p.pln("Iterator _headers = _state.getResponse().headers();");
            p.pln("SOAPHeaderBlockInfo _curHeader;");
            p.pln("Object _headerObj;");
            p.plnI("while (_headers.hasNext()) {");
            p.pln("_curHeader = (SOAPHeaderBlockInfo)_headers.next();");
            iterator = operation.getResponse().getParameters();
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
                    String varName;
                    if(parameter.getType().getJavaType().isHolder())
                        varName = paramName + ".value";
                    else
                        varName = paramName;
                    p.plnI("if (_curHeader.getName().equals(" + qname + ")) {");
                    p.pln("_headerObj = _curHeader.getValue();");
                    p.plnI("if (_headerObj instanceof SOAPDeserializationState) {");
                    p.pln(paramName + ".value = (" + paramType + ")((SOAPDeserializationState)" + "_headerObj).getInstance();");
                    p.pOlnI("} else {");
                    p.pln(varName + " = (" + paramType + ")_headerObj;");
                    p.pOln("}");
                    p.pO("}");
                }
            }
            if(startedHeaders)
                p.pln();
            p.pOln("}");
        }
        if(hasReturn) {
            iterator = message.getParameters();
            Parameter parameter = (Parameter)iterator.next();
            if(parameter.getBlock() == block) {
                JavaStructureMember javaMember = StubTieGeneratorBase.getJavaMember(parameter);
                if(javaMember.isPublic())
                    p.pln("return " + parameter.getName() + ";");
                else
                    p.pln("return " + objName + "." + javaMember.getReadMethod() + "();");
            }
        }
        p.pO();
        Iterator faults = operation.getFaults();
        writeOperationCatchBlock(p, faults);
        p.pOln("}");
    }

    private void declareOperationMethod(IndentingWriter p, Operation operation) throws IOException {
        JavaMethod javaMethod = operation.getJavaMethod();
        String methodName = javaMethod.getName();
        JavaType resultType = javaMethod.getReturnType();
        p.pln("/*");
        p.pln(" *  implementation of " + methodName);
        p.pln(" */");
        p.p("public " + (resultType == null ? "void" : resultType.getName()) + " " + methodName + "(");
        Iterator iterator = javaMethod.getParameters();
        for(int i = 0; iterator.hasNext(); i++) {
            if(i > 0)
                p.p(", ");
            JavaParameter javaParameter = (JavaParameter)iterator.next();
            if(javaParameter.isHolder())
                p.p(Names.holderClassName(super.port, javaParameter.getType()) + " " + javaParameter.getName());
            else
                p.p(javaParameter.getType().getName() + " " + javaParameter.getName());
        }

        p.plnI(")");
        iterator = javaMethod.getExceptions();
        if(iterator.hasNext()) {
            p.p("throws ");
            for(int i = 0; iterator.hasNext(); i++) {
                if(i > 0)
                    p.p(", ");
                p.p((String)iterator.next());
            }

            p.p(", ");
        } else {
            p.p("throws ");
        }
        p.p("java.rmi.RemoteException");
        p.pln(" {");
        p.pln();
    }

    private void writeOperationCatchBlock(IndentingWriter p, Iterator faults) throws IOException {
        if(faults != null)
            for(; faults.hasNext(); p.pO()) {
                Fault fault = (Fault)faults.next();
                p.plnI("} catch (" + Names.customExceptionClassName(fault) + " e) {");
                p.pln("throw e;");
            }

        p.plnI("} catch (SOAPFaultException e) {");
        p.pln("throw new RemoteException(e.getMessage(), e);");
        p.pOlnI("} catch (RemoteException e) {");
        p.pln("// let this one through unchanged");
        p.pln("throw e;");
        p.pOlnI("} catch (JAXRPCException e) {");
        p.pln("throw new RemoteException(e.getMessage(), e);");
        p.pOlnI("} catch (Exception e) {");
        p.plnI("if (e instanceof RuntimeException) {");
        p.pln("throw (RuntimeException)e;");
        p.pOlnI("} else {");
        p.pln("throw new RemoteException(e.getMessage(), e);");
        p.pOln("}");
        p.pOln("}");
    }

    protected void writeDocumentOperation(IndentingWriter p, String remoteClassName, Operation operation) throws IOException, GeneratorException {
        JavaMethod javaMethod = operation.getJavaMethod();
        JavaType resultType = javaMethod.getReturnType();
        declareOperationMethod(p, operation);
        p.plnI("try {");
        Message message = operation.getRequest();
        Block block = null;
        Iterator iterator = message.getBodyBlocks();
        if(iterator.hasNext())
            block = (Block)iterator.next();
        LiteralType type = (LiteralType)block.getType();
        p.pln();
        p.pln("StreamingSenderState _state = _start(_handlerChain);");
        p.pln();
        p.pln("InternalSOAPMessage _request = _state.getRequest();");
        p.pln("_request.setOperationCode(" + Names.getOPCodeName(operation.getUniqueName()) + ");");
        p.pln();
        String objType = type.getJavaType().getName();
        String objName = "_" + Names.getTypeMemberName(type.getJavaType());
        int embeddedParameterCount = 0;
        int nonEmbeddedParameterCount = 0;
        for(iterator = message.getParameters(); iterator.hasNext();) {
            Parameter parameter = (Parameter)iterator.next();
            Block paramBlock = parameter.getBlock();
            if(paramBlock.getLocation() == 1) {
                if(parameter.isEmbedded()) {
                    embeddedParameterCount++;
                } else {
                    objName = parameter.getJavaParameter().getName();
                    nonEmbeddedParameterCount++;
                }
            } else {
                throw new GeneratorException("generator.internal.error.should.not.happen", "stub.generator.001");
            }
        }

        if(nonEmbeddedParameterCount > 1 || nonEmbeddedParameterCount > 0 && embeddedParameterCount > 0)
            throw new GeneratorException("generator.internal.error.should.not.happen", "stub.generator.002");
        if(embeddedParameterCount > 0 || embeddedParameterCount == 0 && nonEmbeddedParameterCount == 0)
            p.pln(objType + " " + objName + " = new " + objType + "();");
        String paramName = null;
        boolean declaredHeaderBlockInfo = false;
        for(iterator = message.getParameters(); iterator.hasNext();) {
            Parameter parameter = (Parameter)iterator.next();
            Block paramBlock = parameter.getBlock();
            if(paramBlock.getLocation() == 1 && parameter.isEmbedded()) {
                JavaStructureMember javaMember = StubTieGeneratorBase.getJavaMember(parameter);
                if(parameter.getJavaParameter() != null) {
                    String memberName = parameter.getName();
                    if(javaMember.isPublic())
                        p.pln(objName + "." + javaMember.getName() + " = " + memberName + ";");
                    else
                        p.pln(objName + "." + javaMember.getWriteMethod() + "(" + memberName + ");");
                }
            }
        }

        p.pln();
        iterator = message.getBodyBlocks();
        if(iterator.hasNext()) {
            Block paramBlock = (Block)iterator.next();
            p.pln("SOAPBlockInfo _bodyBlock = new SOAPBlockInfo(" + Names.getBlockQNameName(operation, block) + ");");
            p.pln("_bodyBlock.setValue(" + objName + ");");
            String serializer = super.writerFactory.createWriter((LiteralType)paramBlock.getType()).serializerMemberName();
            p.pln("_bodyBlock.setSerializer(" + serializer + ");");
            p.pln("_request.setBody(_bodyBlock);");
            p.pln();
        }
        p.pln("_state.getMessageContext().setProperty(HttpClientTransport.HTTP_SOAPACTION_PROPERTY, \"" + operation.getSOAPAction() + "\");");
        p.pln();
        p.pln("_send((String) _getProperty(ENDPOINT_ADDRESS_PROPERTY), _state);");
        p.pln();
        message = operation.getResponse();
        iterator = message.getBodyBlocks();
        if(iterator.hasNext())
            block = (Block)iterator.next();
        type = (LiteralType)block.getType();
        iterator = message.getParameters();
        if(iterator.hasNext()) {
            Parameter parameter = (Parameter)iterator.next();
            if(parameter.isEmbedded()) {
                objName = "_result";
                objType = type.getJavaType().getName();
            } else {
                objName = "_" + ("result".equals(paramName) ? "result1" : "result");
                objType = parameter.getType().getJavaType().getName();
            }
            p.pln(objType + " " + objName + " = null;");
            p.pln("Object _responseObj = _state.getResponse().getBody().getValue();");
            p.plnI("if (_responseObj instanceof SOAPDeserializationState) {");
            p.p(objName + " =");
            p.pln("(" + objType + ")((SOAPDeserializationState) _responseObj).getInstance();");
            p.pOlnI("} else {");
            p.p(objName + " =");
            p.pln("(" + objType + ") _responseObj;");
            p.pOln("}");
            p.pln();
            if(parameter.isEmbedded()) {
                JavaStructureMember javaMember = StubTieGeneratorBase.getJavaMember(parameter);
                if(javaMember.isPublic())
                    p.pln("return " + objName + "." + parameter.getName() + ";");
                else
                    p.pln("return " + objName + "." + javaMember.getReadMethod() + "();");
            } else {
                p.pln("return " + objName + ";");
            }
        }
        p.pln();
        p.pO();
        writeOperationCatchBlock(p, null);
        p.pOln("}");
    }

    protected void writeReadBodyFaultElement(IndentingWriter p) throws IOException {
        boolean hasFaults = false;
        Operation operation;
        for(Iterator operationsIter = super.operations.iterator(); !hasFaults && operationsIter.hasNext(); hasFaults = operation.getFaults().hasNext())
            operation = (Operation)operationsIter.next();

        if(!hasFaults)
            return;
        p.pln("/*");
        p.pln(" *  this method deserializes fault responses");
        p.pln(" */");
        p.plnI("protected Object _readBodyFaultElement(XMLReader bodyReader, SOAPDeserializationContext deserializationContext, StreamingSenderState state) throws Exception {");
        p.pln("Object faultInfo = null;");
        p.pln("int opcode = state.getRequest().getOperationCode();");
        p.plnI("switch (opcode) {");
        for(Iterator operationsIter = super.operations.iterator(); operationsIter.hasNext();) {
            Operation operation2 = (Operation)operationsIter.next();
            if(operation2.getFaults().hasNext()) {
                p.plnI("case " + Names.getOPCodeName(operation2.getUniqueName()) + ":");
                p.pln("faultInfo = " + Names.getClassMemberName(Names.faultSerializerClassName(super.port, operation2)) + ".deserialize(null, bodyReader, deserializationContext);");
                p.pln("break;");
                p.pO();
            }
        }

        p.plnI("default:");
        p.pln("return super._readBodyFaultElement(bodyReader, deserializationContext, state);");
        p.pO();
        p.pOln("}");
        p.pln("return faultInfo;");
        p.pOln("}");
    }

    protected void writeReadFirstBodyElementDefault(IndentingWriter p, String opCode) throws IOException {
        p.pln("throw new SenderException(\"sender.response.unrecognizedOperation\", Integer.toString(" + opCode + "));");
    }

    public void writeGenericMethods(IndentingWriter p) throws IOException {
        p.pln();
        p.plnI("public String _getEncodingStyle() {");
        p.pln("return SOAPNamespaceConstants.ENCODING;");
        p.pOln("}");
        p.pln();
        p.plnI("public void _setEncodingStyle(String encodingStyle) {");
        p.pln("throw new UnsupportedOperationException(\"cannot set encoding style\");");
        p.pOln("}");
    }
}
