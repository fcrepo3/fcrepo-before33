// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   EncodingException.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

public class EncodingException extends JAXRPCExceptionBase {

    public EncodingException(String key) {
        super(key);
    }

    public EncodingException(String key, String arg) {
        super(key, arg);
    }

    public EncodingException(String key, Object args[]) {
        super(key, args);
    }

    public EncodingException(String key, Localizable arg) {
        super(key, arg);
    }

    public EncodingException(Localizable arg) {
        super("nestedEncodingError", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.encoding";
    }
}
