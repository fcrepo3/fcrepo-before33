// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   HTTPConstants.java

package com.sun.xml.rpc.wsdl.document.http;

import javax.xml.rpc.namespace.QName;

public interface HTTPConstants {

    public static final String NS_WSDL_HTTP = "http://schemas.xmlsoap.org/wsdl/http/";
    public static final QName QNAME_ADDRESS = new QName("http://schemas.xmlsoap.org/wsdl/http/", "address");
    public static final QName QNAME_BINDING = new QName("http://schemas.xmlsoap.org/wsdl/http/", "binding");
    public static final QName QNAME_OPERATION = new QName("http://schemas.xmlsoap.org/wsdl/http/", "operation");
    public static final QName QNAME_URL_ENCODED = new QName("http://schemas.xmlsoap.org/wsdl/http/", "urlEncoded");
    public static final QName QNAME_URL_REPLACEMENT = new QName("http://schemas.xmlsoap.org/wsdl/http/", "urlReplacement");

}
