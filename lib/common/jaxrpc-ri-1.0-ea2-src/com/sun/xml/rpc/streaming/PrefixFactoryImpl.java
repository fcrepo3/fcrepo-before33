// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   PrefixFactoryImpl.java

package com.sun.xml.rpc.streaming;

import java.util.HashMap;
import java.util.Map;

// Referenced classes of package com.sun.xml.rpc.streaming:
//            PrefixFactory

public class PrefixFactoryImpl
    implements PrefixFactory {

    private String _base;
    private int _next;
    private Map _cachedUriToPrefixMap;

    public PrefixFactoryImpl(String base) {
        _base = base;
        _next = 1;
    }

    public String getPrefix(String uri) {
        String prefix = null;
        if(_cachedUriToPrefixMap == null)
            _cachedUriToPrefixMap = new HashMap();
        else
            prefix = (String)_cachedUriToPrefixMap.get(uri);
        if(prefix == null) {
            prefix = _base + Integer.toString(_next++);
            _cachedUriToPrefixMap.put(uri, prefix);
        }
        return prefix;
    }
}
