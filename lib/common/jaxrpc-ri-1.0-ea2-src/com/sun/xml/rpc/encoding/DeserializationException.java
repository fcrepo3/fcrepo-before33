// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   DeserializationException.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

public class DeserializationException extends JAXRPCExceptionBase {

    public DeserializationException(String key) {
        super(key);
    }

    public DeserializationException(String key, String arg) {
        super(key, arg);
    }

    public DeserializationException(String key, Object args[]) {
        super(key, args);
    }

    public DeserializationException(String key, Localizable arg) {
        super(key, arg);
    }

    public DeserializationException(Localizable arg) {
        super("nestedDeserializationError", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.encoding";
    }
}
