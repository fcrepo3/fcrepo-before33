// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SenderException.java

package com.sun.xml.rpc.client;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

public class SenderException extends JAXRPCExceptionBase {

    public SenderException(String key) {
        super(key);
    }

    public SenderException(String key, String arg) {
        super(key, arg);
    }

    public SenderException(String key, Object args[]) {
        super(key, args);
    }

    public SenderException(String key, Localizable arg) {
        super(key, arg);
    }

    public SenderException(Localizable arg) {
        super("sender.nestedError", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.sender";
    }
}
