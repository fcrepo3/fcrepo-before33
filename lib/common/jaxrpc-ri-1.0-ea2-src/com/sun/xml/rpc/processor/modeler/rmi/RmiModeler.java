// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   RmiModeler.java

package com.sun.xml.rpc.processor.modeler.rmi;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.config.ModelInfo;
import com.sun.xml.rpc.processor.config.RmiInterfaceInfo;
import com.sun.xml.rpc.processor.config.RmiModelInfo;
import com.sun.xml.rpc.processor.config.RmiServiceInfo;
import com.sun.xml.rpc.processor.config.TypeMappingRegistryInfo;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Message;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Parameter;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Request;
import com.sun.xml.rpc.processor.model.Response;
import com.sun.xml.rpc.processor.model.Service;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.processor.model.java.JavaMethod;
import com.sun.xml.rpc.processor.model.java.JavaParameter;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.soap.RPCRequestOrderedStructureType;
import com.sun.xml.rpc.processor.model.soap.RPCResponseStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.modeler.Modeler;
import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.processor.util.StringUtils;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.rpc.namespace.QName;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.Constants;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.Type;
import sun.tools.javac.BatchEnvironment;

// Referenced classes of package com.sun.xml.rpc.processor.modeler.rmi:
//            RmiConstants, RemoteClass, RmiTypeModeler, ExceptionModeler

public class RmiModeler
    implements RmiConstants, Modeler, Constants {

    private final String modelName;
    private final String typeUri;
    private final String wsdlUri;
    private final Iterator services;
    private com.sun.xml.rpc.processor.util.BatchEnvironment env;
    private Map structMap;
    private TypeMappingRegistryInfo typeMappingRegistry;
    private Map messageMap;

    public RmiModeler(RmiModelInfo rmiModelInfo) {
        modelName = rmiModelInfo.getName();
        typeUri = rmiModelInfo.getTypeNamespaceURI();
        wsdlUri = rmiModelInfo.getTargetNamespaceURI();
        services = rmiModelInfo.getRmiServices();
        env = rmiModelInfo.getConfiguration().getEnvironment();
        typeMappingRegistry = rmiModelInfo.getTypeMappingRegistry();
    }

    public Model buildModel() {
        log("creating model: " + modelName);
        Model model = new Model(new QName(null, modelName));
        model.setTargetNamespaceURI(wsdlUri);
        structMap = new HashMap();
        try {
            while(services.hasNext())  {
                messageMap = new HashMap();
                RmiServiceInfo serviceInfo = (RmiServiceInfo)services.next();
                String javaServiceName = StringUtils.capitalize(serviceInfo.getName());
                log("creating service: " + javaServiceName);
                String serviceInterface;
                if(serviceInfo.getJavaPackageName() != null && !serviceInfo.getJavaPackageName().equals(""))
                    serviceInterface = serviceInfo.getJavaPackageName() + "." + javaServiceName;
                else
                    serviceInterface = javaServiceName;
                Service service = new Service(new QName(wsdlUri, javaServiceName), new JavaInterface(serviceInterface, serviceInterface + "Impl"));
                model.addService(service);
                RmiInterfaceInfo interfaceInfo;
                for(Iterator interfaces = serviceInfo.getInterfaces(); interfaces.hasNext(); service.addPort(modelPort(interfaceInfo)))
                    interfaceInfo = (RmiInterfaceInfo)interfaces.next();

                messageMap = null;
            }
        }
        catch(ModelerException e) {
            throw e;
        }
        catch(JAXRPCExceptionBase e) {
            throw new ModelerException(e);
        }
        catch(Exception e) {
            throw new ModelerException(new LocalizableExceptionAdapter(e));
        }
        structMap = null;
        return model;
    }

    private Port modelPort(RmiInterfaceInfo interfaceInfo) {
        Port port = null;
        Identifier implClassName = Identifier.lookup(interfaceInfo.getName());
        implClassName = env.resolvePackageQualifiedName(implClassName);
        implClassName = mangleClass(implClassName);
        ClassDeclaration decl = env.getClassDeclaration(implClassName);
        try {
            ClassDefinition def = decl.getClassDefinition(env);
            RemoteClass remoteClass = RemoteClass.forClass(env, def);
            if(remoteClass == null)
                throw new ModelerException("rmimodeler.invalid.remote.interface", interfaceInfo.getName());
            port = processInterface(remoteClass, interfaceInfo);
        }
        catch(ClassNotFound classnotfound) {
            throw new ModelerException("rmimodeler.class.not.found", implClassName.toString());
        }
        return port;
    }

    private Port processInterface(RemoteClass remoteClass, RmiInterfaceInfo interfaceInfo) {
        String servant = interfaceInfo.getServantName();
        log("creating port: " + remoteClass.getName().toString());
        String portName = remoteClass.getName().toString();
        int idx = portName.lastIndexOf(".");
        if(idx >= 0)
            portName = portName.substring(idx + 1);
        Port port = new Port(new QName(wsdlUri, portName));
        JavaInterface javaInterface = new JavaInterface(remoteClass.getName().toString(), servant);
        port.setJavaInterface(javaInterface);
        ClassDefinition remoteInterfaces[] = remoteClass.getRemoteInterfaces();
        for(int i = 0; i < remoteInterfaces.length; i++) {
            String interfaceName = remoteInterfaces[i].getName().toString();
            if(!interfaceName.equals(javaInterface.getName()))
                javaInterface.addInterface(interfaceName);
        }

        RemoteClass$Method methods[] = remoteClass.getRemoteMethods();
        for(int i = 0; i < methods.length; i++)
            port.addOperation(processMethod(interfaceInfo, remoteClass, methods[i]));

        port.setClientHandlerChainInfo(interfaceInfo.getClientHandlerChainInfo());
        port.setServerHandlerChainInfo(interfaceInfo.getServerHandlerChainInfo());
        return port;
    }

    private String getStructName(String name) {
        String tmp = name.toLowerCase();
        Integer count = (Integer)structMap.get(tmp);
        if(count != null) {
            count = new Integer(count.intValue() + 1);
            name = name + count;
        } else {
            count = new Integer(0);
        }
        structMap.put(tmp, count);
        return name;
    }

    private Operation processMethod(RmiInterfaceInfo interfaceInfo, RemoteClass remoteClass, RemoteClass$Method method) {
        Type methodType = method.getType();
        Type paramTypes[] = methodType.getArgumentTypes();
        String paramNames[] = nameParameters(paramTypes);
        Type returnType = methodType.getReturnType();
        ClassDeclaration exceptions[] = method.getExceptions();
        ClassDefinition cdef = remoteClass.getClassDefinition();
        String messageName = getMessageName(cdef, method);
        String operationName = getOperationName(messageName);
        String methodName = method.getName().toString();
        log("creating operation: " + methodName);
        Operation operation = new Operation(new QName(wsdlUri, operationName));
        operation.setSOAPAction(getSOAPAction(interfaceInfo, operationName));
        JavaMethod javaMethod = new JavaMethod(methodName);
        operation.setJavaMethod(javaMethod);
        String packageName = cdef.getName().getQualifier().toString();
        if(packageName.length() > 0)
            packageName = packageName + ".";
        log("creating soapstructure: " + packageName + StringUtils.capitalize(methodName) + "_RequestStruct");
        SOAPStructureType paramStruct = new RPCRequestOrderedStructureType(new QName(wsdlUri, StringUtils.capitalize(methodName)));
        paramStruct.setJavaType(new JavaStructureType(getStructName(packageName + paramStruct.getName().getLocalPart() + "_RequestStruct"), false));
        log("creating soapstructure: " + packageName + StringUtils.capitalize(Names.getResponseName(methodName) + "Struct"));
        SOAPStructureType responseStruct = new RPCResponseStructureType(new QName(wsdlUri, StringUtils.capitalize(Names.getResponseName(methodName))));
        responseStruct.setJavaType(new JavaStructureType(getStructName(packageName + StringUtils.capitalize(methodName) + "_ResponseStruct"), false));
        Request request = new Request();
        JavaStructureType javaStructure = (JavaStructureType)paramStruct.getJavaType();
        log("creating block: " + methodName);
        Block block = new Block(new QName(wsdlUri, operationName));
        Parameter parameter;
        JavaParameter javaParameter;
        for(int i = 0; i < paramTypes.length; i++) {
            log("creating soapstructuremember: " + paramNames[i]);
            SOAPStructureMember member = new SOAPStructureMember(new QName(null, paramNames[i]), RmiTypeModeler.modelTypeSOAP(env, typeMappingRegistry, typeUri, paramTypes[i]));
            JavaStructureMember javaMember = new JavaStructureMember(member.getName().getLocalPart(), member.getType().getJavaType(), member, false);
            javaMember.setReadMethod(Names.getJavaMemberReadMethod(javaMember));
            javaMember.setWriteMethod(Names.getJavaMemberWriteMethod(javaMember));
            member.setJavaStructureMember(javaMember);
            javaStructure.add(javaMember);
            paramStruct.add(member);
            log("creating parameter: " + paramNames[i]);
            parameter = new Parameter(paramNames[i]);
            parameter.setEmbedded(true);
            javaParameter = new JavaParameter(paramNames[i], member.getType().getJavaType(), parameter);
            parameter.setJavaParameter(javaParameter);
            parameter.setType(member.getType());
            parameter.setBlock(block);
            javaMethod.addParameter(javaParameter);
            request.addParameter(parameter);
        }

        block.setType(paramStruct);
        request.addBodyBlock(block);
        operation.setRequest(request);
        Response response = new Response();
        javaStructure = (JavaStructureType)responseStruct.getJavaType();
        block = new Block(new QName(wsdlUri, Names.getResponseName(operationName)));
        com.sun.xml.rpc.processor.model.soap.SOAPType resultType = RmiTypeModeler.modelTypeSOAP(env, typeMappingRegistry, typeUri, returnType);
        if(returnType.getTypeCode() != 11) {
            SOAPStructureMember member = new SOAPStructureMember(new QName(null, "result"), resultType);
            JavaStructureMember javaMember = new JavaStructureMember(member.getName().getLocalPart(), member.getType().getJavaType(), member, false);
            javaMember.setReadMethod(Names.getJavaMemberReadMethod(javaMember));
            javaMember.setWriteMethod(Names.getJavaMemberWriteMethod(javaMember));
            member.setJavaStructureMember(javaMember);
            javaStructure.add(javaMember);
            responseStruct.add(member);
        }
        log("creating block result");
        response.addBodyBlock(block);
        log("creating parameter result");
        parameter = new Parameter("result");
        parameter.setEmbedded(true);
        parameter.setType(resultType);
        parameter.setBlock(block);
        block.setType(responseStruct);
        javaParameter = new JavaParameter(null, resultType.getJavaType(), parameter);
        javaMethod.setReturnType(resultType.getJavaType());
        parameter.setJavaParameter(javaParameter);
        response.addParameter(parameter);
        operation.setResponse(response);
        if(exceptions.length > 0) {
            for(int i = 0; i < exceptions.length; i++)
                if(!exceptions[i].getName().toString().equals(RmiConstants.idRemoteException.toString())) {
                    javaMethod.addException(exceptions[i].getName().toString());
                    Fault fault = ExceptionModeler.modelException(env, typeMappingRegistry, typeUri, wsdlUri, exceptions[i]);
                    response.addFaultBlock(fault.getBlock());
                    operation.addFault(fault);
                }

        }
        return operation;
    }

    private static String[] nameParameters(Type types[]) {
        String names[] = new String[types.length];
        for(int i = 0; i < names.length; i++)
            names[i] = generateNameFromType(types[i]) + "_" + (i + 1);

        return names;
    }

    private static String generateNameFromType(Type type) {
        int typeCode = type.getTypeCode();
        switch(typeCode) {
        case 0: // '\0'
        case 1: // '\001'
        case 2: // '\002'
        case 3: // '\003'
        case 4: // '\004'
        case 5: // '\005'
        case 6: // '\006'
        case 7: // '\007'
            return type.toString();

        case 9: // '\t'
            return "arrayOf" + generateNameFromType(type.getElementType());

        case 10: // '\n'
            return mangleClass(type.getClassName().getName()).toString();

        case 8: // '\b'
        default:
            throw new Error("unexpected type code: " + typeCode);
        }
    }

    public String getMessageName(ClassDefinition cdef, RemoteClass$Method method) {
        return method.getName().toString();
    }

    public String getSOAPAction(RmiInterfaceInfo interfaceInfo, String operationName) {
        if(interfaceInfo.getSOAPAction() != null)
            return interfaceInfo.getSOAPAction();
        if(interfaceInfo.getSOAPActionBase() != null)
            return interfaceInfo.getSOAPActionBase() + operationName;
        else
            return "";
    }

    public String getOperationName(String messageName) {
        String operationName = null;
        Integer cnt = (Integer)messageMap.get(messageName);
        if(cnt == null) {
            cnt = new Integer(0);
            operationName = messageName;
        }
        messageMap.put(messageName, new Integer(cnt.intValue() + 1));
        if(operationName == null)
            operationName = messageName + (cnt.intValue() + 1);
        return operationName;
    }

    public static final Identifier mangleClass(Identifier className) {
        if(!className.isInner())
            return className;
        Identifier mangled = Identifier.lookup(className.getFlatName().toString().replace('.', '$'));
        if(mangled.isInner())
            throw new Error("failed to mangle inner class name");
        else
            return Identifier.lookup(className.getQualifier(), mangled);
    }

    private void log(String msg) {
        if(env.verbose())
            System.out.println("[" + msg + "]");
    }
}
