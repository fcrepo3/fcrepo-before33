// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   GenericObjectSerializer.java

package com.sun.xml.rpc.encoding;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            GenericObjectSerializer, JAXRPCSerializer, JAXRPCDeserializer

public class GenericObjectSerializer$MemberInfo {

    QName name;
    QName xmlType;
    Class javaType;
    JAXRPCSerializer serializer;
    JAXRPCDeserializer deserializer;
    GenericObjectSerializer$GetterMethod getter;
    GenericObjectSerializer$SetterMethod setter;

    public GenericObjectSerializer$MemberInfo() {
        name = null;
        xmlType = null;
        javaType = null;
        serializer = null;
        deserializer = null;
        getter = null;
        setter = null;
    }
}
