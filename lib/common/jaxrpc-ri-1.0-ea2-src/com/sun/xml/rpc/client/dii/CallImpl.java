// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   CallImpl.java

package com.sun.xml.rpc.client.dii;

import com.sun.xml.rpc.encoding.*;
import com.sun.xml.rpc.encoding.soap.*;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.rmi.RemoteException;
import java.util.*;
import javax.xml.rpc.Call;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.handler.HandlerChain;
import javax.xml.rpc.handler.HandlerRegistry;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.client.dii:
//            CallInvokerImpl, DynamicInvocationException, CallRequest, CallInvoker

public class CallImpl
    implements Call {

    private static final QName EMPTY_QNAME = new QName("");
    protected static final QName RESULT_QNAME = null;
    protected static final int RETURN_VALUE_INDEX = 0;
    protected static final JAXRPCDeserializer faultDeserializer = new SOAPFaultInfoSerializer(false, false);
    protected static final Set recognizedProperties;
    protected JAXRPCSerializer requestSerializer;
    protected JAXRPCDeserializer responseDeserializer;
    protected List inParameterNames;
    protected List outParameterNames;
    protected List inParameterXmlTypes;
    protected List outParameterXmlTypes;
    protected List inParameterClasses;
    protected List outParameterClasses;
    protected SOAPResponseStructure response;
    protected QName returnXmlType;
    protected Class returnClass;
    protected QName operationName;
    protected QName portTypeName;
    protected String targetEndpointAddress;
    protected Map properties;
    protected InternalTypeMappingRegistry typeRegistry;
    protected CallInvoker invoker;
    protected boolean isParameterAndReturnSpecRequiredFlag;
    protected HandlerRegistry handlerRegistry;

    public CallImpl(InternalTypeMappingRegistry registry, HandlerRegistry handlerRegistry) {
        requestSerializer = null;
        responseDeserializer = null;
        inParameterNames = new ArrayList();
        outParameterNames = new ArrayList();
        inParameterXmlTypes = new ArrayList();
        outParameterXmlTypes = new ArrayList();
        inParameterClasses = new ArrayList();
        outParameterClasses = new ArrayList();
        response = null;
        returnXmlType = null;
        returnClass = null;
        operationName = EMPTY_QNAME;
        portTypeName = EMPTY_QNAME;
        targetEndpointAddress = null;
        properties = new HashMap();
        invoker = new CallInvokerImpl();
        isParameterAndReturnSpecRequiredFlag = true;
        if(registry == null) {
            throw new DynamicInvocationException("dii.typeregistry.missing.in.call");
        } else {
            typeRegistry = new DynamicInternalTypeMappingRegistry(registry);
            this.handlerRegistry = handlerRegistry;
            setProperty("javax.xml.rpc.soap.http.soapaction.use", new Boolean(false));
            return;
        }
    }

    public boolean isParameterAndReturnSpecRequired(QName operation) {
        return isParameterAndReturnSpecRequiredFlag;
    }

    void requireParameterAndReturnSpec() {
        isParameterAndReturnSpecRequiredFlag = true;
    }

    void dontRequireParameterAndReturnSpec() {
        isParameterAndReturnSpecRequiredFlag = false;
    }

    public HandlerChain getHandlerChain() {
        return handlerRegistry.getHandlerChain(portTypeName);
    }

    public void addParameter(String paramName, QName paramXmlType, ParameterMode parameterMode) {
        addParameter(paramName, paramXmlType, null, parameterMode);
    }

    public void addParameter(String paramName, QName paramXmlType, Class paramClass, ParameterMode parameterMode) {
        if(!isParameterAndReturnSpecRequired(operationName))
            throw new DynamicInvocationException("dii.parameterandreturntypespec.not.allowed");
        requestSerializer = null;
        responseDeserializer = null;
        if(parameterMode == ParameterMode.PARAM_MODE_OUT) {
            outParameterNames.add(new QName(paramName));
            outParameterXmlTypes.add(paramXmlType);
            outParameterClasses.add(paramClass);
        } else
        if(parameterMode == ParameterMode.PARAM_MODE_IN) {
            inParameterNames.add(new QName(paramName));
            inParameterXmlTypes.add(paramXmlType);
            inParameterClasses.add(paramClass);
        } else
        if(parameterMode == ParameterMode.PARAM_MODE_INOUT) {
            inParameterNames.add(new QName(paramName));
            inParameterXmlTypes.add(paramXmlType);
            inParameterClasses.add(paramClass);
            outParameterNames.add(new QName(paramName));
            outParameterXmlTypes.add(paramXmlType);
            outParameterClasses.add(paramClass);
        }
    }

    public QName getParameterTypeByName(String parameterName) {
        for(ListIterator eachName = inParameterNames.listIterator(); eachName.hasNext();) {
            String currentName = ((QName)eachName.next()).getLocalPart();
            if(currentName.equals(parameterName))
                return (QName)inParameterXmlTypes.get(eachName.previousIndex());
        }

        for(ListIterator eachName = outParameterNames.listIterator(); eachName.hasNext();) {
            String currentName = ((QName)eachName.next()).getLocalPart();
            if(currentName.equals(parameterName))
                return (QName)outParameterXmlTypes.get(eachName.previousIndex());
        }

        return null;
    }

    public void setReturnType(QName type) {
        setReturnType(type, null);
    }

    public void setReturnType(QName type, Class javaType) {
        if(!isParameterAndReturnSpecRequired(operationName)) {
            throw new DynamicInvocationException("dii.parameterandreturntypespec.not.allowed");
        } else {
            requestSerializer = null;
            responseDeserializer = null;
            returnXmlType = type;
            returnClass = javaType;
            return;
        }
    }

    public QName getReturnType() {
        return returnXmlType;
    }

    public void removeAllParameters() {
        if(!isParameterAndReturnSpecRequired(operationName)) {
            throw new DynamicInvocationException("dii.parameterandreturntypespec.not.allowed");
        } else {
            inParameterNames.clear();
            inParameterXmlTypes.clear();
            inParameterClasses.clear();
            outParameterNames.clear();
            outParameterXmlTypes.clear();
            outParameterClasses.clear();
            return;
        }
    }

    public QName getOperationName() {
        return operationName;
    }

    public void setOperationName(QName operationName) {
        this.operationName = operationName;
    }

    public QName getPortTypeName() {
        return portTypeName;
    }

    public void setPortTypeName(QName portType) {
        portTypeName = portType;
    }

    public void setTargetEndpointAddress(String address) {
        targetEndpointAddress = address;
    }

    public String getTargetEndpointAddress() {
        return targetEndpointAddress;
    }

    public void setProperty(String name, Object value) {
        if(!recognizedProperties.contains(name)) {
            throw new DynamicInvocationException("dii.call.property.set.unrecognized", new Object[] {
                name
            });
        } else {
            properties.put(name, value);
            return;
        }
    }

    public Object getProperty(String name) {
        if(!recognizedProperties.contains(name))
            throw new DynamicInvocationException("dii.call.property.get.unrecognized", new Object[] {
                name
            });
        else
            return properties.get(name);
    }

    public void removeProperty(String name) {
        properties.remove(name);
    }

    public Iterator getPropertyNames() {
        return recognizedProperties.iterator();
    }

    public Object invoke(Object parameters[]) throws RemoteException {
        if(parameters == null)
            parameters = new Object[0];
        try {
            response = getInvoker().doInvoke(new CallRequest(this, parameters), getRequestSerializer(), getResponseDeserializer(), getFaultDeserializer());
        }
        catch(Exception e) {
            if(e instanceof RuntimeException)
                throw (RuntimeException)e;
            else
                throw new RemoteException("", new DynamicInvocationException(new LocalizableExceptionAdapter(e)));
        }
        return response.returnValue;
    }

    public Object invoke(QName operationName, Object inputParams[]) throws RemoteException {
        QName oldopName = getOperationName();
        setOperationName(operationName);
        Object returnValue = invoke(inputParams);
        setOperationName(oldopName);
        return returnValue;
    }

    public void invokeOneWay(Object parameters[]) {
        if(parameters == null)
            parameters = new Object[0];
        try {
            getInvoker().doInvokeOneWay(new CallRequest(this, parameters), getRequestSerializer());
        }
        catch(Exception e) {
            if(e instanceof RuntimeException)
                throw (RuntimeException)e;
            else
                throw new DynamicInvocationException(new LocalizableExceptionAdapter(e));
        }
    }

    public Map getOutputParams() {
        if(response == null)
            throw new DynamicInvocationException("dii.outparameters.not.available");
        else
            return Collections.unmodifiableMap(response.outParameters);
    }

    Object getRequiredProperty(String requiredProperty) {
        Object property = getProperty(requiredProperty);
        if(property == null)
            throw propertyNotFoundException(requiredProperty);
        else
            return property;
    }

    protected CallInvoker getInvoker() {
        return invoker;
    }

    protected JAXRPCSerializer getRequestSerializer() throws Exception {
        if(requestSerializer == null) {
            int parameterCount = inParameterNames.size();
            requestSerializer = new SOAPRequestSerializer(EMPTY_QNAME, (QName[])inParameterNames.toArray(new QName[parameterCount]), (QName[])inParameterXmlTypes.toArray(new QName[parameterCount]), (Class[])inParameterClasses.toArray(new Class[parameterCount]));
            ((Initializable)requestSerializer).initialize(typeRegistry);
        }
        return requestSerializer;
    }

    protected JAXRPCDeserializer getResponseDeserializer() throws Exception {
        if(responseDeserializer == null) {
            boolean beClientSerializer = true;
            int parameterCount = inParameterNames.size();
            responseDeserializer = new SOAPResponseSerializer(EMPTY_QNAME, returnXmlType, returnClass);
            ((Initializable)responseDeserializer).initialize(typeRegistry);
        }
        return responseDeserializer;
    }

    protected JAXRPCDeserializer getFaultDeserializer() {
        return faultDeserializer;
    }

    protected String getEncodingStyle() {
        return (String)getRequiredProperty("javax.xml.rpc.encodingstyle.namespace.uri");
    }

    protected DynamicInvocationException serializerNotFoundException(int index, QName name, Class clazz, QName xmlType) {
        Integer indexObject = new Integer(index);
        if(clazz == null)
            if(xmlType == null)
                return new DynamicInvocationException("dii.parameter.type.underspecified", new Object[] {
                    indexObject, name
                });
            else
                return new DynamicInvocationException("dii.parameter.type.ambiguous.no.class", new Object[] {
                    indexObject, name, xmlType
                });
        if(xmlType == null)
            return new DynamicInvocationException("dii.parameter.type.ambiguous.no.typename", new Object[] {
                indexObject, name, clazz
            });
        else
            return new DynamicInvocationException("dii.parameter.type.unknown", new Object[] {
                indexObject, name, clazz, xmlType
            });
    }

    protected DynamicInvocationException propertyNotFoundException(String property) {
        return new DynamicInvocationException("dii.required.property.not.set", new Object[] {
            property
        });
    }

    static  {
        Set temp = new HashSet();
        temp.add("javax.xml.rpc.security.auth.username");
        temp.add("javax.xml.rpc.security.auth.password");
        temp.add("javax.xml.rpc.service.endpoint.address");
        temp.add("javax.xml.rpc.soap.operation.style");
        temp.add("javax.xml.rpc.soap.http.soapaction.use");
        temp.add("javax.xml.rpc.soap.http.soapaction.uri");
        temp.add("javax.xml.rpc.http.session.maintain");
        temp.add("javax.xml.rpc.encodingstyle.namespace.uri");
        recognizedProperties = Collections.unmodifiableSet(temp);
    }
}
