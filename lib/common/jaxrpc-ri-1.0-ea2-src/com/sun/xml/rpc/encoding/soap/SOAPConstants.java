// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPConstants.java

package com.sun.xml.rpc.encoding.soap;

import javax.xml.rpc.namespace.QName;

public class SOAPConstants
    implements com.sun.xml.rpc.wsdl.document.soap.SOAPConstants {

    public static final String URI_ENVELOPE = "http://schemas.xmlsoap.org/soap/envelope/";
    public static final String URI_ENCODING = "http://schemas.xmlsoap.org/soap/encoding/";
    public static final String URI_HTTP = "http://schemas.xmlsoap.org/soap/http/";
    public static final QName QNAME_ENCODING_ARRAY;
    public static final QName QNAME_ENCODING_ARRAYTYPE;
    public static final QName QNAME_ENCODING_BASE64;
    public static final QName QNAME_ENVELOPE_ENCODINGSTYLE = new QName("http://schemas.xmlsoap.org/soap/envelope/", "encodingStyle");
    public static final QName QNAME_SOAP_FAULT = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Fault");
    public static final QName FAULT_CODE_CLIENT = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Client");
    public static final QName FAULT_CODE_MUST_UNDERSTAND = new QName("http://schemas.xmlsoap.org/soap/envelope/", "MustUnderstand");
    public static final QName FAULT_CODE_SERVER = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server");
    public static final QName FAULT_CODE_VERSION_MISMATCH = new QName("http://schemas.xmlsoap.org/soap/envelope/", "VersionMismatch");

    public SOAPConstants() {
    }

    static  {
        QNAME_ENCODING_ARRAY = com.sun.xml.rpc.wsdl.document.soap.SOAPConstants.QNAME_TYPE_ARRAY;
        QNAME_ENCODING_ARRAYTYPE = com.sun.xml.rpc.wsdl.document.soap.SOAPConstants.QNAME_ATTR_ARRAY_TYPE;
        QNAME_ENCODING_BASE64 = com.sun.xml.rpc.wsdl.document.soap.SOAPConstants.QNAME_TYPE_BASE64;
    }
}
