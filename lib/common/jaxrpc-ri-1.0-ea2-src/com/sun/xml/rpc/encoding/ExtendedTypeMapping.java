// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ExtendedTypeMapping.java

package com.sun.xml.rpc.encoding;

import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.rpc.namespace.QName;

public interface ExtendedTypeMapping
    extends TypeMapping {

    public abstract Class getJavaType(QName qname);

    public abstract QName getXmlType(Class class1);
}
