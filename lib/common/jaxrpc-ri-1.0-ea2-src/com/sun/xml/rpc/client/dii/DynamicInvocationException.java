// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   DynamicInvocationException.java

package com.sun.xml.rpc.client.dii;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

public class DynamicInvocationException extends JAXRPCExceptionBase {

    public DynamicInvocationException(String key) {
        super(key);
    }

    public DynamicInvocationException(String key, String arg) {
        super(key, arg);
    }

    public DynamicInvocationException(String key, Object args[]) {
        super(key, args);
    }

    public DynamicInvocationException(String key, Localizable arg) {
        super(key, arg);
    }

    public DynamicInvocationException(Localizable arg) {
        super("dii.exception.nested", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.dii";
    }
}
