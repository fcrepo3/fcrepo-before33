// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ServiceImpl.java

package com.sun.xml.rpc.client;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.handler.HandlerChain;
import javax.xml.rpc.handler.HandlerRegistry;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.client:
//            ServiceImpl

public class ServiceImpl$HandlerRegistryImpl
    implements HandlerRegistry {

    private final ServiceImpl this$0; /* synthetic field */

    public ServiceImpl$HandlerRegistryImpl(ServiceImpl this$0) {
        this.this$0 = this$0;
    }

    public HandlerChain getHandlerChain(QName portName) {
        ServiceImpl$PortInfo portInfo = null;
        try {
            portInfo = this$0.getPortInfo(portName);
        }
        catch(ServiceException serviceexception) {
            throw new IllegalArgumentException("Port: " + portName + " is not a valid port");
        }
        return portInfo.getHandlerChain();
    }

    public void setHandlerChain(QName portName, HandlerChain chain) {
        ServiceImpl$PortInfo portInfo = null;
        try {
            portInfo = this$0.getPortInfo(portName);
        }
        catch(ServiceException serviceexception) {
            throw new IllegalArgumentException("Port: " + portName + " is not a valid port");
        }
        portInfo.setHandlerChain(chain);
    }
}
