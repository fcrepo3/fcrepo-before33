// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LocalizableMessageFactory.java

package com.sun.xml.rpc.util.localization;


// Referenced classes of package com.sun.xml.rpc.util.localization:
//            LocalizableMessage, Localizable

public class LocalizableMessageFactory {

    protected String _bundlename;

    public LocalizableMessageFactory(String bundlename) {
        _bundlename = bundlename;
    }

    public Localizable getMessage(String key) {
        return getMessage(key, (Object[])null);
    }

    public Localizable getMessage(String key, String arg) {
        return getMessage(key, new Object[] {
            arg
        });
    }

    public Localizable getMessage(String key, Localizable localizable) {
        return getMessage(key, new Object[] {
            localizable
        });
    }

    public Localizable getMessage(String key, Object args[]) {
        return new LocalizableMessage(_bundlename, key, args);
    }
}
