// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ClientTransportException.java

package com.sun.xml.rpc.client;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

public class ClientTransportException extends JAXRPCExceptionBase {

    public ClientTransportException(String key) {
        super(key);
    }

    public ClientTransportException(String key, String argument) {
        super(key, argument);
    }

    public ClientTransportException(String key, Object arguments[]) {
        super(key, arguments);
    }

    public ClientTransportException(String key, Localizable argument) {
        super(key, argument);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.client";
    }
}
