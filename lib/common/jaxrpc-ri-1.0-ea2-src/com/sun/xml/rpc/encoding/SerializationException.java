// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SerializationException.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

public class SerializationException extends JAXRPCExceptionBase {

    public SerializationException(String key) {
        super(key);
    }

    public SerializationException(String key, String arg) {
        super(key, arg);
    }

    public SerializationException(String key, Object args[]) {
        super(key, args);
    }

    public SerializationException(String key, Localizable arg) {
        super(key, arg);
    }

    public SerializationException(Localizable arg) {
        super("nestedSerializationError", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.encoding";
    }
}
