// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   HandlerInfo.java

package com.sun.xml.rpc.processor.config;

import java.util.HashMap;
import java.util.Map;

public class HandlerInfo {

    private String handlerClassName;
    private Map properties;

    public HandlerInfo() {
        properties = new HashMap();
    }

    public String getHandlerClassName() {
        return handlerClassName;
    }

    public void setHandlerClassName(String s) {
        handlerClassName = s;
    }

    public Map getProperties() {
        return properties;
    }
}
