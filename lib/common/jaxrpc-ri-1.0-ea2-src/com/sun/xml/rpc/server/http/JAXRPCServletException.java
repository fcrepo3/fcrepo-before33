// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   JAXRPCServletException.java

package com.sun.xml.rpc.server.http;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

public class JAXRPCServletException extends JAXRPCExceptionBase {

    public JAXRPCServletException(String key) {
        super(key);
    }

    public JAXRPCServletException(String key, String arg) {
        super(key, arg);
    }

    public JAXRPCServletException(String key, Object args[]) {
        super(key, args);
    }

    public JAXRPCServletException(String key, Localizable arg) {
        super(key, arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.jaxrpcservlet";
    }
}
