// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy

// Source File Name:   GeneratorBase.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.ProcessorAction;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriterFactory;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriterFactoryImpl;
import com.sun.xml.rpc.processor.model.*;
import com.sun.xml.rpc.processor.model.literal.*;
import com.sun.xml.rpc.processor.model.soap.*;
import com.sun.xml.rpc.processor.util.BatchEnvironment;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.localization.Localizable;
import com.sun.xml.rpc.util.localization.LocalizableMessageFactory;
import java.io.*;
import java.util.*;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            GeneratorException, GeneratorConstants, Names

public abstract class GeneratorBase
    implements GeneratorConstants, ProcessorAction, ModelVisitor, SOAPTypeVisitor, LiteralTypeVisitor {

    protected File sourceDir;
    protected File destDir;
    protected BatchEnvironment env;
    protected Model model;
    protected Service service;
    protected IndentingWriter out;
    protected boolean encodeTypes;
    protected boolean multiRefEncoding;
    protected SerializerWriterFactory writerFactory;
    protected String serializerNameInfix;
    private LocalizableMessageFactory messageFactory;
    protected Set visitedTypes;

    public GeneratorBase() {
        destDir = null;
        env = null;
        model = null;
        out = null;
        visitedTypes = null;
    }

    public void perform(Model model, Configuration config, Properties properties) {
        BatchEnvironment env = config.getEnvironment();
        String key = "destinationDirectory";
        String dirPath = properties.getProperty(key);
        File destDir = new File(dirPath);
        key = "sourceDirectory";
        String sourcePath = properties.getProperty(key);
        File sourceDir = new File(sourcePath);
        GeneratorBase generator = getGenerator(model, config, properties);
        generator.doGeneration();
    }

    public abstract GeneratorBase getGenerator(Model model1, Configuration configuration, Properties properties);

    protected GeneratorBase(Model model, Configuration config, Properties properties) {
        this.model = model;
        env = config.getEnvironment();
        String key = "destinationDirectory";
        String dirPath = properties.getProperty(key);
        destDir = new File(dirPath);
        key = "sourceDirectory";
        String sourcePath = properties.getProperty(key);
        sourceDir = new File(sourcePath);
        sourceDir = sourceDir;
        destDir = destDir;
        key = "encodeTypes";
        encodeTypes = Boolean.valueOf(properties.getProperty(key)).booleanValue();
        key = "multiRefEncoding";
        multiRefEncoding = Boolean.valueOf(properties.getProperty(key)).booleanValue();
        key = "serializerNameInfix";
        serializerNameInfix = properties.getProperty(key);
        messageFactory = new LocalizableMessageFactory("com.sun.xml.rpc.resources.generator");
    }

    protected void doGeneration() {
        try {
            Names.setSerializerNameInfix(serializerNameInfix);
            model.accept(this);
        }
        catch(Exception e) {
            throw new GeneratorException("generator.nestedGeneratorError", new LocalizableExceptionAdapter(e));
        }
    }

    public void visit(Model model) throws Exception {
        preVisitModel(model);
        visitModel(model);
        postVisitModel(model);
    }

    protected void preVisitModel(Model model1) throws Exception {
    }

    protected void visitModel(Model model) throws Exception {
        Names.resetPrefixFactory();
        writerFactory = new SerializerWriterFactoryImpl();
        for(Iterator services = model.getServices(); services.hasNext(); ((Service)services.next()).accept(this));
    }

    protected void postVisitModel(Model model1) throws Exception {
    }

    public void visit(Service service) throws Exception {
        preVisitService(service);
        visitService(service);
        postVisitService(service);
    }

    protected void preVisitService(Service service1) throws Exception {
    }

    protected void visitService(Service service) throws Exception {
        this.service = service;
        for(Iterator ports = service.getPorts(); ports.hasNext(); ((Port)ports.next()).accept(this));
        this.service = null;
    }

    protected void postVisitService(Service service1) throws Exception {
    }

    public void visit(Port port) throws Exception {
        visitedTypes = new HashSet();
        preVisitPort(port);
        visitPort(port);
        postVisitPort(port);
        visitedTypes = null;
    }

    protected void preVisitPort(Port port1) throws Exception {
    }

    protected void visitPort(Port port) throws Exception {
        for(Iterator operations = port.getOperations(); operations.hasNext(); ((Operation)operations.next()).accept(this));
    }

    protected void postVisitPort(Port port1) throws Exception {
    }

    public void visit(Operation operation) throws Exception {
        preVisitOperation(operation);
        visitOperation(operation);
        postVisitOperation(operation);
    }

    protected void preVisitOperation(Operation operation1) throws Exception {
    }

    protected void visitOperation(Operation operation) throws Exception {
        operation.getRequest().accept(this);
        operation.getResponse().accept(this);
        Fault fault;
        for(Iterator faults = operation.getFaults(); faults.hasNext(); fault.accept(this))
            fault = (Fault)faults.next();

    }

    protected void postVisitOperation(Operation operation1) throws Exception {
    }

    public void visit(Parameter param) throws Exception {
        preVisitParameter(param);
        visitParameter(param);
        postVisitParameter(param);
    }

    protected void preVisitParameter(Parameter parameter) throws Exception {
    }

    protected void visitParameter(Parameter parameter) throws Exception {
    }

    protected void postVisitParameter(Parameter parameter) throws Exception {
    }

    public void visit(Block block) throws Exception {
        preVisitBlock(block);
        visitBlock(block);
        postVisitBlock(block);
    }

    protected void preVisitBlock(Block block1) throws Exception {
    }

    protected void visitBlock(Block block1) throws Exception {
    }

    protected void postVisitBlock(Block block1) throws Exception {
    }

    public void visit(Response response) throws Exception {
        preVisitResponse(response);
        visitResponse(response);
        postVisitResponse(response);
    }

    protected void preVisitResponse(Response response1) throws Exception {
    }

    protected void visitResponse(Response response) throws Exception {
        for(Iterator iter = response.getParameters(); iter.hasNext(); ((Parameter)iter.next()).accept(this));
        Block block;
        for(Iterator iter = response.getBodyBlocks(); iter.hasNext(); responseBodyBlock(block)) {
            block = (Block)iter.next();
            AbstractType type = block.getType();
            if(type.isSOAPType())
                ((SOAPType)type).accept(this);
            else
            if(type.isLiteralType())
                ((LiteralType)type).accept(this);
        }

        Block block2;
        for(Iterator iter = response.getHeaderBlocks(); iter.hasNext(); responseHeaderBlock(block2)) {
            block2 = (Block)iter.next();
            AbstractType type = block2.getType();
            if(type.isSOAPType())
                ((SOAPType)type).accept(this);
            else
            if(type.isLiteralType())
                ((LiteralType)type).accept(this);
        }

    }

    protected void responseBodyBlock(Block block1) throws Exception {
    }

    protected void responseHeaderBlock(Block block1) throws Exception {
    }

    protected void postVisitResponse(Response response1) throws Exception {
    }

    public void visit(Request request) throws Exception {
        preVisitRequest(request);
        visitRequest(request);
        postVisitRequest(request);
    }

    protected void preVisitRequest(Request request1) throws Exception {
    }

    protected void visitRequest(Request request) throws Exception {
        for(Iterator iter = request.getParameters(); iter.hasNext(); ((Parameter)iter.next()).accept(this));
        Block block;
        for(Iterator iter = request.getBodyBlocks(); iter.hasNext(); requestBodyBlock(block)) {
            block = (Block)iter.next();
            AbstractType type = block.getType();
            if(type.isSOAPType())
                ((SOAPType)type).accept(this);
            else
            if(type.isLiteralType())
                ((LiteralType)type).accept(this);
        }

        Block block2;
        for(Iterator iter = request.getHeaderBlocks(); iter.hasNext(); requestHeaderBlock(block2)) {
            block2 = (Block)iter.next();
            AbstractType type = block2.getType();
            if(type.isSOAPType())
                ((SOAPType)type).accept(this);
            else
            if(type.isLiteralType())
                ((LiteralType)type).accept(this);
        }

    }

    protected void requestBodyBlock(Block block1) throws Exception {
    }

    protected void requestHeaderBlock(Block block1) throws Exception {
    }

    protected void postVisitRequest(Request request1) throws Exception {
    }

    public void visit(Fault fault) throws Exception {
        preVisitFault(fault);
        visitFault(fault);
        postVisitFault(fault);
    }

    protected void preVisitFault(Fault fault1) throws Exception {
    }

    protected void visitFault(Fault fault1) throws Exception {
    }

    protected void postVisitFault(Fault fault1) throws Exception {
    }

    public void visit(SOAPCustomType type) throws Exception {
        preVisitSOAPCustomType(type);
        visitSOAPCustomType(type);
        postVisitSOAPCustomType(type);
    }

    protected void preVisitSOAPCustomType(SOAPCustomType soapcustomtype) throws Exception {
    }

    protected void visitSOAPCustomType(SOAPCustomType soapcustomtype) throws Exception {
    }

    protected void postVisitSOAPCustomType(SOAPCustomType soapcustomtype) throws Exception {
    }

    public void visit(SOAPSimpleType type) throws Exception {
        preVisitSOAPSimpleType(type);
        visitSOAPSimpleType(type);
        postVisitSOAPSimpleType(type);
    }

    protected void preVisitSOAPSimpleType(SOAPSimpleType soapsimpletype) throws Exception {
    }

    protected void visitSOAPSimpleType(SOAPSimpleType soapsimpletype) throws Exception {
    }

    protected void postVisitSOAPSimpleType(SOAPSimpleType soapsimpletype) throws Exception {
    }

    public void visit(SOAPAnyType type) throws Exception {
        preVisitSOAPAnyType(type);
        visitSOAPAnyType(type);
        postVisitSOAPAnyType(type);
    }

    protected void preVisitSOAPAnyType(SOAPAnyType soapanytype) throws Exception {
    }

    protected void visitSOAPAnyType(SOAPAnyType soapanytype) throws Exception {
    }

    protected void postVisitSOAPAnyType(SOAPAnyType soapanytype) throws Exception {
    }

    public void visit(SOAPEnumerationType type) throws Exception {
        preVisitSOAPEnumerationType(type);
        visitSOAPEnumerationType(type);
        postVisitSOAPEnumerationType(type);
    }

    protected void preVisitSOAPEnumerationType(SOAPEnumerationType soapenumerationtype) throws Exception {
    }

    protected void visitSOAPEnumerationType(SOAPEnumerationType soapenumerationtype) throws Exception {
    }

    protected void postVisitSOAPEnumerationType(SOAPEnumerationType soapenumerationtype) throws Exception {
    }

    public void visit(SOAPArrayType type) throws Exception {
        preVisitSOAPArrayType(type);
        visitSOAPArrayType(type);
        postVisitSOAPArrayType(type);
    }

    protected void preVisitSOAPArrayType(SOAPArrayType soaparraytype) throws Exception {
    }

    protected void visitSOAPArrayType(SOAPArrayType type) throws Exception {
        SOAPType elemType = type.getElementType();
        elemType.accept(this);
    }

    protected void postVisitSOAPArrayType(SOAPArrayType soaparraytype) throws Exception {
    }

    public void visit(SOAPOrderedStructureType type) throws Exception {
        preVisitSOAPOrderedStructureType(type);
        visitSOAPOrderedStructureType(type);
        postVisitSOAPOrderedStructureType(type);
    }

    protected void preVisitSOAPOrderedStructureType(SOAPOrderedStructureType soaporderedstructuretype) throws Exception {
    }

    protected void visitSOAPOrderedStructureType(SOAPOrderedStructureType type) throws Exception {
        visit(type);
    }

    protected void postVisitSOAPOrderedStructureType(SOAPOrderedStructureType soaporderedstructuretype) throws Exception {
    }

    public void visit(SOAPUnorderedStructureType type) throws Exception {
        preVisitSOAPUnorderedStructureType(type);
        visitSOAPUnorderedStructureType(type);
        postVisitSOAPUnorderedStructureType(type);
    }

    protected void preVisitSOAPUnorderedStructureType(SOAPUnorderedStructureType soapunorderedstructuretype) throws Exception {
    }

    protected void visitSOAPUnorderedStructureType(SOAPUnorderedStructureType type) throws Exception {
        visit(type);
    }

    protected void postVisitSOAPUnorderedStructureType(SOAPUnorderedStructureType soapunorderedstructuretype) throws Exception {
    }

    public void visit(RPCRequestOrderedStructureType type) throws Exception {
        preVisitRPCRequestOrderedStructureType(type);
        visitRPCRequestOrderedStructureType(type);
        postVisitRPCRequestOrderedStructureType(type);
    }

    protected void preVisitRPCRequestOrderedStructureType(RPCRequestOrderedStructureType rpcrequestorderedstructuretype) throws Exception {
    }

    protected void visitRPCRequestOrderedStructureType(RPCRequestOrderedStructureType type) throws Exception {
        visit(type);
    }

    protected void postVisitRPCRequestOrderedStructureType(RPCRequestOrderedStructureType rpcrequestorderedstructuretype) throws Exception {
    }

    public void visit(RPCRequestUnorderedStructureType type) throws Exception {
        preVisitRPCRequestUnorderedStructureType(type);
        visitRPCRequestUnorderedStructureType(type);
        postVisitRPCRequestUnorderedStructureType(type);
    }

    protected void preVisitRPCRequestUnorderedStructureType(RPCRequestUnorderedStructureType rpcrequestunorderedstructuretype) throws Exception {
    }

    protected void visitRPCRequestUnorderedStructureType(RPCRequestUnorderedStructureType type) throws Exception {
        visit(type);
    }

    protected void postVisitRPCRequestUnorderedStructureType(RPCRequestUnorderedStructureType rpcrequestunorderedstructuretype) throws Exception {
    }

    public void visit(RPCResponseStructureType type) throws Exception {
        preVisitRPCResponseStructureType(type);
        visitRPCResponseStructureType(type);
        postVisitRPCResponseStructureType(type);
    }

    protected void preVisitRPCResponseStructureType(RPCResponseStructureType rpcresponsestructuretype) throws Exception {
    }

    protected void visitRPCResponseStructureType(RPCResponseStructureType type) throws Exception {
        visit(type);
    }

    protected void postVisitRPCResponseStructureType(RPCResponseStructureType rpcresponsestructuretype) throws Exception {
    }

    public void visit(SOAPStructureType type) throws Exception {
        if(visitedTypes.contains(type)) {
            return;
        } else {
            visitedTypes.add(type);
            preVisitSOAPStructureType(type);
            visitSOAPStructureType(type);
            postVisitSOAPStructureType(type);
            return;
        }
    }

    protected void preVisitSOAPStructureType(SOAPStructureType soapstructuretype) throws Exception {
    }

    protected void visitSOAPStructureType(SOAPStructureType type) throws Exception {
        SOAPStructureMember member;
        for(Iterator members = type.getMembers(); members.hasNext(); member.getType().accept(this))
            member = (SOAPStructureMember)members.next();

    }

    protected void postVisitSOAPStructureType(SOAPStructureType soapstructuretype) throws Exception {
    }

    public void visit(LiteralSimpleType type) throws Exception {
        preVisitLiteralSimpleType(type);
        visitLiteralSimpleType(type);
        postVisitLiteralSimpleType(type);
    }

    protected void preVisitLiteralSimpleType(LiteralSimpleType literalsimpletype) throws Exception {
    }

    protected void visitLiteralSimpleType(LiteralSimpleType literalsimpletype) throws Exception {
    }

    protected void postVisitLiteralSimpleType(LiteralSimpleType literalsimpletype) throws Exception {
    }

    public void visit(LiteralSequenceType type) throws Exception {
        preVisitLiteralSequenceType(type);
        visitLiteralSequenceType(type);
        postVisitLiteralSequenceType(type);
    }

    protected void preVisitLiteralSequenceType(LiteralSequenceType literalsequencetype) throws Exception {
    }

    protected void visitLiteralSequenceType(LiteralSequenceType literalsequencetype) throws Exception {
    }

    protected void postVisitLiteralSequenceType(LiteralSequenceType literalsequencetype) throws Exception {
    }

    public void visit(LiteralAllType type) throws Exception {
        preVisitLiteralAllType(type);
        visitLiteralAllType(type);
        postVisitLiteralAllType(type);
    }

    protected void preVisitLiteralAllType(LiteralAllType literalalltype) throws Exception {
    }

    protected void visitLiteralAllType(LiteralAllType literalalltype) throws Exception {
    }

    protected void postVisitLiteralAllType(LiteralAllType literalalltype) throws Exception {
    }

    public void visit(LiteralArrayType type) throws Exception {
        preVisitLiteralArrayType(type);
        visitLiteralArrayType(type);
        postVisitLiteralArrayType(type);
    }

    protected void preVisitLiteralArrayType(LiteralArrayType literalarraytype) throws Exception {
    }

    protected void visitLiteralArrayType(LiteralArrayType type) throws Exception {
        type.getElementType().accept(this);
    }

    protected void postVisitLiteralArrayType(LiteralArrayType literalarraytype) throws Exception {
    }

    public void visit(LiteralFragmentType type) throws Exception {
        preVisitLiteralFragmentType(type);
        visitLiteralFragmentType(type);
        postVisitLiteralFragmentType(type);
    }

    protected void preVisitLiteralFragmentType(LiteralFragmentType literalfragmenttype) throws Exception {
    }

    protected void visitLiteralFragmentType(LiteralFragmentType literalfragmenttype) throws Exception {
    }

    protected void postVisitLiteralFragmentType(LiteralFragmentType literalfragmenttype) throws Exception {
    }

    public static void writeWarning(IndentingWriter p) throws IOException {
        p.pln("// Helper class generated by xrpcc, do not edit.");
        p.pln("// Contents subject to change without notice.");
        p.pln();
    }

    public static void writePackage(IndentingWriter p, String classNameStr) throws IOException {
        writeWarning(p);
        writePackageOnly(p, classNameStr);
    }

    public static void writePackageOnly(IndentingWriter p, String classNameStr) throws IOException {
        Identifier className = Identifier.lookup(classNameStr);
        if(className.isQualified()) {
            p.pln("package " + className.getQualifier() + ";");
            p.pln();
        }
    }

    protected void log(String msg) {
        if(env.verbose())
            System.out.println("[" + Names.stripQualifier(getClass().getName()) + ": " + msg + "]");
    }

    protected void warn(String key) {
        env.warn(messageFactory.getMessage(key));
    }

    protected void warn(String key, String arg) {
        env.warn(messageFactory.getMessage(key, arg));
    }

    protected void warn(String key, Object args[]) {
        env.warn(messageFactory.getMessage(key, args));
    }

    protected void info(String key) {
        env.info(messageFactory.getMessage(key));
    }

    protected void info(String key, String arg) {
        env.info(messageFactory.getMessage(key, arg));
    }

    protected void fail(String key) {
        throw new GeneratorException(key);
    }

    protected void fail(String key, String arg) {
        throw new GeneratorException(key, arg);
    }

    protected void fail(String key, String arg1, String arg2) {
        throw new GeneratorException(key, new Object[] {
            arg1, arg2
        });
    }

    protected void fail(Localizable arg) {
        throw new GeneratorException("generator.nestedGeneratorError", arg);
    }

    protected void fail(Throwable arg) {
        throw new GeneratorException("generator.nestedGeneratorError", new LocalizableExceptionAdapter(arg));
    }
}
