// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   TypeMappingException.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

public class TypeMappingException extends JAXRPCExceptionBase {

    public TypeMappingException(String key) {
        super(key);
    }

    public TypeMappingException(String key, String arg) {
        super(key, arg);
    }

    public TypeMappingException(String key, Object args[]) {
        super(key, args);
    }

    public TypeMappingException(String key, Localizable arg) {
        super(key, arg);
    }

    public TypeMappingException(Localizable arg) {
        super("typemapping.nested.exception", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.encoding";
    }
}
