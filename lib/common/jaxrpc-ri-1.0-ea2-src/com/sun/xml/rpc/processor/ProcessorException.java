// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ProcessorException.java

package com.sun.xml.rpc.processor;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

public class ProcessorException extends JAXRPCExceptionBase {

    public ProcessorException(String key) {
        super(key);
    }

    public ProcessorException(String key, String arg) {
        super(key, arg);
    }

    public ProcessorException(String key, Object args[]) {
        super(key, args);
    }

    public ProcessorException(String key, Localizable arg) {
        super(key, arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.processor";
    }
}
