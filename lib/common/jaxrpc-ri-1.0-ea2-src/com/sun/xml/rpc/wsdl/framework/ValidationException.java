// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ValidationException.java

package com.sun.xml.rpc.wsdl.framework;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

public class ValidationException extends JAXRPCExceptionBase {

    public ValidationException(String key) {
        super(key);
    }

    public ValidationException(String key, String arg) {
        super(key, arg);
    }

    public ValidationException(String key, Localizable localizable) {
        super(key, localizable);
    }

    public ValidationException(String key, Object args[]) {
        super(key, args);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.wsdl";
    }
}
