// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy

// Source File Name:   SOAPResponseSerializer.java

package com.sun.xml.rpc.encoding.soap;

import com.sun.xml.rpc.encoding.*;
import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.streaming.*;
import java.util.Map;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding.soap:
//            SOAPRequestSerializer, SOAPResponseStructure

public class SOAPResponseSerializer extends SOAPRequestSerializer
    implements Initializable {

    private static final QName RETURN_VALUE_QNAME = new QName("return");
    private static final QName EMPTY_QNAME_ARRAY[] = new QName[0];
    private static final Class EMPTY_CLASS_ARRAY[] = new Class[0];
    protected QName returnXmlType;
    protected Class returnClass;
    protected JAXRPCSerializer returnSerializer;
    protected JAXRPCDeserializer returnDeserializer;

    public SOAPResponseSerializer(QName type, boolean encodeType, boolean isNullable, String encodingStyle, QName parameterNames[], QName parameterXmlTypes[], Class parameterClasses[],
            QName returnXmlType, Class returnClass) {
        super(type, encodeType, isNullable, encodingStyle, parameterNames, parameterXmlTypes, parameterClasses);
        this.returnXmlType = returnXmlType;
        this.returnClass = returnClass;
    }

    public SOAPResponseSerializer(QName type, boolean encodeType, boolean isNullable, String encodingStyle, QName returnXmlType, Class returnClass) {
        this(type, encodeType, isNullable, encodingStyle, EMPTY_QNAME_ARRAY, EMPTY_QNAME_ARRAY, EMPTY_CLASS_ARRAY, returnXmlType, returnClass);
    }

    public SOAPResponseSerializer(QName type, QName parameterNames[], QName parameterXmlTypes[], Class parameterClasses[], QName returnXmlType, Class returnClass) {
        this(type, false, true, "http://schemas.xmlsoap.org/soap/encoding/", parameterNames, parameterXmlTypes, parameterClasses, returnXmlType, returnClass);
    }

    public SOAPResponseSerializer(QName type, QName returnXmlType, Class returnClass) {
        this(type, EMPTY_QNAME_ARRAY, EMPTY_QNAME_ARRAY, EMPTY_CLASS_ARRAY, returnXmlType, returnClass);
    }

    public void initialize(InternalTypeMappingRegistry registry) throws Exception {
        if(super.typeRegistry != null)
            return;
        super.initialize(registry);
        if(returnClass != null || returnXmlType != null) {
            returnSerializer = (JAXRPCSerializer)registry.getSerializer(super.encodingStyle, returnClass, returnXmlType);
            returnDeserializer = (JAXRPCDeserializer)registry.getDeserializer(super.encodingStyle, returnClass, returnXmlType);
        }
    }

    protected void doSerializeInstance(Object instance, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        SOAPResponseStructure response = (SOAPResponseStructure)instance;
        getReturnSerializer(response.returnValue).serialize(response.returnValue, RETURN_VALUE_QNAME, null, writer, context);
        for(int i = 0; i < super.parameterTypes.length; i++) {
            QName parameterName = super.parameterNames[i];
            Object parameter = response.outParameters.get(parameterName);
            getParameterSerializer(i, parameter).serialize(parameter, parameterName, null, writer, context);
        }

    }

    protected Object doDeserialize(SOAPDeserializationState existingState, XMLReader reader, SOAPDeserializationContext context) throws Exception {
        SOAPResponseStructure instance = new SOAPResponseStructure();
        SOAPResponseSerializer$SOAPResponseStructureBuilder builder = null;
        boolean isComplete = true;
        SOAPDeserializationState state = existingState;
        reader.nextElementContent();
        int responseMemberIndex = 0;
        JAXRPCDeserializer returnDeserializer = getReturnDeserializer(reader);
        if(returnDeserializer != null) {
            Object returnedObject = returnDeserializer.deserialize(null, reader, context);
            if(returnedObject instanceof SOAPDeserializationState) {
                if(builder == null)
                    builder = new SOAPResponseSerializer$SOAPResponseStructureBuilder(instance);
                state = ObjectSerializerBase.registerWithMemberState(instance, state, returnedObject, responseMemberIndex, builder);
                isComplete = false;
            } else {
                instance.returnValue = returnedObject;
            }
        }
        for(int i = 0; i < super.parameterTypes.length; i++) {
            reader.nextElementContent();
            QName parameterName = super.parameterNames[i];
            if(reader.getName().equals(parameterName)) {
                Object returnedObject = getParameterDeserializer(i, reader).deserialize(parameterName, reader, context);
                if(returnedObject instanceof SOAPDeserializationState) {
                    if(builder == null)
                        builder = new SOAPResponseSerializer$SOAPResponseStructureBuilder(instance);
                    responseMemberIndex = i + 1;
                    builder.setOutParameterName(responseMemberIndex, parameterName);
                    state = ObjectSerializerBase.registerWithMemberState(instance, state, returnedObject, responseMemberIndex, builder);
                    isComplete = false;
                } else {
                    instance.outParameters.put(parameterName, returnedObject);
                }
            } else {
                throw new DeserializationException("soap.unexpectedElementName", new Object[] {
                    parameterName, reader.getName()
                });
            }
        }

        reader.nextElementContent();
        if (isComplete)
            return instance;
        else
            return state;
    }

    protected JAXRPCSerializer getReturnSerializer(Object returnValue) throws Exception {
        JAXRPCSerializer serializer = returnSerializer;
        if(serializer == null)
            serializer = (JAXRPCSerializer)super.typeRegistry.getSerializer(super.encodingStyle, returnValue.getClass(), returnXmlType);
        return serializer;
    }

    protected JAXRPCDeserializer getReturnDeserializer(XMLReader reader) throws Exception {
        JAXRPCDeserializer deserializer = returnDeserializer;
        if(deserializer == null) {
            QName xmlType = XMLReaderUtil.getQNameValue(reader, XSDConstants.QNAME_XSI_TYPE);
            if(xmlType != null || returnClass != null)
                deserializer = (JAXRPCDeserializer)super.typeRegistry.getDeserializer(super.encodingStyle, returnClass, xmlType);
        }
        return deserializer;
    }

}
