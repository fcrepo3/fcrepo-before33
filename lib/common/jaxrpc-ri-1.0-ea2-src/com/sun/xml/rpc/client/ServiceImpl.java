// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ServiceImpl.java

package com.sun.xml.rpc.client;

import com.sun.xml.rpc.client.dii.CallImpl;
import com.sun.xml.rpc.client.dii.CallInvocationHandler;
import com.sun.xml.rpc.encoding.*;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.lang.reflect.*;
import java.net.URL;
import java.rmi.Remote;
import java.util.*;
import javax.xml.rpc.*;
import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.rpc.encoding.TypeMappingRegistry;
import javax.xml.rpc.handler.HandlerRegistry;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.client:
//            ServiceExceptionImpl

public class ServiceImpl
    implements Service, SerializerConstants {

    protected QName name;
    protected List ports;
    protected URL wsdlDocumentLocation;
    protected TypeMappingRegistry registry;
    protected InternalTypeMappingRegistry internalRegistry;
    protected Map portInfoMap;
    protected ServiceException registryCreationException;
    protected HandlerRegistry handlerRegistry;

    public ServiceImpl(QName name, TypeMappingRegistry registry) {
        ports = new ArrayList();
        wsdlDocumentLocation = null;
        this.registry = null;
        internalRegistry = null;
        portInfoMap = new HashMap();
        registryCreationException = null;
        this.name = name;
        this.registry = registry;
        registryCreationException = null;
        try {
            internalRegistry = new InternalTypeMappingRegistryImpl(registry);
        }
        catch(JAXRPCExceptionBase e) {
            registryCreationException = new ServiceExceptionImpl(e);
        }
        catch(Exception e) {
            registryCreationException = new ServiceExceptionImpl(new LocalizableExceptionAdapter(e));
        }
        handlerRegistry = new ServiceImpl$HandlerRegistryImpl(this);
    }

    public ServiceImpl(QName name) {
        this(name, createStandardTypeMappingRegistry());
    }

    public ServiceImpl(QName name, QName ports[]) {
        this(name);
        if(ports == null)
            ports = new QName[0];
        for(int i = 0; i < ports.length; i++)
            addPort(ports[i]);

    }

    public ServiceImpl(QName name, QName ports[], TypeMappingRegistry registry) {
        this(name, registry);
        if(ports == null)
            ports = new QName[0];
        for(int i = 0; i < ports.length; i++)
            addPort(ports[i]);

    }

    public ServiceImpl(QName name, Iterator eachPort) {
        this(name);
        for(; eachPort.hasNext(); ports.add((QName)eachPort.next()));
    }

    public ServiceImpl(QName name, URL wsdlLocation) {
        this(name);
        wsdlDocumentLocation = wsdlLocation;
        throw new UnsupportedOperationException();
    }

    public ServiceImpl$PortInfo getPortInfo(QName portName) throws ServiceException {
        if(!ports.contains(portName))
            ports.add(portName);
        ServiceImpl$PortInfo port = (ServiceImpl$PortInfo)portInfoMap.get(portName);
        if(port == null) {
            port = new ServiceImpl$PortInfo();
            portInfoMap.put(portName, port);
        }
        return port;
    }

    protected QName getPortNameForInterface(Class portInterface) {
        return null;
    }

    public Remote getPort(Class portInterface) throws ServiceException {
        QName portName = getPortNameForInterface(portInterface);
        return getPort(portName, portInterface);
    }

    public Remote getPort(QName portName, Class portInterface) throws ServiceException {
        ServiceImpl$PortInfo portInfo = getPortInfo(portName);
        CallInvocationHandler handler = new CallInvocationHandler();
        Method portMethods[] = portInterface.getMethods();
        for(int i = 0; i < portMethods.length; i++) {
            Method currentMethod = portMethods[i];
            if(Modifier.isPublic(currentMethod.getModifiers())) {
                CallImpl methodCall = new CallImpl(internalRegistry, handlerRegistry);
                String methodName = currentMethod.getName();
                ServiceImpl$OperationInfo currentOperation = portInfo.getOperation(methodName);
                String parameterNames[] = currentOperation.getParameterNames();
                QName parameterTypes[] = currentOperation.getParameterXmlTypes();
                Class parameterClasses[] = currentMethod.getParameterTypes();
                methodCall.setPortTypeName(portName);
                methodCall.setOperationName(new QName(currentOperation.getNamespace(), methodName));
                if(parameterNames.length != parameterClasses.length || parameterTypes.length != 0 && parameterTypes.length != parameterNames.length)
                    throw new ServiceExceptionImpl("dii.service.cant.create.proxy.parameter.name.type.mismatch", new Object[] {
                        methodName
                    });
                for(int j = 0; j < parameterNames.length; j++) {
                    String parameterName = parameterNames[j];
                    QName parameterType = parameterTypes == null ? null : parameterTypes[j];
                    Class parameterClass = parameterClasses[j];
                    methodCall.addParameter(parameterName, parameterType, parameterClass, ParameterMode.PARAM_MODE_IN);
                }

                methodCall.setReturnType(currentOperation.getReturnType(), currentMethod.getReturnType());
                methodCall.setTargetEndpointAddress(portInfo.getTargetEndpoint());
                String currentKey;
                for(Iterator eachPropertyKey = currentOperation.getPropertyKeys(); eachPropertyKey.hasNext(); methodCall.setProperty(currentKey, currentOperation.getProperty(currentKey)))
                    currentKey = (String)eachPropertyKey.next();

                handler.addCall(currentMethod, methodCall);
            }
        }

        return (Remote)Proxy.newProxyInstance(portInterface.getClassLoader(), new Class[] {
            portInterface, java.rmi.Remote.class
        }, handler);
    }

    public Remote getPort(Map parameterNameMap, Map parameterTypeMap, String endPointAddress, Class portInterface) throws ServiceException {
        if(parameterTypeMap == null)
            parameterTypeMap = new HashMap();
        CallInvocationHandler handler = new CallInvocationHandler();
        Class portClass = portInterface.getClass();
        Method portMethods[] = portClass.getMethods();
        for(int i = 0; i < portMethods.length; i++) {
            Method currentMethod = portMethods[i];
            if(Modifier.isPublic(currentMethod.getModifiers())) {
                CallImpl methodCall = new CallImpl(internalRegistry, handlerRegistry);
                String methodName = currentMethod.getName();
                methodCall.setOperationName(new QName(methodName));
                String parameterNames[] = (String[])parameterNameMap.get(methodName);
                QName parameterTypes[] = (QName[])parameterTypeMap.get(methodName);
                Class parameterClasses[] = currentMethod.getParameterTypes();
                if(parameterNames.length != parameterClasses.length || parameterTypes != null && parameterTypes.length != parameterNames.length + 1)
                    throw new ServiceExceptionImpl("dii.service.cant.create.proxy.parameter.name.type.mismatch", new Object[] {
                        methodName
                    });
                for(int j = 0; j < parameterNames.length; j++) {
                    String parameterName = parameterNames[j];
                    QName parameterType = parameterTypes == null ? null : parameterTypes[j];
                    Class parameterClass = parameterClasses[j];
                    methodCall.addParameter(parameterName, parameterType, parameterClass, ParameterMode.PARAM_MODE_IN);
                }

                QName parameterType = parameterTypes == null ? null : parameterTypes[parameterTypes.length - 1];
                methodCall.setReturnType(parameterType, currentMethod.getReturnType());
                methodCall.setTargetEndpointAddress(endPointAddress);
                handler.addCall(currentMethod, methodCall);
            }
        }

        return (Remote)Proxy.newProxyInstance(portInterface.getClassLoader(), new Class[] {
            portInterface, java.rmi.Remote.class
        }, handler);
    }

    protected void addPort(QName port) {
        ports.add(port);
    }

    public Call createCall(QName portName) throws ServiceException {
        if(!ports.contains(portName))
            ports.add(portName);
        Call newCall = createCall();
        newCall.setPortTypeName(portName);
        return newCall;
    }

    public Call createCall(QName portName, String operationName) throws ServiceException {
        return createCall(portName, new QName(operationName));
    }

    public Call createCall(QName portName, QName operationName) throws ServiceException {
        Call newCall = createCall(portName);
        newCall.setOperationName(operationName);
        return newCall;
    }

    public Call createCall() throws ServiceException {
        registerSerializers();
        return new CallImpl(internalRegistry, handlerRegistry);
    }

    protected ServiceExceptionImpl portNotFoundException(QName portName) {
        return new ServiceExceptionImpl("dii.service.doesnt.contain.port", new Object[] {
            name, portName
        });
    }

    public QName getServiceName() {
        return name;
    }

    public Iterator getPorts() {
        return ports.iterator();
    }

    public URL getWSDLDocumentLocation() {
        return wsdlDocumentLocation;
    }

    public TypeMappingRegistry getTypeMappingRegistry() {
        return registry;
    }

    public static TypeMappingRegistry createStandardTypeMappingRegistry() {
        TypeMappingRegistry registry = new TypeMappingRegistryImpl();
        TypeMapping soapMappings = createSoapMappings();
        registry.register("http://schemas.xmlsoap.org/soap/encoding/", soapMappings);
        TypeMapping literalMappings = createLiteralMappings();
        registry.register("", literalMappings);
        return registry;
    }

    protected static TypeMapping createSoapMappings() {
        TypeMapping soapMappings = new TypeMappingImpl(StandardTypeMappings.getSoap());
        soapMappings.setSupportedNamespaces(new String[] {
            "http://schemas.xmlsoap.org/soap/encoding/"
        });
        return soapMappings;
    }

    protected static TypeMapping createLiteralMappings() {
        TypeMapping literalMappings = new TypeMappingImpl(StandardTypeMappings.getLiteral());
        literalMappings.setSupportedNamespaces(new String[] {
            ""
        });
        return literalMappings;
    }

    protected void registerSerializers() {
    }

    public HandlerRegistry getHandlerRegistry() {
        return handlerRegistry;
    }
}
