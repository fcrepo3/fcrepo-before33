// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   HandlerException.java

package com.sun.xml.rpc.client;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

public class HandlerException extends JAXRPCExceptionBase {

    public HandlerException(String key) {
        super(key);
    }

    public HandlerException(String key, String arg) {
        super(key, arg);
    }

    public HandlerException(String key, Object args[]) {
        super(key, args);
    }

    public HandlerException(String key, Localizable arg) {
        super(key, arg);
    }

    public HandlerException(Localizable arg) {
        super("handler.nestedError", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.handler";
    }
}
