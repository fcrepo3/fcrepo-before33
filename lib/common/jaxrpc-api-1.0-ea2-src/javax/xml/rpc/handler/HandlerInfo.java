// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   HandlerInfo.java

package javax.xml.rpc.handler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class HandlerInfo
    implements Serializable {

    private Class handlerClass;
    private Map config;

    public HandlerInfo() {
        handlerClass = null;
        config = new HashMap();
    }

    public HandlerInfo(Class handlerClass, Map config) {
        this.handlerClass = handlerClass;
        this.config = config;
    }

    public void setHandlerClass(Class handlerClass) {
        this.handlerClass = handlerClass;
    }

    public Class getHandlerClass() {
        return handlerClass;
    }

    public void setHandlerConfig(Map config) {
        this.config = config;
    }

    public Map getHandlerConfig() {
        return config;
    }
}
