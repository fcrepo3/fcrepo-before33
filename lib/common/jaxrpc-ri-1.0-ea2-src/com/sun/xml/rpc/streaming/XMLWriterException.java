// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XMLWriterException.java

package com.sun.xml.rpc.streaming;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

public class XMLWriterException extends JAXRPCExceptionBase {

    public XMLWriterException(String key) {
        super(key);
    }

    public XMLWriterException(String key, String arg) {
        super(key, arg);
    }

    public XMLWriterException(String key, Object args[]) {
        super(key, args);
    }

    public XMLWriterException(String key, Localizable arg) {
        super(key, arg);
    }

    public XMLWriterException(Localizable arg) {
        super("xmlwriter.nestedError", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.streaming";
    }
}
