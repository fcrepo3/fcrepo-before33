// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   TypeMappingImpl.java

package com.sun.xml.rpc.encoding;

import javax.xml.rpc.encoding.DeserializerFactory;
import javax.xml.rpc.encoding.SerializerFactory;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            TypeMappingDescriptor, TypeMappingImpl

public class TypeMappingImpl$Row
    implements TypeMappingDescriptor {

    Class javaType;
    QName xmlType;
    SerializerFactory serializerFactory;
    DeserializerFactory deserializerFactory;

    TypeMappingImpl$Row(Class javaType, QName xmlType, SerializerFactory sf, DeserializerFactory dsf) {
        if(javaType == null)
            throw new IllegalArgumentException("javaType may not be null");
        if(xmlType == null)
            throw new IllegalArgumentException("xmlType may not be null");
        if(sf == null)
            throw new IllegalArgumentException("serializerFactory may not be null");
        if(dsf == null) {
            throw new IllegalArgumentException("deserializerFactory may not be null");
        } else {
            this.javaType = javaType;
            this.xmlType = xmlType;
            serializerFactory = sf;
            deserializerFactory = dsf;
            return;
        }
    }

    TypeMappingImpl$Row() {
        javaType = null;
        xmlType = null;
        serializerFactory = null;
        deserializerFactory = null;
    }

    public Class getJavaType() {
        return javaType;
    }

    public QName getXMLType() {
        return xmlType;
    }

    public SerializerFactory getSerializer() {
        return serializerFactory;
    }

    public DeserializerFactory getDeserializer() {
        return deserializerFactory;
    }
}
