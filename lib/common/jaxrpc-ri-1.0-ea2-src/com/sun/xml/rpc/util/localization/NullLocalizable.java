// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   NullLocalizable.java

package com.sun.xml.rpc.util.localization;


// Referenced classes of package com.sun.xml.rpc.util.localization:
//            Localizable

public class NullLocalizable
    implements Localizable {

    protected static NullLocalizable instance = null;
    private String _key;

    public NullLocalizable(String key) {
        _key = key;
    }

    public String getKey() {
        return _key;
    }

    public Object[] getArguments() {
        return null;
    }

    public String getResourceBundleName() {
        return "";
    }

    public static NullLocalizable instance() {
        if(instance == null)
            instance = new NullLocalizable(null);
        return instance;
    }

}
