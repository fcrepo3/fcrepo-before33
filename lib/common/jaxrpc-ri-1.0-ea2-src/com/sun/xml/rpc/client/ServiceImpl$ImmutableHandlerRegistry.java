// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ServiceImpl.java

package com.sun.xml.rpc.client;

import javax.xml.rpc.handler.HandlerChain;
import javax.xml.rpc.handler.HandlerRegistry;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.client:
//            HandlerChainImpl, ServiceImpl

public class ServiceImpl$ImmutableHandlerRegistry
    implements HandlerRegistry {

    HandlerRegistry registry;

    public ServiceImpl$ImmutableHandlerRegistry(HandlerRegistry registry) {
        this.registry = registry;
    }

    public HandlerChain getHandlerChain(QName portName) {
        HandlerChainImpl chain = (HandlerChainImpl)registry.getHandlerChain(portName);
        chain.beImmutable();
        return chain;
    }

    public void setHandlerChain(QName portName, HandlerChain chain) {
        throw new UnsupportedOperationException();
    }
}
