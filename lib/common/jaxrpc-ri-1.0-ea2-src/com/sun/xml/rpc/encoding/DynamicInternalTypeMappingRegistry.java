// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   DynamicInternalTypeMappingRegistry.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.io.PrintStream;
import java.lang.reflect.*;
import javax.xml.rpc.encoding.Deserializer;
import javax.xml.rpc.encoding.Serializer;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            TypeMappingException, SerializationException, ReferenceableSerializerImpl, PolymorphicArraySerializer, 
//            ValueObjectSerializer, InternalTypeMappingRegistry, SerializerConstants

public class DynamicInternalTypeMappingRegistry
    implements InternalTypeMappingRegistry, SerializerConstants {

    protected InternalTypeMappingRegistry registry;
    protected final QName ELEMENT_NAME = new QName("element");

    public DynamicInternalTypeMappingRegistry(InternalTypeMappingRegistry registry) {
        this.registry = null;
        if(registry == null) {
            throw new IllegalArgumentException("registry must not be null");
        } else {
            this.registry = registry;
            return;
        }
    }

    public Serializer getSerializer(String encoding, Class javaType, QName xmlType) throws Exception {
        try {
            return registry.getSerializer(encoding, javaType, xmlType);
        }
        catch(TypeMappingException ex) {
            try {
                if(encoding.equals("http://schemas.xmlsoap.org/soap/encoding/")) {
                    if(isArray(javaType, xmlType))
                        return createArraySerializer(javaType, xmlType);
                    if(isValueType(javaType))
                        return createValueSerializer(javaType, xmlType);
                }
                System.out.println("Couldn't match: " + javaType + ", " + xmlType);
                throw ex;
            }
            catch(JAXRPCExceptionBase e) {
                throw new SerializationException(e);
            }
            catch(Exception e) {
                throw new SerializationException(new LocalizableExceptionAdapter(e));
            }
        }
    }

    public Deserializer getDeserializer(String encoding, Class javaType, QName xmlType) throws Exception {
        try {
            return registry.getDeserializer(encoding, javaType, xmlType);
        }
        catch(TypeMappingException ex) {
            try {
                if(encoding.equals("http://schemas.xmlsoap.org/soap/encoding/")) {
                    if(isArray(javaType, xmlType))
                        return createArraySerializer(javaType, xmlType);
                    else
                        return createValueSerializer(javaType, xmlType);
                } else {
                    throw ex;
                }
            }
            catch(JAXRPCExceptionBase e) {
                throw new SerializationException(e);
            }
            catch(Exception e) {
                throw new SerializationException(new LocalizableExceptionAdapter(e));
            }
        }
    }

    private ReferenceableSerializerImpl createArraySerializer(Class javaType, QName xmlType) throws Exception {
        if(javaType == null || xmlType == null) {
            return null;
        } else {
            ReferenceableSerializerImpl serializer = new ReferenceableSerializerImpl(false, new PolymorphicArraySerializer(xmlType, false, true, "http://schemas.xmlsoap.org/soap/encoding/", ELEMENT_NAME));
            serializer.initialize(registry);
            return serializer;
        }
    }

    private ReferenceableSerializerImpl createValueSerializer(Class javaType, QName xmlType) throws Exception {
        if(javaType == null || xmlType == null) {
            return null;
        } else {
            ReferenceableSerializerImpl serializer = new ReferenceableSerializerImpl(false, new ValueObjectSerializer(xmlType, false, true, "http://schemas.xmlsoap.org/soap/encoding/", javaType));
            serializer.initialize(registry);
            return serializer;
        }
    }

    public Serializer getSerializer(String encoding, Class javaType) throws Exception {
        return registry.getSerializer(encoding, javaType);
    }

    public Serializer getSerializer(String encoding, QName xmlType) throws Exception {
        return registry.getSerializer(encoding, xmlType);
    }

    public Deserializer getDeserializer(String encoding, QName xmlType) throws Exception {
        return registry.getDeserializer(encoding, xmlType);
    }

    public Class getJavaType(String encoding, QName xmlType) throws Exception {
        return registry.getJavaType(encoding, xmlType);
    }

    public QName getXmlType(String encoding, Class javaType) throws Exception {
        return registry.getXmlType(encoding, javaType);
    }

    public static boolean isArray(Class javaType, QName xmlType) {
        return javaType != null && javaType.isArray() && SOAPConstants.QNAME_ENCODING_ARRAY.equals(xmlType);
    }

    public static boolean isValueType(Class javaType) throws Exception {
        if(javaType == null || (java.rmi.Remote.class).isAssignableFrom(javaType))
            return false;
        boolean hasPublicConstructor = false;
        Constructor constructors[] = javaType.getConstructors();
        for(int i = 0; i < constructors.length; i++) {
            if(constructors[i].getParameterTypes().length != 0)
                continue;
            hasPublicConstructor = true;
            break;
        }

        if(!hasPublicConstructor)
            return false;
        boolean hasPropertiesOrPublicFields = false;
        if(Introspector.getBeanInfo(javaType).getPropertyDescriptors().length == 0) {
            Field fields[] = javaType.getFields();
            for(int i = 0; i < fields.length; i++) {
                Field currentField = fields[i];
                int fieldModifiers = currentField.getModifiers();
                if(!Modifier.isPublic(fieldModifiers) || Modifier.isTransient(fieldModifiers) || Modifier.isFinal(fieldModifiers))
                    continue;
                hasPropertiesOrPublicFields = true;
                break;
            }

        } else {
            hasPropertiesOrPublicFields = true;
        }
        return hasPropertiesOrPublicFields;
    }
}
