// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   GeneratorException.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.ProcessorException;
import com.sun.xml.rpc.util.localization.Localizable;

public class GeneratorException extends ProcessorException {

    public GeneratorException(String key) {
        super(key);
    }

    public GeneratorException(String key, String arg) {
        super(key, arg);
    }

    public GeneratorException(String key, Object args[]) {
        super(key, args);
    }

    public GeneratorException(String key, Localizable arg) {
        super(key, arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.generator";
    }
}
