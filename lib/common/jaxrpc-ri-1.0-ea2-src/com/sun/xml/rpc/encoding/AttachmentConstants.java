// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   AttachmentConstants.java

package com.sun.xml.rpc.encoding;

import javax.xml.rpc.namespace.QName;

public interface AttachmentConstants {

    public static final String JAXRPC_URI = "http://java.sun.com/jax-rpc-ri/internal";
    public static final QName QNAME_TYPE_IMAGE = new QName("http://java.sun.com/jax-rpc-ri/internal", "image");
    public static final QName QNAME_TYPE_MIME_MULTIPART = new QName("http://java.sun.com/jax-rpc-ri/internal", "multipart");
    public static final QName QNAME_TYPE_SOURCE = new QName("http://java.sun.com/jax-rpc-ri/internal", "text_xml");
    public static final QName QNAME_TYPE_DATA_HANDLER = new QName("http://java.sun.com/jax-rpc-ri/internal", "datahandler");

}
