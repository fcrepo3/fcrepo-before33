// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ConfigurationException.java

package com.sun.xml.rpc.processor.config;

import com.sun.xml.rpc.processor.ProcessorException;
import com.sun.xml.rpc.util.localization.Localizable;

public class ConfigurationException extends ProcessorException {

    public ConfigurationException(String key) {
        super(key);
    }

    public ConfigurationException(String key, String arg) {
        super(key, arg);
    }

    public ConfigurationException(String key, Object args[]) {
        super(key, args);
    }

    public ConfigurationException(String key, Localizable arg) {
        super(key, arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.configuration";
    }
}
