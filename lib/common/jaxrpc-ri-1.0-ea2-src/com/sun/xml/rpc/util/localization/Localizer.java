// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Localizer.java

package com.sun.xml.rpc.util.localization;

import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.text.MessageFormat;
import java.util.*;

// Referenced classes of package com.sun.xml.rpc.util.localization:
//            Localizable

public class Localizer {

    protected Locale _locale;
    protected HashMap _resourceBundles;

    public Localizer() {
        this(Locale.getDefault());
    }

    public Localizer(Locale l) {
        _locale = l;
        _resourceBundles = new HashMap();
    }

    public String localize(Localizable l) {
        String bundlename = l.getResourceBundleName();
        try {
            ResourceBundle bundle = (ResourceBundle)_resourceBundles.get(bundlename);
            if(bundle == null) {
                try {
                    bundle = ResourceBundle.getBundle(bundlename, _locale);
                }
                catch(MissingResourceException missingresourceexception1) {
                    int i = bundlename.lastIndexOf('.');
                    if(i != -1) {
                        String alternateBundleName = bundlename.substring(i + 1);
                        try {
                            bundle = ResourceBundle.getBundle(alternateBundleName, _locale);
                        }
                        catch(MissingResourceException missingresourceexception3) {
                            return getDefaultMessage(l);
                        }
                    }
                }
                _resourceBundles.put(bundlename, bundle);
            }
            if(bundle == null)
                return getDefaultMessage(l);
            String key = l.getKey();
            if(key == null)
                key = "undefined";
            String msg = null;
            try {
                msg = bundle.getString(key);
            }
            catch(MissingResourceException missingresourceexception2) {
                msg = bundle.getString("undefined");
            }
            Object args[] = l.getArguments();
            if(args != null) {
                for(int i = 0; i < args.length; i++)
                    if(args[i] instanceof Localizable)
                        args[i] = localize((Localizable)args[i]);

            }
            String message = MessageFormat.format(msg, args);
            return message;
        }
        catch(MissingResourceException missingresourceexception) {
            return getDefaultMessage(l);
        }
    }

    protected String getDefaultMessage(Localizable l) {
        String key = l.getKey();
        Object args[] = l.getArguments();
        StringBuffer sb = new StringBuffer();
        if(!(l instanceof LocalizableExceptionAdapter))
            sb.append("[failed to localize] ");
        sb.append(String.valueOf(key));
        if(args != null) {
            sb.append('(');
            for(int i = 0; i < args.length; i++) {
                if(i != 0)
                    sb.append(", ");
                sb.append(String.valueOf(args[i]));
            }

            sb.append(')');
        }
        return sb.toString();
    }
}
