// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPProtocolViolationException.java

package com.sun.xml.rpc.soap.streaming;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

public class SOAPProtocolViolationException extends JAXRPCExceptionBase {

    public SOAPProtocolViolationException(String key) {
        super(key);
    }

    public SOAPProtocolViolationException(String key, String argument) {
        super(key, argument);
    }

    public SOAPProtocolViolationException(String key, Object arguments[]) {
        super(key, arguments);
    }

    public SOAPProtocolViolationException(String key, Localizable argument) {
        super(key, argument);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.soap";
    }
}
