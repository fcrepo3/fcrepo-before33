// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   InternalTypeMappingRegistryImpl.java

package com.sun.xml.rpc.encoding;

import javax.xml.rpc.encoding.Deserializer;
import javax.xml.rpc.encoding.Serializer;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            InternalTypeMappingRegistryImpl

public class InternalTypeMappingRegistryImpl$Row {

    String encoding;
    Class javaType;
    QName xmlType;
    Serializer serializer;
    Deserializer deserializer;

    InternalTypeMappingRegistryImpl$Row(String encoding, Class javaType, QName xmlType) {
        this(encoding, javaType, xmlType, null, null);
    }

    InternalTypeMappingRegistryImpl$Row(String encoding, Class javaType, QName xmlType, Serializer serializer, Deserializer deserializer) {
        if(encoding == null)
            throw new IllegalArgumentException("encoding may not be null");
        if(javaType == null && xmlType == null) {
            throw new IllegalArgumentException("javaType and xmlType may not both be null");
        } else {
            this.encoding = encoding;
            this.javaType = javaType;
            this.xmlType = xmlType;
            this.serializer = serializer;
            this.deserializer = deserializer;
            return;
        }
    }

    static InternalTypeMappingRegistryImpl$Row createNull() {
        return new InternalTypeMappingRegistryImpl$Row();
    }

    private InternalTypeMappingRegistryImpl$Row() {
        encoding = null;
        javaType = null;
        xmlType = null;
        serializer = null;
        deserializer = null;
    }

    public String getEncoding() {
        return encoding;
    }

    public Class getJavaType() {
        return javaType;
    }

    public QName getXMLType() {
        return xmlType;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public Deserializer getDeserializer() {
        return deserializer;
    }
}
