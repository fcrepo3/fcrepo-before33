// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy

// Source File Name:   SOAPRequestSerializer.java

package com.sun.xml.rpc.encoding.soap;

import com.sun.xml.rpc.encoding.*;
import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.streaming.*;
import javax.xml.rpc.namespace.QName;

public class SOAPRequestSerializer extends ObjectSerializerBase
    implements Initializable {

    protected QName parameterNames[];
    protected QName parameterTypes[];
    protected Class parameterClasses[];
    protected JAXRPCSerializer serializers[];
    protected JAXRPCDeserializer deserializers[];
    protected InternalTypeMappingRegistry typeRegistry;

    public SOAPRequestSerializer(QName type, boolean encodeType, boolean isNullable, String encodingStyle, QName parameterNames[], QName parameterTypes[], Class parameterClasses[]) {
        super(type, encodeType, isNullable, encodingStyle);
        typeRegistry = null;
        this.parameterNames = parameterNames;
        this.parameterTypes = parameterTypes;
        this.parameterClasses = parameterClasses;
    }

    public SOAPRequestSerializer(QName type, QName parameterNames[], QName parameterTypes[], Class parameterClasses[]) {
        this(type, false, true, "http://schemas.xmlsoap.org/soap/encoding/", parameterNames, parameterTypes, parameterClasses);
    }

    public void initialize(InternalTypeMappingRegistry registry) throws Exception {
        if(typeRegistry != null)
            return;
        serializers = new JAXRPCSerializer[parameterTypes.length];
        deserializers = new JAXRPCDeserializer[parameterTypes.length];
        for(int i = 0; i < parameterTypes.length; i++)
            if(parameterTypes[i] != null || parameterClasses[i] != null) {
                serializers[i] = (JAXRPCSerializer)registry.getSerializer(super.encodingStyle, parameterClasses[i], parameterTypes[i]);
                deserializers[i] = (JAXRPCDeserializer)registry.getDeserializer(super.encodingStyle, parameterClasses[i], parameterTypes[i]);
            } else {
                serializers[i] = null;
                deserializers[i] = null;
            }

        typeRegistry = registry;
    }

    protected void doSerializeInstance(Object instance, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        if(typeRegistry == null)
            throw new EncodingException("initalizable.not.initialized");
        Object parameters[] = (Object[])instance;
        for(int i = 0; i < parameters.length; i++) {
            Object parameter = parameters[i];
            getParameterSerializer(i, parameter).serialize(parameter, getParameterName(i), null, writer, context);
        }

    }

    protected Object doDeserialize(SOAPDeserializationState existingState, XMLReader reader, SOAPDeserializationContext context) throws Exception {
        if(typeRegistry == null)
            throw new EncodingException("initalizable.not.initialized");
        Object instance[] = new Object[parameterTypes.length];
        SOAPRequestSerializer$ParameterArrayBuilder builder = null;
        boolean isComplete = true;
        SOAPDeserializationState state = existingState;
        for(int i = 0; i < parameterTypes.length; i++) {
            reader.nextElementContent();
            QName parameterName = getParameterName(i);
            if(reader.getName().equals(parameterName)) {
                Object parameter = getParameterDeserializer(i, reader).deserialize(parameterName, reader, context);
                if(parameter instanceof SOAPDeserializationState) {
                    if(builder == null)
                        builder = new SOAPRequestSerializer$ParameterArrayBuilder(instance);
                    state = ObjectSerializerBase.registerWithMemberState(((Object) (instance)), state, parameter, i, builder);
                    isComplete = false;
                } else {
                    instance[i] = parameter;
                }
            }
        }

        reader.nextElementContent();
        XMLReaderUtil.verifyReaderState(reader, 2);
        if (isComplete)
            return instance;
        else
            return state;
    }

    protected JAXRPCSerializer getParameterSerializer(int index, Object parameter) throws Exception {
        JAXRPCSerializer serializer = getSerializer(index);
        if(serializer == null)
            serializer = (JAXRPCSerializer)typeRegistry.getSerializer(super.encodingStyle, parameter.getClass(), getParameterType(index));
        return serializer;
    }

    protected JAXRPCDeserializer getParameterDeserializer(int index, XMLReader reader) throws Exception {
        JAXRPCDeserializer deserializer = getDeserializer(index);
        if(deserializer == null) {
            QName parameterXmlType = XMLReaderUtil.getQNameValue(reader, XSDConstants.QNAME_XSI_TYPE);
            deserializer = (JAXRPCDeserializer)typeRegistry.getDeserializer(super.encodingStyle, getParameterClass(index), parameterXmlType);
        }
        return deserializer;
    }

    private Class getParameterClass(int index) {
        if(index < parameterClasses.length)
            return parameterClasses[index];
        else
            return null;
    }

    private QName getParameterType(int index) {
        if(index < parameterTypes.length)
            return parameterTypes[index];
        else
            return null;
    }

    private QName getParameterName(int index) {
        if(index < parameterNames.length)
            return parameterNames[index];
        else
            return null;
    }

    private JAXRPCDeserializer getDeserializer(int index) {
        if(index < deserializers.length)
            return deserializers[index];
        else
            return null;
    }

    private JAXRPCSerializer getSerializer(int index) {
        if(index < serializers.length)
            return serializers[index];
        else
            return null;
    }
}
