// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   TypeMappingUtil.java

package com.sun.xml.rpc.encoding;

import javax.xml.rpc.encoding.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            TypeMappingException

public class TypeMappingUtil {

    public TypeMappingUtil() {
    }

    public static TypeMapping getTypeMapping(TypeMappingRegistry registry, String encodingStyle) throws Exception {
        TypeMapping mapping = registry.getTypeMapping(encodingStyle);
        if(mapping == null)
            throw new TypeMappingException("typemapping.noMappingForEncoding", encodingStyle);
        else
            return mapping;
    }

    public static Serializer getSerializer(TypeMapping mapping, Class javaType, QName xmlType) throws Exception {
        SerializerFactory sf = mapping.getSerializer(javaType, xmlType);
        if(sf == null)
            throw new TypeMappingException("typemapping.serializerNotRegistered", new Object[] {
                javaType, xmlType
            });
        else
            return sf.getSerializerAs("http://java.sun.com/jax-rpc-ri/1.0/streaming/");
    }

    public static Deserializer getDeserializer(TypeMapping mapping, Class javaType, QName xmlType) throws Exception {
        DeserializerFactory df = mapping.getDeserializer(javaType, xmlType);
        if(df == null)
            throw new TypeMappingException("typemapping.deserializerNotRegistered", new Object[] {
                javaType, xmlType
            });
        else
            return df.getDeserializerAs("http://java.sun.com/jax-rpc-ri/1.0/streaming/");
    }
}
