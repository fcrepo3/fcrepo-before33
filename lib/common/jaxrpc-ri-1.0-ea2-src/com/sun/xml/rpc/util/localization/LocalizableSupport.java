// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LocalizableSupport.java

package com.sun.xml.rpc.util.localization;


// Referenced classes of package com.sun.xml.rpc.util.localization:
//            Localizable

public class LocalizableSupport {

    protected String key;
    protected Object arguments[];

    public LocalizableSupport(String key) {
        this(key, (Object[])null);
    }

    public LocalizableSupport(String key, String argument) {
        this(key, new Object[] {
            argument
        });
    }

    public LocalizableSupport(String key, Localizable localizable) {
        this(key, new Object[] {
            localizable
        });
    }

    public LocalizableSupport(String key, Object arguments[]) {
        this.key = key;
        this.arguments = arguments;
    }

    public String getKey() {
        return key;
    }

    public Object[] getArguments() {
        return arguments;
    }
}
