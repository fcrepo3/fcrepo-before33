// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ModelException.java

package com.sun.xml.rpc.processor.model;

import com.sun.xml.rpc.processor.ProcessorException;
import com.sun.xml.rpc.util.localization.Localizable;

public class ModelException extends ProcessorException {

    public ModelException(String key) {
        super(key);
    }

    public ModelException(String key, String arg) {
        super(key, arg);
    }

    public ModelException(String key, Object args[]) {
        super(key, args);
    }

    public ModelException(String key, Localizable arg) {
        super(key, arg);
    }

    public ModelException(Localizable arg) {
        super("model.nestedModelError", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.model";
    }
}
