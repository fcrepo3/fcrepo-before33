// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ServiceGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.ProcessorAction;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.config.HandlerChainInfo;
import com.sun.xml.rpc.processor.config.HandlerInfo;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Service;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.processor.util.BatchEnvironment;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.processor.util.StringUtils;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.localization.Localizable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.xml.rpc.namespace.QName;
import sun.tools.java.ClassFile;
import sun.tools.java.Environment;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            GeneratorException, Names, GeneratorBase, GeneratorUtil

public class ServiceGenerator
    implements ProcessorAction {

    private File sourceDir;
    private BatchEnvironment env;
    private Model model;

    public ServiceGenerator() {
        sourceDir = null;
        env = null;
        model = null;
    }

    public void perform(Model model, Configuration config, Properties properties) {
        BatchEnvironment env = config.getEnvironment();
        String key = "sourceDirectory";
        String dirPath = properties.getProperty(key);
        key = "encodeTypes";
        File sourceDir = new File(dirPath);
        ServiceGenerator generator = new ServiceGenerator(env, sourceDir, model);
        generator.doGeneration();
    }

    private ServiceGenerator(BatchEnvironment env, File sourceDir, Model model) {
        this.env = env;
        this.model = model;
        this.sourceDir = sourceDir;
    }

    private void doGeneration() {
        Names.resetPrefixFactory();
        Service service = null;
        try {
            for(Iterator iter = model.getServices(); iter.hasNext(); generateService(service))
                service = (Service)iter.next();

        }
        catch(IOException ioexception) {
            fail("generator.cant.write", service.getName().getLocalPart());
        }
        finally {
            sourceDir = null;
            env = null;
        }
    }

    private void generateService(Service service) throws IOException {
        try {
            JavaInterface intf = service.getJavaInterface();
            String serializerRegistryName = Names.serializerRegistryClassName(intf);
            String className = Names.interfaceImplClassName(intf);
            log("creating service: " + className);
            String interfaceName = Names.customJavaTypeClassName(intf);
            File classFile = Names.sourceFileForClass(className, className, sourceDir, env);
            env.addGeneratedFile(classFile);
            IndentingWriter out = new IndentingWriter(new OutputStreamWriter(new FileOutputStream(classFile)));
            GeneratorBase.writePackage(out, className);
            writeImports(out);
            out.pln();
            writeClassDecl(out, className, interfaceName);
            writeStaticMembers(out, service);
            out.pln();
            writeConstructor(out, className, service, serializerRegistryName);
            out.pln();
            writeGenericGetPortMethods(out, service);
            out.pln();
            writeIndividualGetPorts(out, service.getPorts());
            out.pOln("}");
            out.close();
            env.parseFile(new ClassFile(classFile));
        }
        catch(Exception e) {
            throw new GeneratorException("generator.nestedGeneratorError", new LocalizableExceptionAdapter(e));
        }
    }

    private void writeImports(IndentingWriter p) throws IOException {
        p.pln("import com.sun.xml.rpc.encoding.*;");
        p.pln("import com.sun.xml.rpc.client.ServiceExceptionImpl;");
        p.pln("import com.sun.xml.rpc.util.exception.*;");
        p.pln("import javax.xml.rpc.*;");
        p.pln("import javax.xml.rpc.encoding.*;");
        p.pln("import javax.xml.rpc.handler.HandlerChain;");
        p.pln("import javax.xml.rpc.handler.HandlerInfo;");
        p.pln("import javax.xml.rpc.namespace.QName;");
    }

    private void writeClassDecl(IndentingWriter p, String className, String interfaceName) throws IOException {
        p.plnI("public class " + Names.mangleClass(className) + " extends com.sun.xml.rpc.client.ServiceImpl" + " implements " + Names.mangleClass(interfaceName) + " {");
    }

    private void writeStaticMembers(IndentingWriter p, Service service) throws IOException {
        p.p("private static final QName serviceName = ");
        GeneratorUtil.writeNewQName(p, service.getName());
        p.pln(";");
        String portClass;
        for(Iterator ports = service.getPorts(); ports.hasNext(); p.pln("private static final Class " + StringUtils.decapitalize(Names.stripQualifier(portClass)) + "_PortClass = " + portClass + ".class;")) {
            Port port = (Port)ports.next();
            portClass = port.getJavaInterface().getName();
            p.p("private static final QName " + Names.getQNameName(port.getName()) + " = ");
            GeneratorUtil.writeNewQName(p, port.getName());
            p.pln(";");
        }

    }

    private void writeConstructor(IndentingWriter p, String className, Service service, String serializerRegistryName) throws IOException {
        p.plnI("public " + Names.mangleClass(className) + "() {");
        p.plnI("super(serviceName, new QName[] {");
        p.pI(3);
        Iterator eachPort = service.getPorts();
        for(int i = 0; eachPort.hasNext(); i++) {
            Port port = (Port)eachPort.next();
            if(i > 0)
                p.pln(",");
            p.p(Names.getQNameName(port.getName()));
        }

        p.pln();
        p.pOln("},");
        p.pO(2);
        p.pln("new " + serializerRegistryName + "().getRegistry());");
        p.pO();
        eachPort = service.getPorts();
        if(eachPort.hasNext()) {
            p.pln();
            p.pln("HandlerChain handlerChain = null;");
            while(eachPort.hasNext())  {
                Port port = (Port)eachPort.next();
                p.pln("handlerChain = handlerRegistry.getHandlerChain(" + Names.getQNameName(port.getName()) + ");");
                HandlerChainInfo portClientHandlers = port.getClientHandlerChainInfo();
                for(Iterator eachHandler = portClientHandlers.getHandlers(); eachHandler.hasNext(); p.pOln("}")) {
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

            }
        }
        p.pOln("}");
    }

    private void writeGenericGetPortMethods(IndentingWriter p, Service service) throws IOException {
        Iterator ports = service.getPorts();
        p.plnI("public java.rmi.Remote getPort(QName portName, Class serviceDefInterface) throws ServiceException {");
        p.plnI("try {");
        for(; ports.hasNext(); p.pOln("}")) {
            Port port = (Port)ports.next();
            String portClass = port.getJavaInterface().getName();
            p.plnI("if (portName.equals(" + Names.getQNameName(port.getName()) + ") &&");
            p.pln("serviceDefInterface.equals(" + StringUtils.decapitalize(Names.stripQualifier(portClass)) + "_PortClass)) {");
            p.pln("return get" + Names.stripQualifier(portClass) + "();");
        }

        p.pOlnI("} catch (Exception e) {");
        p.pln("throw new ServiceExceptionImpl(new LocalizableExceptionAdapter(e));");
        p.pOln("}");
        p.pln("return super.getPort(portName, serviceDefInterface);");
        p.pOln("}");
        p.pln();
        ports = service.getPorts();
        p.plnI("public java.rmi.Remote getPort(Class serviceDefInterface) throws ServiceException {");
        p.plnI("try {");
        for(; ports.hasNext(); p.pOln("}")) {
            Port port = (Port)ports.next();
            String portClass = port.getJavaInterface().getName();
            p.plnI("if (serviceDefInterface.equals(" + StringUtils.decapitalize(Names.stripQualifier(portClass)) + "_PortClass)) {");
            p.pln("return get" + Names.stripQualifier(portClass) + "();");
        }

        p.pOlnI("} catch (Exception e) {");
        p.pln("throw new ServiceExceptionImpl(new LocalizableExceptionAdapter(e));");
        p.pOln("}");
        p.pln("return super.getPort(serviceDefInterface);");
        p.pOln("}");
    }

    private void writeIndividualGetPorts(IndentingWriter p, Iterator ports) throws IOException {
        for(; ports.hasNext(); p.pOln("}")) {
            Port port = (Port)ports.next();
            String portClass = port.getJavaInterface().getName();
            p.plnI("public " + portClass + " get" + Names.stripQualifier(portClass) + "() {");
            p.pln(Names.stubFor(portClass) + " stub = new " + Names.stubFor(portClass) + "(handlerRegistry.getHandlerChain(" + Names.getQNameName(port.getName()) + "));");
            p.plnI("try {");
            p.pln("stub._initialize(super.internalRegistry);");
            p.pOlnI("} catch (JAXRPCException e) {");
            p.pln("throw e;");
            p.pOlnI("} catch (Exception e) {");
            p.pln("throw new JAXRPCException(e.getMessage(), e);");
            p.pOln("}");
            p.pln("return stub;");
        }

    }

    private void log(String msg) {
        if(env.verbose())
            System.out.println("[" + Names.stripQualifier(getClass().getName()) + ": " + msg + "]");
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
