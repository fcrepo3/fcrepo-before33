// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   MIMEConstants.java

package com.sun.xml.rpc.wsdl.document.mime;

import javax.xml.rpc.namespace.QName;

public interface MIMEConstants {

    public static final String NS_WSDL_MIME = "http://schemas.xmlsoap.org/wsdl/mime/";
    public static final QName QNAME_CONTENT = new QName("http://schemas.xmlsoap.org/wsdl/mime/", "content");
    public static final QName QNAME_MULTIPART_RELATED = new QName("http://schemas.xmlsoap.org/wsdl/mime/", "multipartRelated");
    public static final QName QNAME_PART = new QName("http://schemas.xmlsoap.org/wsdl/mime/", "part");
    public static final QName QNAME_MIME_XML = new QName("http://schemas.xmlsoap.org/wsdl/mime/", "mimeXml");

}
