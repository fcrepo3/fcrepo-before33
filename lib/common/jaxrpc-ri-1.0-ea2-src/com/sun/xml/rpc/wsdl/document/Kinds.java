// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Kinds.java

package com.sun.xml.rpc.wsdl.document;

import com.sun.xml.rpc.wsdl.framework.Kind;

public class Kinds {

    public static final Kind BINDING = new Kind("wsdl:binding");
    public static final Kind MESSAGE = new Kind("wsdl:message");
    public static final Kind PORT = new Kind("wsdl:port");
    public static final Kind PORT_TYPE = new Kind("wsdl:portType");
    public static final Kind SERVICE = new Kind("wsdl:service");

    private Kinds() {
    }

}
