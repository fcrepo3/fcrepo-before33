// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Resources.java

package com.sun.xml.rpc.util.localization;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Resources {

    private ResourceBundle _bundle;

    public Resources(String bundleName) throws MissingResourceException {
        _bundle = ResourceBundle.getBundle(bundleName);
    }

    public String getString(String key) {
        return getText(key, null);
    }

    public String getString(String key, String arg) {
        return getText(key, new String[] {
            arg
        });
    }

    public String getString(String key, String args[]) {
        return getText(key, args);
    }

    private String getText(String key, String args[]) {
        if(_bundle == null)
            return "";
        try {
            return MessageFormat.format(_bundle.getString(key), args);
        }
        catch(MissingResourceException missingresourceexception) {
            String msg = "Missing resource: key={0}";
            return MessageFormat.format(msg, new String[] {
                key
            });
        }
    }
}
