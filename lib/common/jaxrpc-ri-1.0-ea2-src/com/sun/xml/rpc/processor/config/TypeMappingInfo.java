// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   TypeMappingInfo.java

package com.sun.xml.rpc.processor.config;

import javax.xml.rpc.namespace.QName;

public class TypeMappingInfo {

    private String encodingStyle;
    private QName xmlType;
    private String javaTypeName;
    private String serializerFactoryName;
    private String deserializerFactoryName;

    public TypeMappingInfo(String encodingStyle, QName xmlType, String javaTypeName, String serializerFactoryName, String deserializerFactoryName) {
        this.encodingStyle = encodingStyle;
        this.xmlType = xmlType;
        this.javaTypeName = javaTypeName;
        this.serializerFactoryName = serializerFactoryName;
        this.deserializerFactoryName = deserializerFactoryName;
    }

    public String getEncodingStyle() {
        return encodingStyle;
    }

    public QName getXMLType() {
        return xmlType;
    }

    public String getJavaTypeName() {
        return javaTypeName;
    }

    public String getSerializerFactoryName() {
        return serializerFactoryName;
    }

    public String getDeserializerFactoryName() {
        return deserializerFactoryName;
    }
}
