// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XMLReaderException.java

package com.sun.xml.rpc.streaming;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

public class XMLReaderException extends JAXRPCExceptionBase {

    public XMLReaderException(String key) {
        super(key);
    }

    public XMLReaderException(String key, String arg) {
        super(key, arg);
    }

    public XMLReaderException(String key, Object args[]) {
        super(key, args);
    }

    public XMLReaderException(String key, Localizable arg) {
        super(key, arg);
    }

    public XMLReaderException(Localizable arg) {
        super("xmlreader.nestedError", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.streaming";
    }
}
