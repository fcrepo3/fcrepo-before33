// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LocalizableMessage.java

package com.sun.xml.rpc.util.localization;


// Referenced classes of package com.sun.xml.rpc.util.localization:
//            Localizable

public class LocalizableMessage
    implements Localizable {

    protected String _bundlename;
    protected String _key;
    protected Object _args[];

    public LocalizableMessage(String bundlename, String key) {
        this(bundlename, key, (Object[])null);
    }

    public LocalizableMessage(String bundlename, String key, String arg) {
        this(bundlename, key, new Object[] {
            arg
        });
    }

    protected LocalizableMessage(String bundlename, String key, Object args[]) {
        _bundlename = bundlename;
        _key = key;
        _args = args;
    }

    public String getKey() {
        return _key;
    }

    public Object[] getArguments() {
        return _args;
    }

    public String getResourceBundleName() {
        return _bundlename;
    }
}
