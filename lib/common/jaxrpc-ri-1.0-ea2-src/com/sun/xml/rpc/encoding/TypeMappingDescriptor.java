// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   TypeMappingDescriptor.java

package com.sun.xml.rpc.encoding;

import javax.xml.rpc.encoding.DeserializerFactory;
import javax.xml.rpc.encoding.SerializerFactory;
import javax.xml.rpc.namespace.QName;

public interface TypeMappingDescriptor {

    public abstract Class getJavaType();

    public abstract QName getXMLType();

    public abstract SerializerFactory getSerializer();

    public abstract DeserializerFactory getDeserializer();
}
