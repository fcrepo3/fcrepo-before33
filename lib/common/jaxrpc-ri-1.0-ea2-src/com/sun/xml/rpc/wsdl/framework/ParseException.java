// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ParseException.java

package com.sun.xml.rpc.wsdl.framework;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

public class ParseException extends JAXRPCExceptionBase {

    public ParseException(String key) {
        super(key);
    }

    public ParseException(String key, String arg) {
        super(key, arg);
    }

    public ParseException(String key, Localizable localizable) {
        super(key, localizable);
    }

    public ParseException(String key, String arg, Localizable localizable) {
        this(key, new Object[] {
            arg, localizable
        });
    }

    public ParseException(String key, Object args[]) {
        super(key, args);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.wsdl";
    }
}
