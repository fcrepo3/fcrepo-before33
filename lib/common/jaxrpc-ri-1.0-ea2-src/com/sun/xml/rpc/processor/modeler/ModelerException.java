// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ModelerException.java

package com.sun.xml.rpc.processor.modeler;

import com.sun.xml.rpc.processor.ProcessorException;
import com.sun.xml.rpc.util.localization.Localizable;

public class ModelerException extends ProcessorException {

    public ModelerException(String key) {
        super(key);
    }

    public ModelerException(String key, String arg) {
        super(key, arg);
    }

    public ModelerException(String key, Object args[]) {
        super(key, args);
    }

    public ModelerException(String key, Localizable arg) {
        super(key, arg);
    }

    public ModelerException(Localizable arg) {
        super("modeler.nestedModelError", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.modeler";
    }
}
