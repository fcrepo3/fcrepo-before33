// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XSDConstants.java

package com.sun.xml.rpc.encoding.xsd;

import javax.xml.rpc.namespace.QName;

public class XSDConstants {

    public static final String URI_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String URI_XSD = "http://www.w3.org/2001/XMLSchema";
    public static final QName QNAME_XSI_TYPE = new QName("http://www.w3.org/2001/XMLSchema-instance", "type");
    public static final QName QNAME_XSI_NIL = new QName("http://www.w3.org/2001/XMLSchema-instance", "nil");

    public XSDConstants() {
    }

}
