// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   HandlerChainInfo.java

package com.sun.xml.rpc.processor.config;

import java.util.*;

// Referenced classes of package com.sun.xml.rpc.processor.config:
//            HandlerInfo

public class HandlerChainInfo {

    private List handlers;

    public HandlerChainInfo() {
        handlers = new ArrayList();
    }

    public void add(HandlerInfo i) {
        handlers.add(i);
    }

    public Iterator getHandlers() {
        return handlers.iterator();
    }

    public int getHandlersCount() {
        return handlers.size();
    }
}
