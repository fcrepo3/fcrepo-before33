// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   WSDLConstants.java

package com.sun.xml.rpc.wsdl.document;

import javax.xml.rpc.namespace.QName;

public interface WSDLConstants {

    public static final String NS_XMLNS = "http://www.w3.org/2000/xmlns/";
    public static final String NS_WSDL = "http://schemas.xmlsoap.org/wsdl/";
    public static final QName QNAME_BINDING = new QName("http://schemas.xmlsoap.org/wsdl/", "binding");
    public static final QName QNAME_DEFINITIONS = new QName("http://schemas.xmlsoap.org/wsdl/", "definitions");
    public static final QName QNAME_DOCUMENTATION = new QName("http://schemas.xmlsoap.org/wsdl/", "documentation");
    public static final QName QNAME_FAULT = new QName("http://schemas.xmlsoap.org/wsdl/", "fault");
    public static final QName QNAME_IMPORT = new QName("http://schemas.xmlsoap.org/wsdl/", "import");
    public static final QName QNAME_INPUT = new QName("http://schemas.xmlsoap.org/wsdl/", "input");
    public static final QName QNAME_MESSAGE = new QName("http://schemas.xmlsoap.org/wsdl/", "message");
    public static final QName QNAME_OPERATION = new QName("http://schemas.xmlsoap.org/wsdl/", "operation");
    public static final QName QNAME_OUTPUT = new QName("http://schemas.xmlsoap.org/wsdl/", "output");
    public static final QName QNAME_PART = new QName("http://schemas.xmlsoap.org/wsdl/", "part");
    public static final QName QNAME_PORT = new QName("http://schemas.xmlsoap.org/wsdl/", "port");
    public static final QName QNAME_PORT_TYPE = new QName("http://schemas.xmlsoap.org/wsdl/", "portType");
    public static final QName QNAME_SERVICE = new QName("http://schemas.xmlsoap.org/wsdl/", "service");
    public static final QName QNAME_TYPES = new QName("http://schemas.xmlsoap.org/wsdl/", "types");
    public static final QName QNAME_ATTR_ARRAY_TYPE = new QName("http://schemas.xmlsoap.org/wsdl/", "arrayType");

}
