// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   InternalTypeMappingRegistry.java

package com.sun.xml.rpc.encoding;

import javax.xml.rpc.encoding.Deserializer;
import javax.xml.rpc.encoding.Serializer;
import javax.xml.rpc.namespace.QName;

public interface InternalTypeMappingRegistry {

    public abstract Serializer getSerializer(String s, Class class1, QName qname) throws Exception;

    public abstract Serializer getSerializer(String s, Class class1) throws Exception;

    public abstract Serializer getSerializer(String s, QName qname) throws Exception;

    public abstract Deserializer getDeserializer(String s, Class class1, QName qname) throws Exception;

    public abstract Deserializer getDeserializer(String s, QName qname) throws Exception;

    public abstract Class getJavaType(String s, QName qname) throws Exception;

    public abstract QName getXmlType(String s, Class class1) throws Exception;
}
